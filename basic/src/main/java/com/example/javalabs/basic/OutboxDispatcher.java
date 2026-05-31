package com.example.javalabs.basic;

/**
 * Dispatches pending outbox events to a publisher.
 *
 * <p>The dispatcher is deliberately small: it reads pending events, publishes them, and writes the
 * new status back to the store. Keeping these steps explicit makes failure behavior easy to test.</p>
 */
public final class OutboxDispatcher {

    private final OutboxEventStore store;
    private final OutboxEventPublisher publisher;

    /**
     * Creates a dispatcher with an event store and downstream publisher.
     *
     * @param store outbox store that owns event state
     * @param publisher downstream event publisher
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public OutboxDispatcher(OutboxEventStore store, OutboxEventPublisher publisher) {
        if (store == null) {
            throw new IllegalArgumentException("store must not be null");
        }
        if (publisher == null) {
            throw new IllegalArgumentException("publisher must not be null");
        }
        this.store = store;
        this.publisher = publisher;
    }

    /**
     * Dispatches pending or failed events up to a caller-supplied limit.
     *
     * @param limit maximum number of events to dispatch in this run
     * @return number of events successfully published
     * @throws IllegalArgumentException when {@code limit} is not positive
     */
    public int dispatchPending(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        int dispatched = 0;
        for (OutboxEvent event : store.findPending(limit)) {
            try {
                publisher.publish(event);
                store.replace(event.markPublished());
                dispatched++;
            } catch (RuntimeException exception) {
                // Failed events stay visible for retry planning instead of disappearing.
                store.replace(event.markFailed());
            }
        }
        return dispatched;
    }
}
