package com.example.demo.registration;

import com.example.demo.analytics.AnalyticsEventStore;
import com.example.demo.notification.NotificationGateway;
import com.example.demo.profile.UserProfileEntity;
import com.example.demo.profile.UserProfileRepository;
import com.example.demo.ratelimit.FixedWindowRateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service that coordinates rate limiting, persistence, analytics, and notifications.
 */
@Service
public class RegistrationService {

    private final UserProfileRepository userProfileRepository;
    private final NotificationGateway notificationGateway;
    private final AnalyticsEventStore analyticsEventStore;
    private final FixedWindowRateLimiter rateLimiter;

    public RegistrationService(
            UserProfileRepository userProfileRepository,
            NotificationGateway notificationGateway,
            AnalyticsEventStore analyticsEventStore,
            FixedWindowRateLimiter rateLimiter) {
        this.userProfileRepository = userProfileRepository;
        this.notificationGateway = notificationGateway;
        this.analyticsEventStore = analyticsEventStore;
        this.rateLimiter = rateLimiter;
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
        if (!rateLimiter.allow(callerKey)) {
            throw new RateLimitExceededException("rate limit exceeded for caller " + callerKey);
        }

        UserProfileEntity entity = new UserProfileEntity(request.userId(), request.email(), request.region());
        UserProfileEntity saved = userProfileRepository.save(entity);
        analyticsEventStore.record("USER_REGISTERED", saved.getUserId(), saved.getRegion().name());
        notificationGateway.sendWelcome(saved);

        return new RegistrationResult(true, "user registered", UserProfileResponse.fromEntity(saved));
    }
}
