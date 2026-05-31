package com.example.javalabs.basic;

/**
 * Result of a profile update attempt.
 *
 * @param changed whether data was actually written
 * @param message human-readable outcome
 */
public record ProfileUpdateResult(boolean changed, String message) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when {@code message} is blank
     */
    public ProfileUpdateResult {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }
}
