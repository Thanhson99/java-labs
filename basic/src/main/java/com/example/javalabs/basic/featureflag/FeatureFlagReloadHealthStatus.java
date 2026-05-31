package com.example.javalabs.basic.featureflag;

/**
 * Operational health level for feature flag reload workflow metrics.
 *
 * <p>Health statuses are intentionally coarse because alert policies should make simple routing
 * decisions: no action, investigate soon, or page immediately.</p>
 */
public enum FeatureFlagReloadHealthStatus {
    /**
     * Metrics are inside expected operating bounds.
     */
    HEALTHY,

    /**
     * Metrics show degradation that should be visible to operators.
     */
    WARNING,

    /**
     * Metrics show a serious failure mode that requires urgent attention.
     */
    CRITICAL
}
