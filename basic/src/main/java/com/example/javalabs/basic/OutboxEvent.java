package com.example.javalabs.basic;

/**
 * Event stored in the outbox before being sent to a downstream system.
 *
 * @param id event identifier
 * @param type event type
 * @param aggregateId domain object id
 * @param payload serialized payload for the downstream consumer
 * @param status processing status
 * @param attemptCount number of publish attempts
 */
public record OutboxEvent(
        String id,
        String type,
        String aggregateId,
        String payload,
        OutboxEventStatus status,
        int attemptCount) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when identifiers, payload, status, or attempt count are invalid
     */
    public OutboxEvent {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId must not be blank");
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (attemptCount < 0) {
            throw new IllegalArgumentException("attemptCount must not be negative");
        }
    }

    /**
     * Returns a copy marked as successfully published.
     *
     * @return published event with incremented attempt count
     */
    public OutboxEvent markPublished() {
        return new OutboxEvent(id, type, aggregateId, payload, OutboxEventStatus.PUBLISHED, attemptCount + 1);
    }

    /**
     * Returns a copy marked as failed.
     *
     * @return failed event with incremented attempt count
     */
    public OutboxEvent markFailed() {
        return new OutboxEvent(id, type, aggregateId, payload, OutboxEventStatus.FAILED, attemptCount + 1);
    }
}
