package com.example.javalabs.basic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Dead-letter store with a fixed capacity.
 *
 * <p>Keeping every failed message in memory is risky during a long outage. This implementation
 * keeps the newest messages, drops the oldest ones when full, and exposes the drop count so the
 * loss is visible.</p>
 */
public final class BoundedDeadLetterStore implements DeadLetterStore {

    private final int capacity;
    private final ArrayDeque<DeadLetterMessage> messages = new ArrayDeque<>();
    private long droppedCount;

    /**
     * Creates a bounded in-memory dead-letter store.
     *
     * @param capacity maximum number of retained messages
     * @throws IllegalArgumentException when {@code capacity} is not positive
     */
    public BoundedDeadLetterStore(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * {@inheritDoc}
     *
     * <p>When the store is full, the oldest message is removed before the new message is added.</p>
     *
     * @throws IllegalArgumentException when {@code message} is {@code null}
     */
    @Override
    public void save(DeadLetterMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        if (messages.size() == capacity) {
            // Keep recent failures because they usually describe the current outage best.
            messages.removeFirst();
            droppedCount++;
        }
        messages.addLast(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DeadLetterMessage> findAll() {
        return List.copyOf(new ArrayList<>(messages));
    }

    /**
     * Returns the number of retained messages.
     *
     * @return current store size
     */
    public int size() {
        return messages.size();
    }

    /**
     * Returns the configured maximum retained messages.
     *
     * @return store capacity
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns how many messages were dropped because the store was full.
     *
     * @return total dropped message count
     */
    public long droppedCount() {
        return droppedCount;
    }
}
