package com.example.javalabs.basic;

/**
 * Publishes outbox events to a downstream system.
 */
public interface OutboxEventPublisher {

    /**
     * Publishes one outbox event to a downstream system.
     *
     * @param event event to publish
     * @throws IllegalArgumentException when {@code event} is {@code null}
     * @throws RuntimeException when the downstream publish fails
     */
    void publish(OutboxEvent event);
}
