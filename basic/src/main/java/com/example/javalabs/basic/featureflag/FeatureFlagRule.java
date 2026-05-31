package com.example.javalabs.basic.featureflag;

/**
 * Rollout configuration for one feature flag.
 *
 * @param flagName stable flag name
 * @param enabled global on/off switch
 * @param rolloutPercentage percentage of users included in rollout
 */
public record FeatureFlagRule(String flagName, boolean enabled, int rolloutPercentage) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when the flag name is blank or rollout is outside 0..100
     */
    public FeatureFlagRule {
        if (flagName == null || flagName.isBlank()) {
            throw new IllegalArgumentException("flagName must not be blank");
        }
        if (rolloutPercentage < 0 || rolloutPercentage > 100) {
            throw new IllegalArgumentException("rolloutPercentage must be between 0 and 100");
        }
    }
}
