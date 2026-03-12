package com.example.notificationservice.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitNotificationListenerTest {

    @Test
    void onMessageDelegatesToHandler() {
        NotificationEventHandler handler = mock(NotificationEventHandler.class);
        RabbitNotificationListener listener = new RabbitNotificationListener(handler);
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-102",
                "u102@example.com",
                "US",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        listener.onMessage(event);

        verify(handler).handle("rabbitmq", event);
    }
}
