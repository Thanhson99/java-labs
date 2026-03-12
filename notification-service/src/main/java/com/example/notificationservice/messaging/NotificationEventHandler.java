package com.example.notificationservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Shared consumer-side business handler for Kafka and RabbitMQ deliveries.
 */
@Service
public class NotificationEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventHandler.class);

    private final NotificationInbox inbox;

    public NotificationEventHandler(NotificationInbox inbox) {
        this.inbox = inbox;
    }

    public void handle(String transport, UserRegisteredEvent event) {
        logger.info("Received {} notification event for {}", transport, event.userId());
        inbox.append(transport, event);
    }
}
