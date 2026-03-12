package com.example.demo.messaging;

import com.example.demo.observability.ApplicationMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class OutboxDispatcherTest {

    @Test
    void dispatchPublishesAndMarksOutboxRowAsPublished() {
        OutboxEventStore outboxEventStore = mock(OutboxEventStore.class);
        UserRegistrationEventPublisher eventPublisher = mock(UserRegistrationEventPublisher.class);
        OutboxProperties outboxProperties = new OutboxProperties(10, 3000, 5000, 3);
        ApplicationMetrics applicationMetrics = new ApplicationMetrics(new SimpleMeterRegistry());
        OutboxDispatcher dispatcher = new OutboxDispatcher(
                outboxEventStore,
                eventPublisher,
                outboxProperties,
                applicationMetrics
        );
        OutboxEventRecord record = new OutboxEventRecord(
                10L,
                "USER",
                "u-88",
                "USER_REGISTERED",
                "{\"userId\":\"u-88\"}",
                "PENDING",
                0,
                Instant.parse("2026-03-12T00:00:00Z"),
                Instant.parse("2026-03-12T00:00:00Z"),
                null,
                null
        );
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-88",
                "u88@example.com",
                "APAC",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        when(outboxEventStore.deserializeUserRegistered(record.payload())).thenReturn(event);

        dispatcher.dispatch(record);

        verify(eventPublisher).publish(event);
        verify(outboxEventStore).markPublished(10L);
    }

    @Test
    void dispatchMovesRowToDeadLetterWhenAttemptsAreExhausted() {
        OutboxEventStore outboxEventStore = mock(OutboxEventStore.class);
        UserRegistrationEventPublisher eventPublisher = mock(UserRegistrationEventPublisher.class);
        OutboxProperties outboxProperties = new OutboxProperties(10, 3000, 5000, 3);
        ApplicationMetrics applicationMetrics = new ApplicationMetrics(new SimpleMeterRegistry());
        OutboxDispatcher dispatcher = new OutboxDispatcher(
                outboxEventStore,
                eventPublisher,
                outboxProperties,
                applicationMetrics
        );
        OutboxEventRecord record = new OutboxEventRecord(
                11L,
                "USER",
                "u-89",
                "USER_REGISTERED",
                "{\"userId\":\"u-89\"}",
                "PENDING",
                2,
                Instant.parse("2026-03-12T00:00:00Z"),
                Instant.parse("2026-03-12T00:00:00Z"),
                null,
                null
        );
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-89",
                "u89@example.com",
                "APAC",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        when(outboxEventStore.deserializeUserRegistered(record.payload())).thenReturn(event);
        org.mockito.Mockito.doThrow(new IllegalStateException("broker unavailable"))
                .when(eventPublisher).publish(event);

        dispatcher.dispatch(record);

        verify(outboxEventStore).deserializeUserRegistered(record.payload());
        verify(outboxEventStore).markDeadLetter(11L, "broker unavailable");
        verifyNoMoreInteractions(outboxEventStore);
    }
}
