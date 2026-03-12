package com.example.javalabs.basic;

/**
 * Demonstrates a microservice-style application service.
 *
 * <p>The service coordinates validation, rate limiting, persistence, and a downstream client.
 * This mirrors how many backend services are structured even when the real code is more complex.</p>
 */
public final class RegistrationService {

    private final UserProfileRepository repository;
    private final NotificationClient notificationClient;
    private final FixedWindowRateLimiter rateLimiter;

    public RegistrationService(
            UserProfileRepository repository,
            NotificationClient notificationClient,
            FixedWindowRateLimiter rateLimiter) {
        this.repository = repository;
        this.notificationClient = notificationClient;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Registers a new user if the caller is still within the allowed request budget.
     *
     * @param callerKey the request source, such as an API key or IP address
     * @param userProfile the new user profile
     * @return a result describing whether the operation succeeded
     */
    public RegistrationResult register(String callerKey, UserProfile userProfile) {
        if (!rateLimiter.allow(callerKey)) {
            return new RegistrationResult(false, "rate limit exceeded");
        }

        repository.save(userProfile);
        notificationClient.sendWelcomeMessage(userProfile);
        return new RegistrationResult(true, "user registered");
    }
}
