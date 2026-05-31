package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pre-flight checks for feature flag reloads.
 *
 * <p>The goal is to catch risky config before it mutates the registry: duplicate flag names,
 * overly large rollout jumps, and brand-new flags that start with too much traffic.</p>
 */
public final class FeatureFlagReloadValidator {

    private final int maxRolloutIncrease;
    private final int maxNewFlagRollout;

    /**
     * Creates a validator with rollout safety thresholds.
     *
     * @param maxRolloutIncrease maximum allowed increase for an existing flag
     * @param maxNewFlagRollout maximum allowed rollout for a brand-new enabled flag
     * @throws IllegalArgumentException when thresholds are outside 0..100
     */
    public FeatureFlagReloadValidator(int maxRolloutIncrease, int maxNewFlagRollout) {
        validatePercentage(maxRolloutIncrease, "maxRolloutIncrease");
        validatePercentage(maxNewFlagRollout, "maxNewFlagRollout");
        this.maxRolloutIncrease = maxRolloutIncrease;
        this.maxNewFlagRollout = maxNewFlagRollout;
    }

    /**
     * Validates a desired rule list against current rules.
     *
     * @param currentRules current rules keyed by flag name
     * @param newRules desired complete rule set
     * @return validation report containing blocking violations
     * @throws IllegalArgumentException when inputs are invalid
     */
    public FeatureFlagReloadValidationReport validate(
            Map<String, FeatureFlagRule> currentRules,
            List<FeatureFlagRule> newRules) {
        if (currentRules == null) {
            throw new IllegalArgumentException("currentRules must not be null");
        }
        if (newRules == null) {
            throw new IllegalArgumentException("newRules must not be null");
        }

        List<String> violations = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();
        for (FeatureFlagRule rule : newRules) {
            if (rule == null) {
                throw new IllegalArgumentException("newRules must not contain null");
            }
            if (!seenNames.add(rule.flagName())) {
                violations.add("duplicate flag: " + rule.flagName());
                continue;
            }

            FeatureFlagRule current = currentRules.get(rule.flagName());
            if (current == null) {
                // New flags are risky if they immediately start with too much enabled traffic.
                validateNewFlag(rule, violations);
            } else {
                validateRolloutJump(current, rule, violations);
            }
        }

        return new FeatureFlagReloadValidationReport(violations);
    }

    /**
     * Validates a desired rule list against a live registry snapshot.
     *
     * @param registry current feature flag registry
     * @param newRules desired complete rule set
     * @return validation report
     * @throws IllegalArgumentException when inputs are invalid
     */
    public FeatureFlagReloadValidationReport validate(FeatureFlagRegistry registry, List<FeatureFlagRule> newRules) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        return validate(registry.snapshot(), newRules);
    }

    /**
     * Adds a violation when a brand-new enabled flag starts too wide.
     */
    private void validateNewFlag(FeatureFlagRule rule, List<String> violations) {
        if (rule.enabled() && rule.rolloutPercentage() > maxNewFlagRollout) {
            violations.add("new flag " + rule.flagName()
                    + " rollout " + rule.rolloutPercentage()
                    + " exceeds max " + maxNewFlagRollout);
        }
    }

    /**
     * Adds a violation when an existing flag rollout increases too quickly.
     */
    private void validateRolloutJump(
            FeatureFlagRule current,
            FeatureFlagRule next,
            List<String> violations) {
        int increase = next.rolloutPercentage() - current.rolloutPercentage();
        if (next.enabled() && increase > maxRolloutIncrease) {
            violations.add("flag " + next.flagName()
                    + " rollout increase " + increase
                    + " exceeds max " + maxRolloutIncrease);
        }
    }

    /**
     * Validates a percentage threshold.
     *
     * @param value percentage value
     * @param name parameter name
     * @throws IllegalArgumentException when the value is outside 0..100
     */
    private static void validatePercentage(int value, String name) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(name + " must be between 0 and 100");
        }
    }
}
