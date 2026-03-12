package com.example.demo.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaUserRegistrationEventPublisherTest {

    @Test
    void publishSendsEventToConfiguredTopic() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate = mock(KafkaTemplate.class);
        RegistrationMessagingProperties properties = new RegistrationMessagingProperties(
                new RegistrationMessagingProperties.Kafka(true, "user-registered.v1"),
                new RegistrationMessagingProperties.Rabbitmq(false, "ignored", "ignored", "ignored")
        );
        KafkaUserRegistrationEventPublisher publisher = new KafkaUserRegistrationEventPublisher(kafkaTemplate, properties);
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-10",
                "u10@example.com",
                "APAC",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        publisher.publish(event);

        verify(kafkaTemplate).send("user-registered.v1", "u-10", event);
    }
}
