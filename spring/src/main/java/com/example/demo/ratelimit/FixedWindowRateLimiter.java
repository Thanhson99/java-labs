package com.example.demo.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory fixed-window rate limiter suitable for learning and single-instance demos.
 */
@Component
public class FixedWindowRateLimiter {

    private final RegistrationRateLimitProperties properties;
    private final Clock clock;
    private final Map<String, WindowState> states = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(RegistrationRateLimitProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Attempts to consume one request for the given caller key.
     *
     * @param callerKey a stable key such as an API key or client ID
     * @return {@code true} if the caller is still within budget
     */
    public boolean allow(String callerKey) {
        long now = clock.millis();
        WindowState state = states.computeIfAbsent(callerKey, unused -> new WindowState(now, 0));

        synchronized (state) {
            if (now - state.windowStartMillis >= properties.windowMillis()) {
                state.windowStartMillis = now;
                state.requestCount = 0;
            }

            if (state.requestCount >= properties.maxRequests()) {
                return false;
            }

            state.requestCount++;
            return true;
        }
    }

    public int remainingRequests(String callerKey) {
        WindowState state = states.get(callerKey);
        if (state == null) {
            return properties.maxRequests();
        }

        long now = clock.millis();
        synchronized (state) {
            if (now - state.windowStartMillis >= properties.windowMillis()) {
                return properties.maxRequests();
            }
            return Math.max(0, properties.maxRequests() - state.requestCount);
        }
    }

    private static final class WindowState {
        private long windowStartMillis;
        private int requestCount;

        private WindowState(long windowStartMillis, int requestCount) {
            this.windowStartMillis = windowStartMillis;
            this.requestCount = requestCount;
        }
    }
}
