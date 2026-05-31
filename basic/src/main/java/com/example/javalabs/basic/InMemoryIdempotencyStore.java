package com.example.javalabs.basic;

import java.util.HashSet;
import java.util.Set;

/**
 * In-memory idempotency store for learning and tests.
 */
public final class InMemoryIdempotencyStore implements IdempotencyStore {

    private final Set<String> processedKeys = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markProcessing(String key) {
        validateKey(key);
        return processedKeys.add(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasProcessed(String key) {
        validateKey(key);
        return processedKeys.contains(key);
    }

    /**
     * @return number of unique processed keys retained in memory
     */
    public int processedCount() {
        return processedKeys.size();
    }

    /**
     * Validates an idempotency key.
     *
     * @param key stable operation key
     * @throws IllegalArgumentException when {@code key} is blank
     */
    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
    }
}
