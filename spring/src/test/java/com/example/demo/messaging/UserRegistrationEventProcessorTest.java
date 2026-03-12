package com.example.demo.messaging;

import com.example.demo.analytics.AnalyticsEventStore;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserRegistrationEventProcessorTest {

    @Test
    void processRecordsAnalyticsAndUpdatesTracker() {
        AnalyticsEventStore analyticsEventStore = mock(AnalyticsEventStore.class);
        EventConsumptionTracker tracker = new EventConsumptionTracker();
        UserRegistrationEventProcessor processor = new UserRegistrationEventProcessor(analyticsEventStore, tracker);
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-50",
                "u50@example.com",
                "EU",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        processor.process("kafka", event);

        verify(analyticsEventStore).record("USER_REGISTERED_CONSUMED_KAFKA", "u-50", "EU");
        org.assertj.core.api.Assertions.assertThat(tracker.consumedCounts()).containsEntry("kafka", 1);
        org.assertj.core.api.Assertions.assertThat(tracker.lastConsumedUserIds()).containsEntry("kafka", "u-50");
    }
}
