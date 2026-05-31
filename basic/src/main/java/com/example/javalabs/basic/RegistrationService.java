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

    /**
     * Creates a registration service with explicit infrastructure dependencies.
     *
     * @param repository storage abstraction for user profiles
     * @param notificationClient downstream client used for welcome messages
     * @param rateLimiter caller-level request limiter
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public RegistrationService(
            UserProfileRepository repository,
            NotificationClient notificationClient,
            FixedWindowRateLimiter rateLimiter) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        if (notificationClient == null) {
            throw new IllegalArgumentException("notificationClient must not be null");
        }
        if (rateLimiter == null) {
            throw new IllegalArgumentException("rateLimiter must not be null");
        }
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
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    public RegistrationResult register(String callerKey, UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        if (!rateLimiter.allow(callerKey)) {
            return new RegistrationResult(false, "rate limit exceeded");
        }

        // Persist first, then notify: tests can see the service orchestration order clearly.
        repository.save(userProfile);
        notificationClient.sendWelcomeMessage(userProfile);
        return new RegistrationResult(true, "user registered");
    }
}
