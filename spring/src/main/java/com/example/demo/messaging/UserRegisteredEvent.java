package com.example.demo.messaging;

import java.time.Instant;

/**
 * Domain event emitted after a user registration succeeds.
 */
public record UserRegisteredEvent(
        String userId,
        String email,
        String region,
        Instant occurredAt,
        String source) {
}
