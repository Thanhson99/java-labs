package com.example.javalabs.basic;

import java.util.List;

/**
 * Stores failed messages that need later inspection or replay.
 */
public interface DeadLetterStore {

    /**
     * Saves one failed side-effect message.
     *
     * @param message dead-letter message to retain
     * @throws IllegalArgumentException when {@code message} is {@code null}
     */
    void save(DeadLetterMessage message);

    /**
     * Returns retained dead-letter messages.
     *
     * @return immutable or defensive snapshot of retained messages
     */
    List<DeadLetterMessage> findAll();
}
