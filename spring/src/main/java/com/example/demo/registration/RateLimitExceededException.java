package com.example.demo.registration;

/**
 * Exception thrown when a caller exceeds the configured request budget.
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}
