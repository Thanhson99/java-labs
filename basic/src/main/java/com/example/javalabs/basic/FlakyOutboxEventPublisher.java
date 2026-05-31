package com.example.javalabs.basic;

/**
 * Publisher that fails a configured number of times before succeeding.
 */
public final class FlakyOutboxEventPublisher implements OutboxEventPublisher {

    private final int failuresBeforeSuccess;
    private final InMemoryOutboxEventPublisher delegate = new InMemoryOutboxEventPublisher();
    private int attemptCount;

    /**
     * Creates a publisher that fails before eventually succeeding.
     *
     * @param failuresBeforeSuccess number of initial publish calls that should fail
     * @throws IllegalArgumentException when {@code failuresBeforeSuccess} is negative
     */
    public FlakyOutboxEventPublisher(int failuresBeforeSuccess) {
        if (failuresBeforeSuccess < 0) {
            throw new IllegalArgumentException("failuresBeforeSuccess must not be negative");
        }
        this.failuresBeforeSuccess = failuresBeforeSuccess;
    }

    /**
     * Publishes an event after the configured failures are exhausted.
     *
     * @param event event to publish
     * @throws IllegalArgumentException when {@code event} is {@code null}
     * @throws IllegalStateException while the fake publisher is configured to fail
     */
    @Override
    public void publish(OutboxEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        attemptCount++;
        if (attemptCount <= failuresBeforeSuccess) {
            throw new IllegalStateException("temporary publish failure");
        }
        delegate.publish(event);
    }

    /**
     * @return total publish attempts observed by this fake publisher
     */
    public int attemptCount() {
        return attemptCount;
    }

    /**
     * @return successfully published event count
     */
    public int publishedCount() {
        return delegate.publishedEvents().size();
    }
}
