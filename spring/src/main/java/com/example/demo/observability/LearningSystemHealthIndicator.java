package com.example.demo.observability;

import com.example.demo.analytics.AnalyticsEventStore;
import com.example.demo.messaging.UserRegistrationEventPublisher;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Adds domain-specific details to the default Actuator health response.
 */
@Component
public class LearningSystemHealthIndicator implements HealthIndicator {

    private final AnalyticsEventStore analyticsEventStore;
    private final UserRegistrationEventPublisher eventPublisher;

    public LearningSystemHealthIndicator(AnalyticsEventStore analyticsEventStore,
                                         UserRegistrationEventPublisher eventPublisher) {
        this.analyticsEventStore = analyticsEventStore;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("analyticsEventCount", analyticsEventStore.countEvents())
                .withDetail("enabledMessagingTransports", eventPublisher.enabledTransports())
                .build();
    }
}
