package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts reload metrics into an operator-friendly health report.
 */
public final class FeatureFlagReloadHealthAnalyzer {

    private final double warningBlockRate;
    private final double criticalBlockRate;
    private final double warningRejectionRate;
    private final double criticalRejectionRate;

    /**
     * Creates a health analyzer with warning and critical thresholds.
     *
     * @param warningBlockRate block-rate threshold for warning health
     * @param criticalBlockRate block-rate threshold for critical health
     * @param warningRejectionRate rejection-rate threshold for warning health
     * @param criticalRejectionRate rejection-rate threshold for critical health
     * @throws IllegalArgumentException when thresholds are outside 0..1 or warning exceeds critical
     */
    public FeatureFlagReloadHealthAnalyzer(
            double warningBlockRate,
            double criticalBlockRate,
            double warningRejectionRate,
            double criticalRejectionRate) {
        validateThreshold(warningBlockRate, "warningBlockRate");
        validateThreshold(criticalBlockRate, "criticalBlockRate");
        validateThreshold(warningRejectionRate, "warningRejectionRate");
        validateThreshold(criticalRejectionRate, "criticalRejectionRate");
        if (warningBlockRate > criticalBlockRate) {
            throw new IllegalArgumentException("warningBlockRate must be <= criticalBlockRate");
        }
        if (warningRejectionRate > criticalRejectionRate) {
            throw new IllegalArgumentException("warningRejectionRate must be <= criticalRejectionRate");
        }
        this.warningBlockRate = warningBlockRate;
        this.criticalBlockRate = criticalBlockRate;
        this.warningRejectionRate = warningRejectionRate;
        this.criticalRejectionRate = criticalRejectionRate;
    }

    /**
     * Converts reload metrics into an operator-facing health report.
     *
     * @param snapshot metrics snapshot to analyze
     * @return health report with status, warnings, and derived rates
     * @throws IllegalArgumentException when {@code snapshot} is {@code null}
     */
    public FeatureFlagReloadHealthReport analyze(FeatureFlagReloadMetricsSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot must not be null");
        }

        double blockRate = ratio(snapshot.rateLimitedBlocks(), snapshot.flushedAttempts());
        int changedConfigs = snapshot.safeReloadApplied() + snapshot.safeReloadRejected();
        double rejectionRate = ratio(snapshot.safeReloadRejected(), changedConfigs);
        double skipRate = ratio(snapshot.fingerprintSkips(), snapshot.flushedAttempts());

        List<String> warnings = new ArrayList<>();
        FeatureFlagReloadHealthStatus status = FeatureFlagReloadHealthStatus.HEALTHY;

        if (blockRate >= criticalBlockRate) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("reload block rate is critical: " + formatRate(blockRate));
        } else if (blockRate >= warningBlockRate) {
            status = max(status, FeatureFlagReloadHealthStatus.WARNING);
            warnings.add("reload block rate is elevated: " + formatRate(blockRate));
        }

        if (rejectionRate >= criticalRejectionRate) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("reload rejection rate is critical: " + formatRate(rejectionRate));
        } else if (rejectionRate >= warningRejectionRate) {
            status = max(status, FeatureFlagReloadHealthStatus.WARNING);
            warnings.add("reload rejection rate is elevated: " + formatRate(rejectionRate));
        }

        if (snapshot.submissions() > 0 && snapshot.flushedAttempts() == 0) {
            // Submitted work that never reaches a flush can indicate debounce timing or scheduler issues.
            status = max(status, FeatureFlagReloadHealthStatus.WARNING);
            warnings.add("config submissions are waiting without flushed attempts");
        }

        return new FeatureFlagReloadHealthReport(status, warnings, blockRate, rejectionRate, skipRate);
    }

    private static FeatureFlagReloadHealthStatus max(
            FeatureFlagReloadHealthStatus current,
            FeatureFlagReloadHealthStatus candidate) {
        if (current == FeatureFlagReloadHealthStatus.CRITICAL || candidate == FeatureFlagReloadHealthStatus.CRITICAL) {
            return FeatureFlagReloadHealthStatus.CRITICAL;
        }
        if (current == FeatureFlagReloadHealthStatus.WARNING || candidate == FeatureFlagReloadHealthStatus.WARNING) {
            return FeatureFlagReloadHealthStatus.WARNING;
        }
        return FeatureFlagReloadHealthStatus.HEALTHY;
    }

    /**
     * Calculates a safe ratio when a denominator may be zero.
     */
    private static double ratio(int numerator, int denominator) {
        if (denominator == 0) {
            return 0.0;
        }
        return (double) numerator / denominator;
    }

    /**
     * Formats a rate as a whole percentage for operator messages.
     */
    private static String formatRate(double value) {
        return Math.round(value * 100.0) + "%";
    }

    /**
     * Validates a threshold ratio.
     *
     * @throws IllegalArgumentException when {@code value} is outside 0..1
     */
    private static void validateThreshold(double value, String name) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
