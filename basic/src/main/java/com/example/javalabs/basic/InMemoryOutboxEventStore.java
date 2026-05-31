package com.example.javalabs.basic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory outbox store that keeps insertion order for predictable dispatching.
 */
public final class InMemoryOutboxEventStore implements OutboxEventStore {

    private final Map<String, OutboxEvent> eventsById = new LinkedHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(OutboxEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        eventsById.put(event.id(), event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OutboxEvent> findPending(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        return eventsById.values().stream()
                // Failed events remain retryable until a retry policy decides they are exhausted.
                .filter(event -> event.status() == OutboxEventStatus.PENDING || event.status() == OutboxEventStatus.FAILED)
                .limit(limit)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replace(OutboxEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (!eventsById.containsKey(event.id())) {
            throw new IllegalStateException("event not found: " + event.id());
        }
        eventsById.put(event.id(), event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OutboxEvent> findAll() {
        return List.copyOf(eventsById.values());
    }
}
