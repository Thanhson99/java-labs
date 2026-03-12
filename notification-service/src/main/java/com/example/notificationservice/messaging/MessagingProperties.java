package com.example.notificationservice.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.messaging")
public record MessagingProperties(
        Kafka kafka,
        Rabbitmq rabbitmq) {

    public record Kafka(boolean enabled, String topic, String consumerGroup) {
    }

    public record Rabbitmq(boolean enabled, String exchange, String queue, String routingKey) {
    }
}
