package com.example.demo.messaging;

import java.time.Instant;

/**
 * In-memory view of one outbox row waiting to be dispatched.
 */
public record OutboxEventRecord(
        long id,
        String aggregateType,
        String aggregateId,
        String eventType,
        String payload,
        String status,
        int attempts,
        Instant availableAt,
        Instant createdAt,
        Instant publishedAt,
        String lastError) {
}
