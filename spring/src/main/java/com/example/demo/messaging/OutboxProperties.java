package com.example.demo.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the educational outbox dispatcher.
 */
@ConfigurationProperties("app.messaging.outbox")
public record OutboxProperties(
        int batchSize,
        long pollDelayMillis,
        long retryDelayMillis,
        int maxAttempts) {
}
