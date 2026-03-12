package com.example.notificationservice.messaging;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEventHandlerTest {

    @Test
    void handleStoresConsumedNotificationInInbox() {
        NotificationInbox inbox = new NotificationInbox(Clock.fixed(Instant.parse("2026-03-12T00:00:00Z"), ZoneOffset.UTC));
        NotificationEventHandler handler = new NotificationEventHandler(inbox);

        handler.handle("kafka", new UserRegisteredEvent(
                "u-100",
                "u100@example.com",
                "EU",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        ));

        assertThat(inbox.messages()).hasSize(1);
        assertThat(inbox.transportCounts()).containsEntry("kafka", 1);
        assertThat(inbox.messages().get(0).userId()).isEqualTo("u-100");
    }
}
