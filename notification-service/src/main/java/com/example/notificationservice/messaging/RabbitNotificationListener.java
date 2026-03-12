package com.example.notificationservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
public class RabbitNotificationListener {

    private final NotificationEventHandler handler;

    public RabbitNotificationListener(NotificationEventHandler handler) {
        this.handler = handler;
    }

    @RabbitListener(queues = "${app.messaging.rabbitmq.queue}")
    public void onMessage(UserRegisteredEvent event) {
        handler.handle("rabbitmq", event);
    }
}
