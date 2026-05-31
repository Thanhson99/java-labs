package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory publisher used by examples and tests.
 */
public final class InMemoryUserProfileChangePublisher implements UserProfileChangePublisher {

    private final List<UserProfileChangeEvent> events = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(UserProfileChangeEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        events.add(event);
    }

    /**
     * @return immutable snapshot of published events
     */
    public List<UserProfileChangeEvent> publishedEvents() {
        return List.copyOf(events);
    }

    /**
     * @return number of published events
     */
    public int publishCount() {
        return events.size();
    }
}
