package com.example.demo.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for the registration event.
 */
@Component
@ConditionalOnProperty(prefix = "app.messaging.kafka", name = "enabled", havingValue = "true")
public class KafkaUserRegistrationEventListener {

    private final UserRegistrationEventProcessor processor;

    public KafkaUserRegistrationEventListener(UserRegistrationEventProcessor processor) {
        this.processor = processor;
    }

    /**
     * Consumes a Kafka registration event and forwards it to the shared processor.
     *
     * @param event immutable event payload
     */
    @KafkaListener(
            topics = "${app.messaging.kafka.topic}",
            groupId = "${app.messaging.kafka.consumer-group:user-registration-learning}")
    public void onMessage(UserRegisteredEvent event) {
        processor.process("kafka", event);
    }
}
