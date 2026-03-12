package com.example.notificationservice.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.messaging.kafka", name = "enabled", havingValue = "true")
public class KafkaNotificationListener {

    private final NotificationEventHandler handler;

    public KafkaNotificationListener(NotificationEventHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(
            topics = "${app.messaging.kafka.topic}",
            groupId = "${app.messaging.kafka.consumer-group}")
    public void onMessage(UserRegisteredEvent event) {
        handler.handle("kafka", event);
    }
}
