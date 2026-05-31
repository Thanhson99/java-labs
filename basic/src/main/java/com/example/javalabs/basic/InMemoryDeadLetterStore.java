package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory dead-letter store for learning and tests.
 */
public final class InMemoryDeadLetterStore implements DeadLetterStore {

    private final List<DeadLetterMessage> messages = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(DeadLetterMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        messages.add(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DeadLetterMessage> findAll() {
        return List.copyOf(messages);
    }

    /**
     * @return number of retained dead-letter messages
     */
    public int size() {
        return messages.size();
    }
}
