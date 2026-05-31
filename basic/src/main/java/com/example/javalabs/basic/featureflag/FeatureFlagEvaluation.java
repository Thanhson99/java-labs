package com.example.javalabs.basic.featureflag;

/**
 * Result of evaluating a feature flag for one user.
 *
 * @param enabled whether the feature is enabled
 * @param bucket stable user bucket from 0 to 99
 * @param reason human-readable reason
 */
public record FeatureFlagEvaluation(boolean enabled, int bucket, String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when bucket or reason is invalid
     */
    public FeatureFlagEvaluation {
        if (bucket < 0 || bucket > 99) {
            throw new IllegalArgumentException("bucket must be between 0 and 99");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
