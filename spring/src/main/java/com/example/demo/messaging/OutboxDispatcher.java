package com.example.demo.messaging;

import com.example.demo.observability.ApplicationMetrics;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Background dispatcher that turns durable outbox rows into transport publishes.
 */
@Component
public class OutboxDispatcher {

    private final OutboxEventStore outboxEventStore;
    private final UserRegistrationEventPublisher eventPublisher;
    private final OutboxProperties outboxProperties;
    private final ApplicationMetrics applicationMetrics;

    public OutboxDispatcher(OutboxEventStore outboxEventStore,
                            UserRegistrationEventPublisher eventPublisher,
                            OutboxProperties outboxProperties,
                            ApplicationMetrics applicationMetrics) {
        this.outboxEventStore = outboxEventStore;
        this.eventPublisher = eventPublisher;
        this.outboxProperties = outboxProperties;
        this.applicationMetrics = applicationMetrics;
    }

    @Scheduled(fixedDelayString = "${app.messaging.outbox.poll-delay-millis:3000}")
    public void dispatchPendingEvents() {
        List<OutboxEventRecord> batch;
        try {
            batch = outboxEventStore.fetchReadyBatch(outboxProperties.batchSize());
        } catch (Exception exception) {
            // Testcontainers can tear databases down before the scheduler stops.
            return;
        }
        for (OutboxEventRecord record : batch) {
            dispatch(record);
        }
    }

    public void dispatch(OutboxEventRecord record) {
        try {
            if ("USER_REGISTERED".equals(record.eventType())) {
                eventPublisher.publish(outboxEventStore.deserializeUserRegistered(record.payload()));
                outboxEventStore.markPublished(record.id());
                applicationMetrics.recordOutboxPublished();
                return;
            }

            outboxEventStore.markDeadLetter(record.id(), "unsupported event type: " + record.eventType());
            applicationMetrics.recordOutboxDeadLetter();
        } catch (Exception exception) {
            if (record.attempts() + 1 >= outboxProperties.maxAttempts()) {
                outboxEventStore.markDeadLetter(record.id(), exception.getMessage());
                applicationMetrics.recordOutboxDeadLetter();
                return;
            }

            outboxEventStore.reschedule(record.id(), exception.getMessage(), outboxProperties.retryDelayMillis());
            applicationMetrics.recordOutboxRetry();
        }
    }
}
