package com.example.javalabs.basic;

/**
 * Result of deciding whether a retry should be attempted.
 *
 * @param allowed true when the caller can retry
 * @param reason human-readable reason for the decision
 */
public record RetryDecision(boolean allowed, String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when {@code reason} is blank
     */
    public RetryDecision {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
