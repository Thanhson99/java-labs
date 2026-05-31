package com.example.javalabs.basic;

/**
 * Processing status for an outbox event.
 */
public enum OutboxEventStatus {
    /**
     * Event is waiting for its first publish attempt.
     */
    PENDING,

    /**
     * Event was successfully published and should not be retried.
     */
    PUBLISHED,

    /**
     * Event failed during publish and may be retried by policy.
     */
    FAILED
}
