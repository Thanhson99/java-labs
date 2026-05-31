package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Validates a feature flag snapshot before applying it.
 *
 * <p>This keeps validation and mutation in one safe workflow: invalid config returns a rejected
 * result and leaves the registry unchanged.</p>
 */
public final class SafeFeatureFlagReloader {

    private final FeatureFlagRegistry registry;
    private final FeatureFlagReloadValidator validator;
    private final FeatureFlagReloader reloader;

    /**
     * Creates a safe reload workflow.
     *
     * @param registry registry that should be mutated only after validation passes
     * @param validator pre-flight validator
     * @throws IllegalArgumentException when dependencies are {@code null}
     */
    public SafeFeatureFlagReloader(FeatureFlagRegistry registry, FeatureFlagReloadValidator validator) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        if (validator == null) {
            throw new IllegalArgumentException("validator must not be null");
        }
        this.registry = registry;
        this.validator = validator;
        this.reloader = new FeatureFlagReloader(registry);
    }

    /**
     * Validates and applies a desired feature flag snapshot.
     *
     * @param newRules desired complete rule set
     * @return rejected result when validation fails, otherwise applied reload result
     * @throws IllegalArgumentException when {@code newRules} is invalid
     */
    public SafeFeatureFlagReloadResult reload(List<FeatureFlagRule> newRules) {
        FeatureFlagReloadValidationReport validationReport = validator.validate(registry, newRules);
        if (validationReport.rejected()) {
            // Do not mutate live registry when validation reports blocking violations.
            return SafeFeatureFlagReloadResult.rejected(validationReport);
        }
        return SafeFeatureFlagReloadResult.applied(validationReport, reloader.reload(newRules));
    }
}
