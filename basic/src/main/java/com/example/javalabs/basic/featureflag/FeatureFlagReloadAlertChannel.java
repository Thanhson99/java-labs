package com.example.javalabs.basic.featureflag;

/**
 * Destination for a feature flag reload alert after suppression.
 */
public enum FeatureFlagReloadAlertChannel {
    /**
     * No delivery should occur.
     */
    NONE,

    /**
     * Non-paging dashboard or operations UI destination.
     */
    DASHBOARD,

    /**
     * Paging destination for critical alerts.
     */
    ON_CALL
}
