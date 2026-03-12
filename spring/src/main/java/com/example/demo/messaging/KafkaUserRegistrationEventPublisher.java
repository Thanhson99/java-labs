package com.example.demo.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka transport for the user registration event.
 */
@Component
@ConditionalOnProperty(prefix = "app.messaging.kafka", name = "enabled", havingValue = "true")
public class KafkaUserRegistrationEventPublisher implements UserRegistrationEventSink {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private final RegistrationMessagingProperties properties;

    public KafkaUserRegistrationEventPublisher(
            KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate,
            RegistrationMessagingProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public String transportName() {
        return "kafka";
    }

    @Override
    public void publish(UserRegisteredEvent event) {
        kafkaTemplate.send(properties.kafka().topic(), event.userId(), event);
    }
}
