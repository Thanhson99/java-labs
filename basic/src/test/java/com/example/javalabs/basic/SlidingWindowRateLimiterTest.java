package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlidingWindowRateLimiterTest {

    @Test
    void allowsOnlyConfiguredRequestsInsideSlidingWindow() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(2, 1_000, timeSource);

        assertTrue(limiter.allow("client-1"));
        timeSource.advanceMillis(400);
        assertTrue(limiter.allow("client-1"));
        timeSource.advanceMillis(400);

        assertFalse(limiter.allow("client-1"));
        assertEquals(0, limiter.remainingRequests("client-1"));
    }

    @Test
    void requestBecomesAvailableWhenOldestTimestampExpires() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(2, 1_000, timeSource);

        assertTrue(limiter.allow("client-1"));
        timeSource.advanceMillis(400);
        assertTrue(limiter.allow("client-1"));
        timeSource.advanceMillis(600);

        assertTrue(limiter.allow("client-1"));
        assertEquals(0, limiter.remainingRequests("client-1"));
        assertEquals(2, limiter.trackedRequestCount("client-1"));
    }

    @Test
    void tracksDifferentKeysIndependently() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(1, 1_000, timeSource);

        assertTrue(limiter.allow("client-1"));
        assertTrue(limiter.allow("client-2"));

        assertFalse(limiter.allow("client-1"));
        assertFalse(limiter.allow("client-2"));
    }

    @Test
    void rejectsBlankKey() {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(1, 1_000, new ManualTimeSource(0));

        assertThrows(IllegalArgumentException.class, () -> limiter.allow(" "));
    }
}
