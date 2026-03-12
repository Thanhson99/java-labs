package com.example.demo.registration;

import com.example.demo.analytics.AnalyticsEventStore;
import com.example.demo.audit.RegistrationAuditStore;
import com.example.demo.messaging.UserRegisteredEvent;
import com.example.demo.messaging.UserRegistrationEventPublisher;
import com.example.demo.notification.NotificationGateway;
import com.example.demo.observability.ApplicationMetrics;
import com.example.demo.profile.Region;
import com.example.demo.profile.UserProfileEntity;
import com.example.demo.profile.UserProfileRepository;
import com.example.demo.ratelimit.FixedWindowRateLimiter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrationServiceEventTest {

    @Test
    void registerPublishesDomainEventAfterSuccessfulRegistration() {
        UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);
        NotificationGateway notificationGateway = mock(NotificationGateway.class);
        AnalyticsEventStore analyticsEventStore = mock(AnalyticsEventStore.class);
        RegistrationAuditStore registrationAuditStore = mock(RegistrationAuditStore.class);
        FixedWindowRateLimiter rateLimiter = mock(FixedWindowRateLimiter.class);
        UserRegistrationEventPublisher eventPublisher = mock(UserRegistrationEventPublisher.class);
        Clock clock = Clock.fixed(Instant.parse("2026-03-12T00:00:00Z"), ZoneOffset.UTC);
        ApplicationMetrics applicationMetrics = new ApplicationMetrics(new SimpleMeterRegistry());
        RegistrationService registrationService = new RegistrationService(
                userProfileRepository,
                notificationGateway,
                analyticsEventStore,
                registrationAuditStore,
                rateLimiter,
                eventPublisher,
                clock,
                applicationMetrics
        );
        RegisterUserRequest request = new RegisterUserRequest("u-40", "u40@example.com", Region.APAC);

        when(rateLimiter.allow("test-client")).thenReturn(true);
        when(userProfileRepository.save(any(UserProfileEntity.class)))
                .thenReturn(new UserProfileEntity("u-40", "u40@example.com", Region.APAC));

        registrationService.register("test-client", request);

        verify(eventPublisher).publish(new UserRegisteredEvent(
                "u-40",
                "u40@example.com",
                "APAC",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        ));
    }
}
