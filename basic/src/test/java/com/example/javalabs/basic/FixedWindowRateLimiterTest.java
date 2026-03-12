package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixedWindowRateLimiterTest {

    @Test
    void allowsOnlyConfiguredNumberOfRequestsPerWindow() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 1_000, timeSource);

        assertTrue(limiter.allow("client-1"));
        assertTrue(limiter.allow("client-1"));
        assertFalse(limiter.allow("client-1"));
        assertEquals(0, limiter.remainingRequests("client-1"));
    }

    @Test
    void resetsBudgetAfterWindowExpires() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 1_000, timeSource);

        assertTrue(limiter.allow("client-1"));
        assertFalse(limiter.allow("client-1"));

        timeSource.advanceMillis(1_000);

        assertTrue(limiter.allow("client-1"));
    }
}
