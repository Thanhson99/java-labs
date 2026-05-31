package com.example.javalabs.basic.featureflag;

/**
 * Final decision after applying alert suppression.
 *
 * @param alert original alert from policy
 * @param emitted whether the alert should be sent now
 * @param reason short reason for the decision
 * @param nextAllowedAtMillis timestamp when the same alert can be emitted again
 */
public record FeatureFlagReloadAlertDecision(
        FeatureFlagReloadAlert alert,
        boolean emitted,
        String reason,
        long nextAllowedAtMillis) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when alert, reason, or timestamp is invalid
     */
    public FeatureFlagReloadAlertDecision {
        if (alert == null) {
            throw new IllegalArgumentException("alert must not be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        if (nextAllowedAtMillis < 0) {
            throw new IllegalArgumentException("nextAllowedAtMillis must not be negative");
        }
    }

    /**
     * @return {@code true} when this decision should not be delivered
     */
    public boolean suppressed() {
        return !emitted;
    }
}
