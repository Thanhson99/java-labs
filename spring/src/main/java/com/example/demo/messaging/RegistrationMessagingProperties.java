package com.example.demo.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Messaging settings used to compare Kafka and RabbitMQ in the same registration flow.
 */
@ConfigurationProperties("app.messaging")
public record RegistrationMessagingProperties(
        Kafka kafka,
        Rabbitmq rabbitmq) {

    /**
     * Kafka-specific learning configuration.
     */
    public record Kafka(boolean enabled, String topic) {
    }

    /**
     * RabbitMQ-specific learning configuration.
     */
    public record Rabbitmq(boolean enabled, String exchange, String queue, String routingKey) {
    }
}
