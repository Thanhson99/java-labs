package com.example.javalabs.basic;

/**
 * Background job with a processing deadline.
 *
 * @param job background job payload
 * @param deadlineMillis absolute deadline timestamp in milliseconds
 */
public record DeadlineJob(BackgroundJob job, long deadlineMillis) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when job is {@code null} or deadline is negative
     */
    public DeadlineJob {
        if (job == null) {
            throw new IllegalArgumentException("job must not be null");
        }
        if (deadlineMillis < 0) {
            throw new IllegalArgumentException("deadlineMillis must not be negative");
        }
    }
}
