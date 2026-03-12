package com.example.demo.messaging;

import com.example.demo.analytics.AnalyticsEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Shared handler used by Kafka and RabbitMQ listeners after a message is received.
 */
@Service
public class UserRegistrationEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationEventProcessor.class);

    private final AnalyticsEventStore analyticsEventStore;
    private final EventConsumptionTracker tracker;

    public UserRegistrationEventProcessor(
            AnalyticsEventStore analyticsEventStore,
            EventConsumptionTracker tracker) {
        this.analyticsEventStore = analyticsEventStore;
        this.tracker = tracker;
    }

    /**
     * Handles a consumed registration event and records a transport-specific analytics event.
     *
     * @param transport transport that delivered the event
     * @param event immutable event payload
     */
    public void process(String transport, UserRegisteredEvent event) {
        logger.info("Consumed {} registration event for user {}", transport, event.userId());
        analyticsEventStore.record(
                "USER_REGISTERED_CONSUMED_" + transport.toUpperCase(),
                event.userId(),
                event.region()
        );
        tracker.record(transport, event);
    }
}
