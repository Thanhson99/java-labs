package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketRateLimiterTest {

    @Test
    void allowsInitialBurstUpToBucketCapacity() {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 1.0, new ManualTimeSource(0));

        assertTrue(limiter.allow("client-1"));
        assertTrue(limiter.allow("client-1"));
        assertTrue(limiter.allow("client-1"));
        assertFalse(limiter.allow("client-1"));
    }

    @Test
    void refillsTokensOverTime() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(2, 2.0, timeSource);

        assertTrue(limiter.allow("client-1"));
        assertTrue(limiter.allow("client-1"));
        assertFalse(limiter.allow("client-1"));

        timeSource.advanceMillis(500);
        assertEquals(1.0, limiter.availableTokens("client-1"));
        assertTrue(limiter.allow("client-1"));
        assertFalse(limiter.allow("client-1"));
    }

    @Test
    void refillDoesNotExceedCapacity() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(2, 10.0, timeSource);

        limiter.allow("client-1");
        timeSource.advanceMillis(10_000);

        assertEquals(2.0, limiter.availableTokens("client-1"));
    }

    @Test
    void tracksDifferentKeysIndependently() {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(1, 1.0, new ManualTimeSource(0));

        assertTrue(limiter.allow("client-1"));
        assertTrue(limiter.allow("client-2"));
        assertFalse(limiter.allow("client-1"));
        assertFalse(limiter.allow("client-2"));
    }

    @Test
    void rejectsBlankKey() {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(1, 1.0, new ManualTimeSource(0));

        assertThrows(IllegalArgumentException.class, () -> limiter.allow(" "));
    }
}
