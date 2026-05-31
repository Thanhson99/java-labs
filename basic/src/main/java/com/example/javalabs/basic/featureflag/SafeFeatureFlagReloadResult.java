package com.example.javalabs.basic.featureflag;

import java.util.Optional;

/**
 * Outcome of validating and optionally applying a feature flag reload.
 *
 * @param validationReport pre-flight validation result
 * @param reloadReport diff report when the reload was applied
 */
public record SafeFeatureFlagReloadResult(
        FeatureFlagReloadValidationReport validationReport,
        Optional<FeatureFlagReloadReport> reloadReport) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when validation or reload report container is {@code null}
     */
    public SafeFeatureFlagReloadResult {
        if (validationReport == null) {
            throw new IllegalArgumentException("validationReport must not be null");
        }
        if (reloadReport == null) {
            throw new IllegalArgumentException("reloadReport must not be null");
        }
    }

    /**
     * Creates a rejected reload result.
     *
     * @param validationReport validation report with violations
     * @return rejected result without a reload report
     * @throws IllegalArgumentException when {@code validationReport} is {@code null}
     */
    public static SafeFeatureFlagReloadResult rejected(FeatureFlagReloadValidationReport validationReport) {
        return new SafeFeatureFlagReloadResult(validationReport, Optional.empty());
    }

    /**
     * Creates an applied reload result.
     *
     * @param validationReport accepted validation report
     * @param reloadReport diff report from applying the reload
     * @return applied result with reload report
     * @throws IllegalArgumentException when {@code validationReport} is {@code null}
     * @throws IllegalArgumentException when {@code reloadReport} is {@code null}
     */
    public static SafeFeatureFlagReloadResult applied(
            FeatureFlagReloadValidationReport validationReport,
            FeatureFlagReloadReport reloadReport) {
        if (reloadReport == null) {
            throw new IllegalArgumentException("reloadReport must not be null");
        }
        return new SafeFeatureFlagReloadResult(validationReport, Optional.of(reloadReport));
    }

    /**
     * @return {@code true} when the reload was applied
     */
    public boolean applied() {
        return reloadReport.isPresent();
    }

    /**
     * @return {@code true} when validation rejected the reload
     */
    public boolean rejected() {
        return !applied();
    }
}
