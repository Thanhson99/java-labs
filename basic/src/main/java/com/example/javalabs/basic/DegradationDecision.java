package com.example.javalabs.basic;

/**
 * Decision returned by graceful degradation.
 *
 * @param mode selected response mode
 * @param reason human-readable reason
 */
public record DegradationDecision(ResponseMode mode, String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when mode is {@code null} or reason is blank
     */
    public DegradationDecision {
        if (mode == null) {
            throw new IllegalArgumentException("mode must not be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
