package com.example.javalabs.basic.featureflag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Immutable feature flag configuration snapshot.
 *
 * @param version monotonically increasing version number
 * @param rules rules captured in this snapshot
 */
public record FeatureFlagSnapshot(long version, Map<String, FeatureFlagRule> rules) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when version or rules are invalid
     */
    public FeatureFlagSnapshot {
        if (version <= 0) {
            throw new IllegalArgumentException("version must be positive");
        }
        if (rules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        rules = Map.copyOf(rules);
    }

    /**
     * Builds a snapshot from a rule list keyed by flag name.
     *
     * @param version snapshot version
     * @param rules rules to include
     * @return immutable snapshot
     * @throws IllegalArgumentException when inputs are invalid
     */
    public static FeatureFlagSnapshot fromRules(long version, List<FeatureFlagRule> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        Map<String, FeatureFlagRule> byName = rules.stream()
                .collect(Collectors.toMap(
                        FeatureFlagRule::flagName,
                        rule -> rule,
                        // Last duplicate wins to mirror reload-map behavior.
                        (first, second) -> second
                ));
        return new FeatureFlagSnapshot(version, byName);
    }

    /**
     * @return number of rules in this snapshot
     */
    public int size() {
        return rules.size();
    }
}
