package com.example.javalabs.basic;

/**
 * Abstraction over time so rate-limiting code is easy to test deterministically.
 */
public interface TimeSource {

    /**
     * Returns the current time in milliseconds.
     *
     * @return the current timestamp in milliseconds
     */
    long currentTimeMillis();
}
