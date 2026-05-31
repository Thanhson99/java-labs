package com.example.javalabs.basic;

/**
 * Thrown when a write is based on an outdated version of a record.
 */
public final class OptimisticLockException extends RuntimeException {

    /**
     * Creates an optimistic-lock exception.
     *
     * @param message explanation of the version conflict
     */
    public OptimisticLockException(String message) {
        super(message);
    }
}
