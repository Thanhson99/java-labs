package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Result of checking a feature flag config before reload.
 *
 * @param violations problems that should block the reload
 */
public record FeatureFlagReloadValidationReport(List<String> violations) {

    /**
     * Validates and defensively copies the generated record constructor argument.
     *
     * @throws IllegalArgumentException when {@code violations} is {@code null}
     */
    public FeatureFlagReloadValidationReport {
        if (violations == null) {
            throw new IllegalArgumentException("violations must not be null");
        }
        violations = List.copyOf(violations);
    }

    /**
     * @return {@code true} when validation found no blocking violations
     */
    public boolean accepted() {
        return violations.isEmpty();
    }

    /**
     * @return {@code true} when validation should block the reload
     */
    public boolean rejected() {
        return !accepted();
    }
}
