package com.example.javalabs.basic;

/**
 * Calculates whether an outbox event should be retried and how long the next delay should be.
 */
public final class OutboxRetryPolicy {

    private final int maxAttempts;
    private final long baseDelayMillis;
    private final long maxDelayMillis;

    /**
     * Creates an exponential backoff retry policy.
     *
     * @param maxAttempts maximum attempts before an event is no longer retryable
     * @param baseDelayMillis initial retry delay
     * @param maxDelayMillis maximum delay cap
     * @throws IllegalArgumentException when attempts or delays are invalid
     */
    public OutboxRetryPolicy(int maxAttempts, long baseDelayMillis, long maxDelayMillis) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive");
        }
        if (baseDelayMillis <= 0) {
            throw new IllegalArgumentException("baseDelayMillis must be positive");
        }
        if (maxDelayMillis < baseDelayMillis) {
            throw new IllegalArgumentException("maxDelayMillis must be greater than or equal to baseDelayMillis");
        }
        this.maxAttempts = maxAttempts;
        this.baseDelayMillis = baseDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
    }

    /**
     * Checks whether an event can be retried.
     *
     * @param event event to inspect
     * @return {@code true} when the event is not published and has attempts remaining
     * @throws IllegalArgumentException when {@code event} is {@code null}
     */
    public boolean canRetry(OutboxEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (event.status() == OutboxEventStatus.PUBLISHED) {
            return false;
        }
        return event.attemptCount() < maxAttempts;
    }

    /**
     * Calculates delay before the next retry attempt.
     *
     * @param event event to inspect
     * @return delay in milliseconds, or {@code -1} when the event is not retryable
     * @throws IllegalArgumentException when {@code event} is {@code null}
     */
    public long delayBeforeNextAttemptMillis(OutboxEvent event) {
        if (!canRetry(event)) {
            return -1;
        }
        // Clamp the shift count to avoid overflowing when a malformed event has many attempts.
        long multiplier = 1L << Math.min(event.attemptCount(), 30);
        long delay = baseDelayMillis * multiplier;
        return Math.min(delay, maxDelayMillis);
    }
}
