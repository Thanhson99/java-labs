package com.example.javalabs.basic;

import java.util.List;

/**
 * Builds retry plans for failed outbox events.
 */
public final class OutboxRetryPlanner {

    private final OutboxRetryPolicy retryPolicy;

    /**
     * Creates a planner backed by a retry policy.
     *
     * @param retryPolicy policy used to determine retryability and delay
     * @throws IllegalArgumentException when {@code retryPolicy} is {@code null}
     */
    public OutboxRetryPlanner(OutboxRetryPolicy retryPolicy) {
        if (retryPolicy == null) {
            throw new IllegalArgumentException("retryPolicy must not be null");
        }
        this.retryPolicy = retryPolicy;
    }

    /**
     * Builds retry plans for failed events only.
     *
     * @param events events to inspect
     * @return retry plans for failed events in input order
     * @throws IllegalArgumentException when {@code events} is {@code null} or contains {@code null}
     */
    public List<OutboxRetryPlan> planRetries(List<OutboxEvent> events) {
        if (events == null) {
            throw new IllegalArgumentException("events must not be null");
        }
        if (events.stream().anyMatch(event -> event == null)) {
            throw new IllegalArgumentException("events must not contain null");
        }
        return events.stream()
                .filter(event -> event.status() == OutboxEventStatus.FAILED)
                .map(event -> new OutboxRetryPlan(
                        event.id(),
                        retryPolicy.canRetry(event),
                        retryPolicy.delayBeforeNextAttemptMillis(event)
                ))
                .toList();
    }
}
