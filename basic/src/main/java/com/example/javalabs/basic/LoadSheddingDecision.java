package com.example.javalabs.basic;

/**
 * Result of deciding whether a request should be accepted or shed.
 *
 * @param accepted true when the request should continue
 * @param reason human-readable reason
 */
public record LoadSheddingDecision(boolean accepted, String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when {@code reason} is blank
     */
    public LoadSheddingDecision {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
