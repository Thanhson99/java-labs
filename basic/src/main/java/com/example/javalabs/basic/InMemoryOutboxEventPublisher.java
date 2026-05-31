package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory publisher that records published events.
 */
public final class InMemoryOutboxEventPublisher implements OutboxEventPublisher {

    private final List<OutboxEvent> publishedEvents = new ArrayList<>();

    /**
     * Records an event as published.
     *
     * @param event event to publish
     * @throws IllegalArgumentException when {@code event} is {@code null}
     */
    @Override
    public void publish(OutboxEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        publishedEvents.add(event);
    }

    /**
     * @return immutable snapshot of published events
     */
    public List<OutboxEvent> publishedEvents() {
        return List.copyOf(publishedEvents);
    }
}
