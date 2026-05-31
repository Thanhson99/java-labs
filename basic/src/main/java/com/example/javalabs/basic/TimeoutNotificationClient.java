package com.example.javalabs.basic;

/**
 * Timeout decorator for {@link NotificationClient}.
 *
 * <p>This learning version measures elapsed time through {@link TimeSource}. In production, a real
 * HTTP or messaging client should also enforce network-level timeouts so the call is interrupted,
 * not only measured after it returns.</p>
 */
public final class TimeoutNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final long timeoutMillis;
    private final TimeSource timeSource;

    /**
     * Creates a timeout-measuring notification client.
     *
     * @param delegate downstream notification client
     * @param timeoutMillis maximum allowed elapsed time in milliseconds
     * @param timeSource clock used to measure elapsed time
     * @throws IllegalArgumentException when dependencies or timeout are invalid
     */
    public TimeoutNotificationClient(NotificationClient delegate, long timeoutMillis, TimeSource timeSource) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("timeoutMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.delegate = delegate;
        this.timeoutMillis = timeoutMillis;
        this.timeSource = timeSource;
    }

    /**
     * Sends a welcome message and fails if the call exceeds the configured timeout.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     * @throws IllegalStateException when elapsed time is greater than the timeout
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        long startedAt = timeSource.currentTimeMillis();
        delegate.sendWelcomeMessage(userProfile);
        long elapsedMillis = timeSource.currentTimeMillis() - startedAt;
        if (elapsedMillis > timeoutMillis) {
            throw new IllegalStateException("notification timed out after " + elapsedMillis + " ms");
        }
    }
}
