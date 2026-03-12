package com.example.demo.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ consumer for the registration event.
 */
@Component
@ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
public class RabbitUserRegistrationEventListener {

    private final UserRegistrationEventProcessor processor;

    public RabbitUserRegistrationEventListener(UserRegistrationEventProcessor processor) {
        this.processor = processor;
    }

    /**
     * Consumes a RabbitMQ registration event and forwards it to the shared processor.
     *
     * @param event immutable event payload
     */
    @RabbitListener(queues = "${app.messaging.rabbitmq.queue}")
    public void onMessage(UserRegisteredEvent event) {
        processor.process("rabbitmq", event);
    }
}
