package com.example.javalabs.basic;

import java.util.Random;
import java.util.function.DoubleSupplier;

/**
 * Calculates exponential retry delays with jitter.
 *
 * <p>Plain exponential backoff can still create retry waves when many callers fail at the same
 * time. Jitter spreads retries across a small range so the downstream dependency has a better
 * chance to recover.</p>
 */
public final class JitteredBackoffPolicy {

    private final int maxAttempts;
    private final long baseDelayMillis;
    private final long maxDelayMillis;
    private final double jitterRatio;
    private final DoubleSupplier randomSource;

    /**
     * Creates a retry policy that uses a random jitter source.
     *
     * @param maxAttempts maximum number of attempts that may be retried
     * @param baseDelayMillis first retry delay before exponential growth
     * @param maxDelayMillis upper bound for any generated delay
     * @param jitterRatio percentage of delay variance, from {@code 0.0} to {@code 1.0}
     * @throws IllegalArgumentException when numeric limits are invalid
     */
    public JitteredBackoffPolicy(
            int maxAttempts,
            long baseDelayMillis,
            long maxDelayMillis,
            double jitterRatio) {
        this(maxAttempts, baseDelayMillis, maxDelayMillis, jitterRatio, new Random()::nextDouble);
    }

    /**
     * Creates a retry policy with an injected random source for deterministic tests.
     */
    JitteredBackoffPolicy(
            int maxAttempts,
            long baseDelayMillis,
            long maxDelayMillis,
            double jitterRatio,
            DoubleSupplier randomSource) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive");
        }
        if (baseDelayMillis <= 0) {
            throw new IllegalArgumentException("baseDelayMillis must be positive");
        }
        if (maxDelayMillis < baseDelayMillis) {
            throw new IllegalArgumentException("maxDelayMillis must be greater than or equal to baseDelayMillis");
        }
        if (jitterRatio < 0 || jitterRatio > 1) {
            throw new IllegalArgumentException("jitterRatio must be between 0 and 1");
        }
        if (randomSource == null) {
            throw new IllegalArgumentException("randomSource must not be null");
        }
        this.maxAttempts = maxAttempts;
        this.baseDelayMillis = baseDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
        this.jitterRatio = jitterRatio;
        this.randomSource = randomSource;
    }

    /**
     * Determines whether another retry is allowed for the current attempt count.
     *
     * @param attemptCount number of attempts already made
     * @return true when another attempt is still inside the retry budget
     * @throws IllegalArgumentException when {@code attemptCount} is negative
     */
    public boolean canRetry(int attemptCount) {
        if (attemptCount < 0) {
            throw new IllegalArgumentException("attemptCount must not be negative");
        }
        return attemptCount < maxAttempts;
    }

    /**
     * Calculates the delay before the next retry attempt.
     *
     * @param attemptCount number of attempts already made
     * @return delay in milliseconds, or {@code -1} when no retry is allowed
     * @throws IllegalArgumentException when {@code attemptCount} is negative
     */
    public long delayBeforeNextAttemptMillis(int attemptCount) {
        if (!canRetry(attemptCount)) {
            return -1;
        }

        long cappedDelay = cappedExponentialDelay(attemptCount);
        if (jitterRatio == 0) {
            return cappedDelay;
        }

        // Clamp the random sample so a custom test source cannot produce a delay outside bounds.
        long lowerBound = Math.max(0, Math.round(cappedDelay * (1 - jitterRatio)));
        long upperBound = Math.min(maxDelayMillis, Math.round(cappedDelay * (1 + jitterRatio)));
        double sample = Math.max(0, Math.min(1, randomSource.getAsDouble()));
        return lowerBound + Math.round((upperBound - lowerBound) * sample);
    }

    /**
     * Calculates exponential growth while protecting against long overflow.
     */
    private long cappedExponentialDelay(int attemptCount) {
        int safeShift = Math.min(attemptCount, 30);
        long multiplier = 1L << safeShift;
        if (baseDelayMillis > Long.MAX_VALUE / multiplier) {
            return maxDelayMillis;
        }
        return Math.min(baseDelayMillis * multiplier, maxDelayMillis);
    }
}
