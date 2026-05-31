package com.example.javalabs.basic;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks success/failure calls against an availability target in a rolling window.
 *
 * <p>Error budgets turn reliability into a measurable number. When the budget is exhausted, a
 * service can reduce risky changes, lower retry volume, or alert operators.</p>
 */
public final class SlaBudgetTracker {

    private final double targetAvailability;
    private final long windowMillis;
    private final TimeSource timeSource;
    private final Deque<CallRecord> calls = new ArrayDeque<>();

    /**
     * Creates a rolling-window SLA budget tracker.
     *
     * @param targetAvailability required success ratio, for example {@code 0.99}
     * @param windowMillis rolling window length in milliseconds
     * @param timeSource clock used to make tests deterministic
     * @throws IllegalArgumentException when the target, window, or clock is invalid
     */
    public SlaBudgetTracker(double targetAvailability, long windowMillis, TimeSource timeSource) {
        if (targetAvailability <= 0 || targetAvailability > 1) {
            throw new IllegalArgumentException("targetAvailability must be greater than 0 and at most 1");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.targetAvailability = targetAvailability;
        this.windowMillis = windowMillis;
        this.timeSource = timeSource;
    }

    /**
     * Records one service call outcome in the current rolling window.
     *
     * @param outcome call outcome to store
     * @throws IllegalArgumentException when {@code outcome} is {@code null}
     */
    public void record(ServiceCallOutcome outcome) {
        if (outcome == null) {
            throw new IllegalArgumentException("outcome must not be null");
        }
        long now = timeSource.currentTimeMillis();
        evictExpired(now);
        calls.addLast(new CallRecord(now, outcome));
    }

    /**
     * Builds a snapshot from retained calls.
     *
     * @return immutable SLA budget snapshot for the current rolling window
     */
    public SlaBudgetSnapshot snapshot() {
        evictExpired(timeSource.currentTimeMillis());
        int totalCalls = calls.size();
        int failedCalls = 0;
        for (CallRecord call : calls) {
            if (call.outcome() == ServiceCallOutcome.FAILURE) {
                failedCalls++;
            }
        }
        return new SlaBudgetSnapshot(totalCalls, failedCalls, allowedFailures(totalCalls));
    }

    /**
     * @return configured target availability ratio
     */
    public double targetAvailability() {
        return targetAvailability;
    }

    /**
     * Calculates how many failures are allowed for the current sample size.
     *
     * @param totalCalls retained call count
     * @return allowed failure count before the budget is exhausted
     */
    private int allowedFailures(int totalCalls) {
        return (int) Math.floor(totalCalls * (1.0 - targetAvailability));
    }

    /**
     * Removes calls outside the rolling window.
     *
     * @param now current timestamp in milliseconds
     */
    private void evictExpired(long now) {
        long cutoff = now - windowMillis;
        while (!calls.isEmpty() && calls.peekFirst().timestampMillis() <= cutoff) {
            calls.removeFirst();
        }
    }

    /**
     * Internal timestamped call sample retained inside the rolling window.
     *
     * @param timestampMillis time when the call was recorded
     * @param outcome success or failure outcome
     */
    private record CallRecord(long timestampMillis, ServiceCallOutcome outcome) {
    }
}
