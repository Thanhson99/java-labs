package com.example.javalabs.basic;

/**
 * Circuit breaker decorator for {@link NotificationClient}.
 *
 * <p>When a downstream service keeps failing, the breaker opens and rejects calls immediately.
 * After a cooldown, it allows one trial call in half-open state. A successful trial closes the
 * breaker; a failed trial opens it again.</p>
 */
public final class CircuitBreakerNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final int failureThreshold;
    private final long cooldownMillis;
    private final TimeSource timeSource;

    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private int consecutiveFailures;
    private long openedAtMillis;

    /**
     * Creates a circuit breaker around a notification client.
     *
     * @param delegate downstream notification client
     * @param failureThreshold consecutive failures required to open the breaker
     * @param cooldownMillis time before an open breaker allows a half-open trial
     * @param timeSource clock used to make cooldown behavior deterministic in tests
     * @throws IllegalArgumentException when dependencies or limits are invalid
     */
    public CircuitBreakerNotificationClient(
            NotificationClient delegate,
            int failureThreshold,
            long cooldownMillis,
            TimeSource timeSource) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (failureThreshold <= 0) {
            throw new IllegalArgumentException("failureThreshold must be positive");
        }
        if (cooldownMillis <= 0) {
            throw new IllegalArgumentException("cooldownMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }

        this.delegate = delegate;
        this.failureThreshold = failureThreshold;
        this.cooldownMillis = cooldownMillis;
        this.timeSource = timeSource;
    }

    /**
     * Sends a welcome message unless the circuit is currently open.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     * @throws IllegalStateException when the breaker is open
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        moveToHalfOpenIfCooldownPassed();
        if (state == CircuitBreakerState.OPEN) {
            throw new IllegalStateException("circuit breaker is open");
        }

        try {
            delegate.sendWelcomeMessage(userProfile);
            recordSuccess();
        } catch (RuntimeException exception) {
            recordFailure();
            throw exception;
        }
    }

    /**
     * Returns the current breaker state after applying any cooldown transition.
     *
     * @return current circuit breaker state
     */
    public CircuitBreakerState state() {
        moveToHalfOpenIfCooldownPassed();
        return state;
    }

    /**
     * @return number of consecutive failures observed while closed or half-open
     */
    public int consecutiveFailures() {
        return consecutiveFailures;
    }

    /**
     * Records a successful downstream call and closes the breaker.
     */
    private void recordSuccess() {
        consecutiveFailures = 0;
        state = CircuitBreakerState.CLOSED;
    }

    /**
     * Records a failed downstream call and opens the breaker when the threshold is reached.
     */
    private void recordFailure() {
        consecutiveFailures++;
        if (state == CircuitBreakerState.HALF_OPEN || consecutiveFailures >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
            openedAtMillis = timeSource.currentTimeMillis();
        }
    }

    /**
     * Moves an open breaker to half-open when its cooldown window has elapsed.
     */
    private void moveToHalfOpenIfCooldownPassed() {
        if (state != CircuitBreakerState.OPEN) {
            return;
        }
        long now = timeSource.currentTimeMillis();
        if (now - openedAtMillis >= cooldownMillis) {
            state = CircuitBreakerState.HALF_OPEN;
        }
    }
}
