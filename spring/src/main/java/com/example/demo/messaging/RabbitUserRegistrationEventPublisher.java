package com.example.demo.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ transport for the user registration event.
 */
@Component
@ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
public class RabbitUserRegistrationEventPublisher implements UserRegistrationEventSink {

    private final RabbitTemplate rabbitTemplate;
    private final RegistrationMessagingProperties properties;

    public RabbitUserRegistrationEventPublisher(
            RabbitTemplate rabbitTemplate,
            RegistrationMessagingProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @Override
    public String transportName() {
        return "rabbitmq";
    }

    @Override
    public void publish(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(
                properties.rabbitmq().exchange(),
                properties.rabbitmq().routingKey(),
                event
        );
    }
}
