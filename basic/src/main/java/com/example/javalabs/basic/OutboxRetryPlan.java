package com.example.javalabs.basic;

/**
 * Human-readable retry decision for an outbox event.
 *
 * @param eventId event identifier
 * @param retryable whether the event should be retried
 * @param delayMillis delay before the next attempt, or -1 when not retryable
 */
public record OutboxRetryPlan(String eventId, boolean retryable, long delayMillis) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when {@code eventId} is blank or delay is invalid
     */
    public OutboxRetryPlan {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId must not be blank");
        }
        if (delayMillis < -1) {
            throw new IllegalArgumentException("delayMillis must be -1 or greater");
        }
        if (!retryable && delayMillis != -1) {
            throw new IllegalArgumentException("non-retryable plans must use -1 delay");
        }
    }
}
