package com.example.javalabs.basic;

import java.util.List;

/**
 * Store for events that should be published after the main data write.
 */
public interface OutboxEventStore {

    /**
     * Saves a new outbox event.
     *
     * @param event event to store
     * @throws IllegalArgumentException when {@code event} is {@code null}
     */
    void save(OutboxEvent event);

    /**
     * Finds publishable pending work.
     *
     * @param limit maximum number of events to return
     * @return pending or retryable failed events in deterministic order
     * @throws IllegalArgumentException when {@code limit} is not positive
     */
    List<OutboxEvent> findPending(int limit);

    /**
     * Replaces an existing event with updated status or attempt count.
     *
     * @param event replacement event
     * @throws IllegalArgumentException when {@code event} is {@code null}
     * @throws IllegalStateException when the event id is not already stored
     */
    void replace(OutboxEvent event);

    /**
     * Returns all stored events.
     *
     * @return immutable or defensive snapshot of the store
     */
    List<OutboxEvent> findAll();
}
