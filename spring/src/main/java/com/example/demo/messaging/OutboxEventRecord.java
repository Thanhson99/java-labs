package com.example.demo.messaging;

/**
 * In-memory view of one outbox row waiting to be dispatched.
 */
public record OutboxEventRecord(
        long id,
        String aggregateType,
        String aggregateId,
        String eventType,
        String payload,
        int attempts) {
}
