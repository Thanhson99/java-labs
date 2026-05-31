package com.example.javalabs.basic;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Limits how many retries are allowed in a rolling time window.
 *
 * <p>Retries are useful for temporary failures, but uncontrolled retries can make an outage worse.
 * A retry budget keeps retry traffic bounded while still allowing a small amount of recovery work.</p>
 */
public final class RetryBudget {

    private final int maxRetriesPerWindow;
    private final long windowMillis;
    private final TimeSource timeSource;
    private final Deque<Long> retryTimestamps = new ArrayDeque<>();

    /**
     * Creates a rolling-window retry budget.
     *
     * @param maxRetriesPerWindow maximum retry attempts allowed in one window
     * @param windowMillis rolling window length in milliseconds
     * @param timeSource clock used to make tests deterministic
     * @throws IllegalArgumentException when limits are not positive or {@code timeSource} is {@code null}
     */
    public RetryBudget(int maxRetriesPerWindow, long windowMillis, TimeSource timeSource) {
        if (maxRetriesPerWindow <= 0) {
            throw new IllegalArgumentException("maxRetriesPerWindow must be positive");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.maxRetriesPerWindow = maxRetriesPerWindow;
        this.windowMillis = windowMillis;
        this.timeSource = timeSource;
    }

    /**
     * Attempts to consume one retry slot.
     *
     * @return {@code true} when a retry slot was consumed; {@code false} when the budget is exhausted
     */
    public boolean tryAcquireRetry() {
        long now = timeSource.currentTimeMillis();
        evictExpired(now);
        if (retryTimestamps.size() >= maxRetriesPerWindow) {
            return false;
        }
        retryTimestamps.addLast(now);
        return true;
    }

    /**
     * @return remaining retry slots in the current rolling window
     */
    public int remainingRetries() {
        evictExpired(timeSource.currentTimeMillis());
        return maxRetriesPerWindow - retryTimestamps.size();
    }

    /**
     * @return retry slots already used in the current rolling window
     */
    public int usedRetries() {
        evictExpired(timeSource.currentTimeMillis());
        return retryTimestamps.size();
    }

    /**
     * Removes retry timestamps that are no longer inside the rolling window.
     *
     * @param now current timestamp in milliseconds
     */
    private void evictExpired(long now) {
        long cutoff = now - windowMillis;
        while (!retryTimestamps.isEmpty() && retryTimestamps.peekFirst() <= cutoff) {
            retryTimestamps.removeFirst();
        }
    }
}
