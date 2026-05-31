package com.example.javalabs.basic.metrics;

import com.example.javalabs.basic.TimeSource;

import java.util.Arrays;

/**
 * Tracks recent event throughput with fixed-size time buckets.
 *
 * <p>The window stores only bucket counters instead of every event timestamp. Advancing time clears
 * expired buckets and keeps snapshot reads cheap, which is useful for load shedding, autoscaling,
 * and lightweight request-rate dashboards.</p>
 */
public final class BucketedThroughputWindow {

    private final long bucketSizeMillis;
    private final long[] buckets;
    private final TimeSource timeSource;
    private int currentBucketIndex;
    private long currentBucketStartMillis;
    private long totalEvents;
    private boolean initialized;

    /**
     * Creates a rolling throughput window.
     *
     * @param bucketSizeMillis duration represented by each bucket
     * @param bucketCount number of buckets retained
     * @param timeSource clock used to advance buckets
     * @throws IllegalArgumentException when bucket settings or clock are invalid
     */
    public BucketedThroughputWindow(long bucketSizeMillis, int bucketCount, TimeSource timeSource) {
        if (bucketSizeMillis <= 0) {
            throw new IllegalArgumentException("bucketSizeMillis must be positive");
        }
        if (bucketCount <= 0) {
            throw new IllegalArgumentException("bucketCount must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.bucketSizeMillis = bucketSizeMillis;
        this.buckets = new long[bucketCount];
        this.timeSource = timeSource;
    }

    /**
     * Records one event in the current time bucket.
     *
     * @return throughput snapshot after recording the event
     * @throws IllegalStateException when time moves backwards
     */
    public ThroughputSnapshot record() {
        advanceTo(timeSource.currentTimeMillis());
        buckets[currentBucketIndex]++;
        totalEvents++;
        return snapshot();
    }

    /**
     * Returns the current throughput snapshot after expiring old buckets.
     *
     * @return immutable throughput snapshot for the retained window
     * @throws IllegalStateException when time moves backwards
     */
    public ThroughputSnapshot snapshot() {
        advanceTo(timeSource.currentTimeMillis());
        long windowMillis = windowMillis();
        double eventsPerSecond = totalEvents * 1_000.0 / windowMillis;
        return new ThroughputSnapshot(totalEvents, windowMillis, eventsPerSecond);
    }

    /**
     * Clears all bucket counters while keeping configuration.
     */
    public void clear() {
        Arrays.fill(buckets, 0);
        currentBucketIndex = 0;
        currentBucketStartMillis = 0;
        totalEvents = 0;
        initialized = false;
    }

    /**
     * Returns a defensive copy of bucket counters for inspection and tests.
     *
     * @return retained bucket counts in internal ring order
     */
    public long[] bucketCounts() {
        return Arrays.copyOf(buckets, buckets.length);
    }

    /**
     * Returns the total time range represented by the retained buckets.
     *
     * @return rolling window size in milliseconds
     */
    public long windowMillis() {
        return bucketSizeMillis * buckets.length;
    }

    /**
     * Advances bucket state to the supplied timestamp and clears expired buckets.
     */
    private void advanceTo(long nowMillis) {
        if (!initialized) {
            currentBucketStartMillis = alignToBucketStart(nowMillis);
            initialized = true;
            return;
        }
        if (nowMillis < currentBucketStartMillis) {
            throw new IllegalStateException("time must not move backwards");
        }

        long elapsedBuckets = (nowMillis - currentBucketStartMillis) / bucketSizeMillis;
        if (elapsedBuckets == 0) {
            return;
        }
        if (elapsedBuckets >= buckets.length) {
            Arrays.fill(buckets, 0);
            totalEvents = 0;
            currentBucketIndex = 0;
            currentBucketStartMillis = alignToBucketStart(nowMillis);
            return;
        }

        for (long step = 0; step < elapsedBuckets; step++) {
            currentBucketIndex = (currentBucketIndex + 1) % buckets.length;
            totalEvents -= buckets[currentBucketIndex];
            buckets[currentBucketIndex] = 0;
        }
        currentBucketStartMillis += elapsedBuckets * bucketSizeMillis;
    }

    /**
     * Aligns a timestamp to the beginning of its bucket.
     */
    private long alignToBucketStart(long nowMillis) {
        return Math.floorDiv(nowMillis, bucketSizeMillis) * bucketSizeMillis;
    }
}
