package com.example.javalabs.basic;

/**
 * Tracks operation keys that have already produced a side effect.
 */
public interface IdempotencyStore {

    /**
     * Attempts to mark a key as processed.
     *
     * @param key stable operation key
     * @return true when this is the first time the key is seen
     * @throws IllegalArgumentException when {@code key} is blank
     */
    boolean markProcessing(String key);

    /**
     * Checks whether a key was already processed.
     *
     * @param key stable operation key
     * @return {@code true} when the key exists in the store
     * @throws IllegalArgumentException when {@code key} is blank
     */
    boolean wasProcessed(String key);
}
