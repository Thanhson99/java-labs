package com.example.demo.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitUserRegistrationEventPublisherTest {

    @Test
    void publishSendsEventToConfiguredExchangeAndRoutingKey() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RegistrationMessagingProperties properties = new RegistrationMessagingProperties(
                new RegistrationMessagingProperties.Kafka(false, "ignored"),
                new RegistrationMessagingProperties.Rabbitmq(true,
                        "user.registration.exchange",
                        "user.registration.queue",
                        "user.registered")
        );
        RabbitUserRegistrationEventPublisher publisher = new RabbitUserRegistrationEventPublisher(rabbitTemplate, properties);
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-20",
                "u20@example.com",
                "EU",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend("user.registration.exchange", "user.registered", event);
    }
}
