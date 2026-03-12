package com.example.notificationservice.messaging;

import java.time.Instant;

/**
 * In-memory representation of a consumed notification job.
 */
public record NotificationMessage(
        String transport,
        String userId,
        String email,
        String region,
        Instant receivedAt) {
}
