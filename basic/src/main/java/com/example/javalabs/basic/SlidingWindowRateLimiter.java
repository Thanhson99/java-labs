package com.example.javalabs.basic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Sliding-window rate limiter that tracks recent request timestamps per key.
 *
 * <p>Compared with a fixed window, this limiter reduces boundary bursts because every decision is
 * based on the last {@code windowSizeMillis} of activity instead of a shared window start time.</p>
 */
public final class SlidingWindowRateLimiter {

    private final int maxRequests;
    private final long windowSizeMillis;
    private final TimeSource timeSource;
    private final Map<String, Deque<Long>> requestsByKey = new HashMap<>();

    /**
     * Creates a limiter that allows at most {@code maxRequests} per sliding window.
     *
     * @param maxRequests maximum allowed requests per key
     * @param windowSizeMillis rolling window size in milliseconds
     * @param timeSource clock used to evaluate request age
     * @throws IllegalArgumentException when limits are not positive or {@code timeSource} is null
     */
    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMillis, TimeSource timeSource) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowSizeMillis <= 0) {
            throw new IllegalArgumentException("windowSizeMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.timeSource = timeSource;
    }

    /**
     * Attempts to allow one request for the key.
     *
     * @param key stable client identifier
     * @return true when the request fits inside the sliding window
     * @throws IllegalArgumentException when {@code key} is blank
     */
    public boolean allow(String key) {
        validateKey(key);
        long now = timeSource.currentTimeMillis();
        Deque<Long> timestamps = requestsByKey.computeIfAbsent(key, unused -> new ArrayDeque<>());
        removeExpired(timestamps, now);

        if (timestamps.size() >= maxRequests) {
            return false;
        }

        timestamps.addLast(now);
        return true;
    }

    /**
     * Returns how many more requests can be accepted right now for the key.
     *
     * @param key stable client identifier
     * @return remaining request allowance in the active sliding window
     * @throws IllegalArgumentException when {@code key} is blank
     */
    public int remainingRequests(String key) {
        validateKey(key);
        long now = timeSource.currentTimeMillis();
        Deque<Long> timestamps = requestsByKey.get(key);
        if (timestamps == null) {
            return maxRequests;
        }
        removeExpired(timestamps, now);
        return Math.max(0, maxRequests - timestamps.size());
    }

    /**
     * Returns the number of non-expired request timestamps currently tracked for the key.
     *
     * @param key stable client identifier
     * @return tracked request count after expired timestamps are removed
     * @throws IllegalArgumentException when {@code key} is blank
     */
    public int trackedRequestCount(String key) {
        validateKey(key);
        Deque<Long> timestamps = requestsByKey.get(key);
        if (timestamps == null) {
            return 0;
        }
        removeExpired(timestamps, timeSource.currentTimeMillis());
        return timestamps.size();
    }

    /**
     * Removes timestamps that are outside the current rolling window.
     */
    private void removeExpired(Deque<Long> timestamps, long now) {
        while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= windowSizeMillis) {
            timestamps.removeFirst();
        }
    }

    /**
     * Validates the caller key before it is used as a map key.
     */
    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
    }
}
