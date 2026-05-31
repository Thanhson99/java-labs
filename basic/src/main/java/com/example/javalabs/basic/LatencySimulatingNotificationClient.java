package com.example.javalabs.basic;

/**
 * Learning double that advances a manual clock to make latency visible in tests.
 */
public final class LatencySimulatingNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final ManualTimeSource timeSource;
    private final long latencyMillis;

    /**
     * Creates a client that simulates elapsed time before delegating.
     *
     * @param delegate downstream notification client
     * @param timeSource manual clock to advance
     * @param latencyMillis simulated latency in milliseconds
     * @throws IllegalArgumentException when dependencies or latency are invalid
     */
    public LatencySimulatingNotificationClient(
            NotificationClient delegate,
            ManualTimeSource timeSource,
            long latencyMillis) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        if (latencyMillis < 0) {
            throw new IllegalArgumentException("latencyMillis must not be negative");
        }
        this.delegate = delegate;
        this.timeSource = timeSource;
        this.latencyMillis = latencyMillis;
    }

    /**
     * Advances the manual clock and then sends the welcome message.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        // Advance before the delegate returns so timeout/instrumentation wrappers can measure it.
        timeSource.advanceMillis(latencyMillis);
        delegate.sendWelcomeMessage(userProfile);
    }
}
