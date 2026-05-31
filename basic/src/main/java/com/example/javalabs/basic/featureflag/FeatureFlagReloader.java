package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies a full feature flag config snapshot and reports the diff.
 *
 * <p>The reloader treats {@code newRules} as the desired complete state. Rules missing from the new
 * snapshot are removed, equal rules are counted as unchanged, and changed rules are upserted into
 * the registry.</p>
 */
public final class FeatureFlagReloader {

    private final FeatureFlagRegistry registry;

    /**
     * Creates a reloader that mutates the supplied registry.
     *
     * @param registry target feature flag registry
     * @throws IllegalArgumentException when {@code registry} is {@code null}
     */
    public FeatureFlagReloader(FeatureFlagRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        this.registry = registry;
    }

    /**
     * Applies a complete feature flag snapshot and reports added, updated, removed, and unchanged flags.
     *
     * @param newRules desired complete rule set
     * @return diff report describing the reload result
     * @throws IllegalArgumentException when {@code newRules} is {@code null} or contains {@code null}
     */
    public FeatureFlagReloadReport reload(List<FeatureFlagRule> newRules) {
        if (newRules == null) {
            throw new IllegalArgumentException("newRules must not be null");
        }

        Map<String, FeatureFlagRule> currentRules = registry.snapshot();
        Map<String, FeatureFlagRule> nextRules = new LinkedHashMap<>();
        for (FeatureFlagRule rule : newRules) {
            if (rule == null) {
                throw new IllegalArgumentException("newRules must not contain null");
            }
            // LinkedHashMap keeps the last duplicate rule by name and preserves deterministic order.
            nextRules.put(rule.flagName(), rule);
        }

        List<String> added = new ArrayList<>();
        List<String> updated = new ArrayList<>();
        List<String> unchanged = new ArrayList<>();
        for (FeatureFlagRule rule : nextRules.values()) {
            FeatureFlagRule current = currentRules.get(rule.flagName());
            if (current == null) {
                registry.upsert(rule);
                added.add(rule.flagName());
            } else if (!current.equals(rule)) {
                registry.upsert(rule);
                updated.add(rule.flagName());
            } else {
                unchanged.add(rule.flagName());
            }
        }

        List<String> removed = new ArrayList<>();
        for (String existingFlag : currentRules.keySet()) {
            if (!nextRules.containsKey(existingFlag)) {
                // Missing from the desired snapshot means the flag should no longer exist.
                registry.remove(existingFlag);
                removed.add(existingFlag);
            }
        }

        return new FeatureFlagReloadReport(added, updated, removed, unchanged);
    }
}
