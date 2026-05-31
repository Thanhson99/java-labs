package com.example.javalabs.basic.metrics;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks latency statistics for the most recent fixed-size window of samples.
 *
 * <p>The implementation uses a ring buffer for bounded memory, a running total for constant-time
 * averages, and monotonic queues for constant-time min/max reads. This is a common optimization
 * pattern when metrics should stay cheap under high request volume.</p>
 */
public final class RollingLatencyWindow {

    private final long[] values;
    private final Deque<Sample> minCandidates = new ArrayDeque<>();
    private final Deque<Sample> maxCandidates = new ArrayDeque<>();
    private int nextIndex;
    private int size;
    private long nextSequence;
    private long totalMillis;

    /**
     * Creates a bounded rolling latency window.
     *
     * @param capacity maximum number of recent samples retained
     * @throws IllegalArgumentException when {@code capacity} is not positive
     */
    public RollingLatencyWindow(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.values = new long[capacity];
    }

    /**
     * Records one latency sample and evicts the oldest sample when the window is full.
     *
     * @param latencyMillis latency value in milliseconds
     * @throws IllegalArgumentException when {@code latencyMillis} is negative
     */
    public void record(long latencyMillis) {
        if (latencyMillis < 0) {
            throw new IllegalArgumentException("latencyMillis must not be negative");
        }

        if (size == values.length) {
            totalMillis -= values[nextIndex];
        } else {
            size++;
        }

        long sequence = nextSequence++;
        values[nextIndex] = latencyMillis;
        nextIndex = (nextIndex + 1) % values.length;
        totalMillis += latencyMillis;

        addMinCandidate(sequence, latencyMillis);
        addMaxCandidate(sequence, latencyMillis);
        removeExpiredCandidates();
    }

    /**
     * Returns the current rolling latency summary.
     *
     * @return immutable snapshot of the retained samples
     */
    public LatencyWindowSnapshot snapshot() {
        if (size == 0) {
            return LatencyWindowSnapshot.empty();
        }
        return new LatencyWindowSnapshot(
                size,
                (double) totalMillis / size,
                minCandidates.peekFirst().value(),
                maxCandidates.peekFirst().value()
        );
    }

    /**
     * Removes all retained samples and resets the rolling statistics.
     */
    public void clear() {
        nextIndex = 0;
        size = 0;
        nextSequence = 0;
        totalMillis = 0;
        minCandidates.clear();
        maxCandidates.clear();
    }

    /**
     * Returns the number of samples currently retained.
     *
     * @return retained sample count
     */
    public int size() {
        return size;
    }

    /**
     * Returns the maximum number of samples this window retains.
     *
     * @return configured capacity
     */
    public int capacity() {
        return values.length;
    }

    /**
     * Adds a candidate to the increasing queue used for minimum lookup.
     */
    private void addMinCandidate(long sequence, long latencyMillis) {
        while (!minCandidates.isEmpty() && minCandidates.peekLast().value() >= latencyMillis) {
            minCandidates.removeLast();
        }
        minCandidates.addLast(new Sample(sequence, latencyMillis));
    }

    /**
     * Adds a candidate to the decreasing queue used for maximum lookup.
     */
    private void addMaxCandidate(long sequence, long latencyMillis) {
        while (!maxCandidates.isEmpty() && maxCandidates.peekLast().value() <= latencyMillis) {
            maxCandidates.removeLast();
        }
        maxCandidates.addLast(new Sample(sequence, latencyMillis));
    }

    /**
     * Drops candidates whose sequence number has fallen outside the retained window.
     */
    private void removeExpiredCandidates() {
        long oldestSequence = nextSequence - size;
        while (!minCandidates.isEmpty() && minCandidates.peekFirst().sequence() < oldestSequence) {
            minCandidates.removeFirst();
        }
        while (!maxCandidates.isEmpty() && maxCandidates.peekFirst().sequence() < oldestSequence) {
            maxCandidates.removeFirst();
        }
    }

    /**
     * Internal latency sample tagged with a monotonic sequence number.
     *
     * @param sequence insertion sequence used to evict stale queue candidates
     * @param value latency value in milliseconds
     */
    private record Sample(long sequence, long value) {
    }
}
