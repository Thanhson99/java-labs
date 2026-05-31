package com.example.javalabs.basic;

/**
 * Metrics decorator for {@link NotificationClient}.
 *
 * <p>Optimization work should be measured. This wrapper records call count, success count, failure
 * count, and total duration without changing the business service that uses the client.</p>
 */
public final class InstrumentedNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final TimeSource timeSource;
    private int totalCalls;
    private int successfulCalls;
    private int failedCalls;
    private long totalDurationMillis;

    /**
     * Creates a metrics wrapper around a notification client.
     *
     * @param delegate downstream notification client
     * @param timeSource clock used to measure elapsed time
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public InstrumentedNotificationClient(NotificationClient delegate, TimeSource timeSource) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.delegate = delegate;
        this.timeSource = timeSource;
    }

    /**
     * Sends a welcome message and records call metrics.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        long startedAt = timeSource.currentTimeMillis();
        totalCalls++;
        try {
            delegate.sendWelcomeMessage(userProfile);
            successfulCalls++;
        } catch (RuntimeException exception) {
            failedCalls++;
            throw exception;
        } finally {
            long finishedAt = timeSource.currentTimeMillis();
            // Guard against non-monotonic fake clocks so metrics never accumulate negative duration.
            totalDurationMillis += Math.max(0, finishedAt - startedAt);
        }
    }

    /**
     * @return immutable snapshot of collected call metrics
     */
    public ClientCallMetrics metrics() {
        return new ClientCallMetrics(totalCalls, successfulCalls, failedCalls, totalDurationMillis);
    }
}
