package com.example.javalabs.basic;

/**
 * Publishes profile change events to downstream consumers.
 */
public interface UserProfileChangePublisher {

    /**
     * Publishes one profile change event.
     *
     * @param event event to publish
     * @throws IllegalArgumentException when {@code event} is {@code null}
     */
    void publish(UserProfileChangeEvent event);
}
