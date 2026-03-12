package com.example.demo.registration;

import com.example.demo.analytics.AnalyticsEventStore;
import com.example.demo.messaging.UserRegisteredEvent;
import com.example.demo.messaging.UserRegistrationEventPublisher;
import com.example.demo.observability.ApplicationMetrics;
import com.example.demo.audit.RegistrationAuditStore;
import com.example.demo.notification.NotificationGateway;
import com.example.demo.profile.UserProfileEntity;
import com.example.demo.profile.UserProfileRepository;
import com.example.demo.ratelimit.FixedWindowRateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Application service that coordinates rate limiting, persistence, analytics, and notifications.
 */
@Service
public class RegistrationService {

    private final UserProfileRepository userProfileRepository;
    private final NotificationGateway notificationGateway;
    private final AnalyticsEventStore analyticsEventStore;
    private final RegistrationAuditStore registrationAuditStore;
    private final FixedWindowRateLimiter rateLimiter;
    private final UserRegistrationEventPublisher eventPublisher;
    private final Clock clock;
    private final ApplicationMetrics applicationMetrics;

    public RegistrationService(
            UserProfileRepository userProfileRepository,
            NotificationGateway notificationGateway,
            AnalyticsEventStore analyticsEventStore,
            RegistrationAuditStore registrationAuditStore,
            FixedWindowRateLimiter rateLimiter,
            UserRegistrationEventPublisher eventPublisher,
            Clock clock,
            ApplicationMetrics applicationMetrics) {
        this.userProfileRepository = userProfileRepository;
        this.notificationGateway = notificationGateway;
        this.analyticsEventStore = analyticsEventStore;
        this.registrationAuditStore = registrationAuditStore;
        this.rateLimiter = rateLimiter;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
        this.applicationMetrics = applicationMetrics;
    }

    /**
     * Registers a user when the caller is still inside the request budget.
     *
     * @param callerKey client identity, usually an API key or caller ID
     * @param request validated API request
     * @return a structured registration result
     */
    @Transactional
    public RegistrationResult register(String callerKey, RegisterUserRequest request) {
        return registerInternal(callerKey, request, false);
    }

    /**
     * Executes a registration flow that can intentionally fail after writing to the primary
     * database, so transaction rollback can be observed in tests and during study.
     *
     * @param callerKey client identity, usually an API key or caller ID
     * @param request validated API request
     * @param failAfterAudit whether to throw after writing an audit row
     * @return a structured registration result when no failure is requested
     */
    @Transactional
    public RegistrationResult registerWithRollbackDemo(String callerKey, RegisterUserRequest request, boolean failAfterAudit) {
        return registerInternal(callerKey, request, failAfterAudit);
    }

    private RegistrationResult registerInternal(String callerKey, RegisterUserRequest request, boolean failAfterAudit) {
        Instant startedAt = clock.instant();
        if (!rateLimiter.allow(callerKey)) {
            applicationMetrics.recordRegistrationRateLimited();
            throw new RateLimitExceededException("rate limit exceeded for caller " + callerKey);
        }

        try {
            UserProfileEntity entity = new UserProfileEntity(request.userId(), request.email(), request.region());
            UserProfileEntity saved = userProfileRepository.save(entity);
            registrationAuditStore.record(saved.getUserId(), "REGISTERED");

            if (failAfterAudit) {
                applicationMetrics.recordRegistrationFailure();
                throw new IllegalStateException("simulated failure after audit write");
            }

            analyticsEventStore.record("USER_REGISTERED", saved.getUserId(), saved.getRegion().name());
            notificationGateway.sendWelcome(saved);
            eventPublisher.publish(new UserRegisteredEvent(
                    saved.getUserId(),
                    saved.getEmail(),
                    saved.getRegion().name(),
                    clock.instant(),
                    "registration-service"
            ));

            applicationMetrics.recordRegistrationSuccess();
            return new RegistrationResult(true, "user registered", UserProfileResponse.fromEntity(saved));
        } finally {
            applicationMetrics.recordRegistrationDuration(Duration.between(startedAt, clock.instant()));
        }
    }
}
