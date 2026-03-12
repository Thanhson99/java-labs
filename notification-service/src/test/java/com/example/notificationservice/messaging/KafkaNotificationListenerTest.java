package com.example.notificationservice.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaNotificationListenerTest {

    @Test
    void onMessageDelegatesToHandler() {
        NotificationEventHandler handler = mock(NotificationEventHandler.class);
        KafkaNotificationListener listener = new KafkaNotificationListener(handler);
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-101",
                "u101@example.com",
                "APAC",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        listener.onMessage(event);

        verify(handler).handle("kafka", event);
    }
}
