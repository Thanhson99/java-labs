package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Alert decision derived from a feature flag reload health report.
 *
 * @param active whether an operator alert should be emitted
 * @param severity health status used as alert severity
 * @param message concise operator-facing message
 * @param details warning details copied from the health report
 */
public record FeatureFlagReloadAlert(
        boolean active,
        FeatureFlagReloadHealthStatus severity,
        String message,
        List<String> details) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when severity, message, or details are invalid
     */
    public FeatureFlagReloadAlert {
        if (severity == null) {
            throw new IllegalArgumentException("severity must not be null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (details == null) {
            throw new IllegalArgumentException("details must not be null");
        }
        details = List.copyOf(details);
    }

    /**
     * Creates an inactive healthy alert payload.
     *
     * @return inactive alert
     */
    public static FeatureFlagReloadAlert inactive() {
        return new FeatureFlagReloadAlert(
                false,
                FeatureFlagReloadHealthStatus.HEALTHY,
                "feature flag reload workflow is healthy",
                List.of()
        );
    }

    /**
     * Creates an active alert for non-healthy status.
     *
     * @param severity warning or critical severity
     * @param details operator-facing warning details
     * @return active alert
     * @throws IllegalArgumentException when severity is healthy
     */
    public static FeatureFlagReloadAlert active(
            FeatureFlagReloadHealthStatus severity,
            List<String> details) {
        if (severity == FeatureFlagReloadHealthStatus.HEALTHY) {
            throw new IllegalArgumentException("active alert severity must not be HEALTHY");
        }
        return new FeatureFlagReloadAlert(
                true,
                severity,
                "feature flag reload workflow needs attention",
                details
        );
    }
}
