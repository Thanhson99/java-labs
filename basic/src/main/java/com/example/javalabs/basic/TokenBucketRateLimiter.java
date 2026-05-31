package com.example.javalabs.basic;

import java.util.HashMap;
import java.util.Map;

/**
 * Token-bucket rate limiter with per-key buckets.
 *
 * <p>The bucket has a fixed capacity and refills over time. This allows a controlled burst when a
 * client has been idle, while still enforcing a long-term refill rate.</p>
 */
public final class TokenBucketRateLimiter {

    private final int capacity;
    private final double refillTokensPerMillis;
    private final TimeSource timeSource;
    private final Map<String, BucketState> bucketsByKey = new HashMap<>();

    /**
     * Creates a per-key token-bucket limiter.
     *
     * @param capacity maximum tokens per bucket
     * @param refillTokensPerSecond refill rate in tokens per second
     * @param timeSource clock used to calculate refill amount
     * @throws IllegalArgumentException when capacity, refill rate, or clock is invalid
     */
    public TokenBucketRateLimiter(int capacity, double refillTokensPerSecond, TimeSource timeSource) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (refillTokensPerSecond <= 0) {
            throw new IllegalArgumentException("refillTokensPerSecond must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.capacity = capacity;
        this.refillTokensPerMillis = refillTokensPerSecond / 1_000.0;
        this.timeSource = timeSource;
    }

    /**
     * Attempts to consume one token for a key.
     *
     * @param key stable client identifier
     * @return true when a token was available
     * @throws IllegalArgumentException when {@code key} is blank
     */
    public boolean allow(String key) {
        validateKey(key);
        BucketState bucket = bucketFor(key);
        refill(bucket);

        if (bucket.tokens < 1.0) {
            return false;
        }

        bucket.tokens -= 1.0;
        return true;
    }

    /**
     * Returns the currently available tokens for a key after refilling.
     *
     * @param key stable client identifier
     * @return available tokens
     * @throws IllegalArgumentException when {@code key} is blank
     */
    public double availableTokens(String key) {
        validateKey(key);
        BucketState bucket = bucketFor(key);
        refill(bucket);
        return bucket.tokens;
    }

    /**
     * Finds or creates the bucket for one key.
     */
    private BucketState bucketFor(String key) {
        long now = timeSource.currentTimeMillis();
        return bucketsByKey.computeIfAbsent(key, unused -> new BucketState(capacity, now));
    }

    /**
     * Refills a bucket based on elapsed time since the previous refill.
     */
    private void refill(BucketState bucket) {
        long now = timeSource.currentTimeMillis();
        long elapsedMillis = Math.max(0, now - bucket.lastRefillMillis);
        if (elapsedMillis == 0) {
            return;
        }

        bucket.tokens = Math.min(capacity, bucket.tokens + elapsedMillis * refillTokensPerMillis);
        bucket.lastRefillMillis = now;
    }

    /**
     * Validates a bucket key.
     */
    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
    }

    /**
     * Mutable bucket state for one limiter key.
     */
    private static final class BucketState {
        private double tokens;
        private long lastRefillMillis;

        private BucketState(double tokens, long lastRefillMillis) {
            this.tokens = tokens;
            this.lastRefillMillis = lastRefillMillis;
        }
    }
}
