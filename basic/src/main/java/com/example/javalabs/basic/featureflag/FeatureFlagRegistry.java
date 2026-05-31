package com.example.javalabs.basic.featureflag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores feature flag rules as an immutable snapshot.
 *
 * <p>Callers can read a snapshot without being able to mutate internal state. Updates return
 * whether the rule changed so reload jobs can skip unnecessary work.</p>
 */
public final class FeatureFlagRegistry {

    private final Map<String, FeatureFlagRule> rulesByName = new LinkedHashMap<>();

    /**
     * Creates a registry from an initial rule set.
     *
     * @param initialRules rules to load into the registry
     * @throws IllegalArgumentException when {@code initialRules} is {@code null} or contains invalid rules
     */
    public FeatureFlagRegistry(List<FeatureFlagRule> initialRules) {
        if (initialRules == null) {
            throw new IllegalArgumentException("initialRules must not be null");
        }
        for (FeatureFlagRule rule : initialRules) {
            upsert(rule);
        }
    }

    /**
     * Looks up a rule by flag name.
     *
     * @param flagName feature flag name
     * @return optional rule when the flag exists
     * @throws IllegalArgumentException when {@code flagName} is blank
     */
    public Optional<FeatureFlagRule> find(String flagName) {
        validateFlagName(flagName);
        return Optional.ofNullable(rulesByName.get(flagName));
    }

    /**
     * Finds an existing rule or returns a safe disabled rule.
     *
     * @param flagName feature flag name
     * @return existing rule, or a disabled zero-rollout rule for missing flags
     * @throws IllegalArgumentException when {@code flagName} is blank
     */
    public FeatureFlagRule findOrDisabled(String flagName) {
        validateFlagName(flagName);
        return rulesByName.getOrDefault(flagName, new FeatureFlagRule(flagName, false, 0));
    }

    /**
     * Inserts or replaces a rule only when it changed.
     *
     * @param rule rule to store
     * @return {@code true} when the registry changed; {@code false} when the same rule already existed
     * @throws IllegalArgumentException when {@code rule} is {@code null}
     */
    public boolean upsert(FeatureFlagRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("rule must not be null");
        }
        FeatureFlagRule current = rulesByName.get(rule.flagName());
        if (rule.equals(current)) {
            return false;
        }
        // Preserve insertion order so snapshots and demos are deterministic.
        rulesByName.put(rule.flagName(), rule);
        return true;
    }

    /**
     * Removes a rule from the registry.
     *
     * @param flagName feature flag name
     * @return {@code true} when an existing rule was removed
     * @throws IllegalArgumentException when {@code flagName} is blank
     */
    public boolean remove(String flagName) {
        validateFlagName(flagName);
        return rulesByName.remove(flagName) != null;
    }

    /**
     * Evaluates a named rule with a supplied evaluator.
     *
     * @param flagName feature flag name
     * @param userId stable user identifier
     * @param evaluator evaluator used to apply rollout rules
     * @return feature flag evaluation result
     * @throws IllegalArgumentException when {@code evaluator} is {@code null} or names are invalid
     */
    public FeatureFlagEvaluation evaluate(String flagName, String userId, FeatureFlagEvaluator evaluator) {
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator must not be null");
        }
        return evaluator.evaluate(findOrDisabled(flagName), userId);
    }

    /**
     * Returns an immutable snapshot of current rules.
     *
     * @return immutable rules keyed by flag name
     */
    public Map<String, FeatureFlagRule> snapshot() {
        return Map.copyOf(rulesByName);
    }

    /**
     * @return number of rules currently stored
     */
    public int size() {
        return rulesByName.size();
    }

    /**
     * Validates the common feature-flag name invariant.
     *
     * @param flagName feature flag name to validate
     * @throws IllegalArgumentException when {@code flagName} is blank
     */
    private static void validateFlagName(String flagName) {
        if (flagName == null || flagName.isBlank()) {
            throw new IllegalArgumentException("flagName must not be blank");
        }
    }
}
