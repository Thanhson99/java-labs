package com.example.notificationservice.messaging;

import java.time.Instant;

/**
 * Local copy of the event contract consumed from the registration service.
 */
public record UserRegisteredEvent(
        String userId,
        String email,
        String region,
        Instant occurredAt,
        String source) {
}
