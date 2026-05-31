package com.example.javalabs.basic;

/**
 * Retry decorator for {@link NotificationClient}.
 *
 * <p>Retry is useful for temporary downstream failures such as a short network interruption.
 * The important boundary is the maximum attempt count: retry should reduce transient failures,
 * not hide permanent bugs or loop forever.</p>
 */
public final class ResilientNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final int maxAttempts;

    /**
     * Creates a retrying notification client.
     *
     * @param delegate downstream notification client
     * @param maxAttempts maximum number of total attempts, including the first call
     * @throws IllegalArgumentException when {@code delegate} is {@code null} or attempts are invalid
     */
    public ResilientNotificationClient(NotificationClient delegate, int maxAttempts) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive");
        }
        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Sends a welcome message, retrying transient runtime failures up to the configured limit.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     * @throws IllegalStateException when all retry attempts fail
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                delegate.sendWelcomeMessage(userProfile);
                return;
            } catch (RuntimeException exception) {
                // Keep the last failure as the cause so debugging still points at the real error.
                lastFailure = exception;
            }
        }

        throw new IllegalStateException("notification failed after " + maxAttempts + " attempts", lastFailure);
    }
}
