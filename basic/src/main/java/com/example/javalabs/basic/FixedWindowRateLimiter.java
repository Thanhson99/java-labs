package com.example.javalabs.basic;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple fixed-window rate limiter.
 *
 * <p>This style of limiter is common in APIs because it is easy to reason about and cheap to
 * implement. The tradeoff is that requests around a window boundary can burst more than expected.</p>
 */
public final class FixedWindowRateLimiter {

    private final int maxRequestsPerWindow;
    private final long windowSizeMillis;
    private final TimeSource timeSource;
    private final Map<String, WindowState> states = new HashMap<>();

    /**
     * Creates a limiter with a fixed request budget per time window.
     *
     * @param maxRequestsPerWindow maximum number of requests allowed in one window
     * @param windowSizeMillis the window size in milliseconds
     * @param timeSource clock abstraction used for timing decisions
     * @throws IllegalArgumentException when limits are not positive or {@code timeSource} is {@code null}
     */
    public FixedWindowRateLimiter(int maxRequestsPerWindow, long windowSizeMillis, TimeSource timeSource) {
        if (maxRequestsPerWindow <= 0) {
            throw new IllegalArgumentException("maxRequestsPerWindow must be positive");
        }
        if (windowSizeMillis <= 0) {
            throw new IllegalArgumentException("windowSizeMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSizeMillis = windowSizeMillis;
        this.timeSource = timeSource;
    }

    /**
     * Attempts to consume one request token for a client key.
     *
     * @param key a stable identifier such as user ID, IP address, or API key
     * @return {@code true} when the request is allowed, otherwise {@code false}
     */
    public boolean allow(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }

        long now = timeSource.currentTimeMillis();
        WindowState state = states.computeIfAbsent(key, unused -> new WindowState(now, 0));
        if (now - state.windowStartMillis >= windowSizeMillis) {
            // A new fixed window starts at the first request after the previous window expires.
            state.windowStartMillis = now;
            state.requestCount = 0;
        }

        if (state.requestCount >= maxRequestsPerWindow) {
            return false;
        }

        state.requestCount++;
        return true;
    }

    /**
     * Returns the remaining quota in the current window.
     *
     * @param key the client key
     * @return remaining number of requests that can still be accepted
     * @throws IllegalArgumentException when {@code key} is blank
     */
    public int remainingRequests(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        WindowState state = states.get(key);
        if (state == null) {
            return maxRequestsPerWindow;
        }

        long now = timeSource.currentTimeMillis();
        if (now - state.windowStartMillis >= windowSizeMillis) {
            return maxRequestsPerWindow;
        }
        return Math.max(0, maxRequestsPerWindow - state.requestCount);
    }

    /**
     * Mutable counter state for one caller inside a fixed time window.
     */
    private static final class WindowState {
        private long windowStartMillis;
        private int requestCount;

        /**
         * Creates a window counter.
         *
         * @param windowStartMillis timestamp when the current window started
         * @param requestCount number of requests consumed in the current window
         */
        private WindowState(long windowStartMillis, int requestCount) {
            this.windowStartMillis = windowStartMillis;
            this.requestCount = requestCount;
        }
    }
}
