package com.example.javalabs.basic.featureflag;

/**
 * Retry decision for a failed feature flag reload alert delivery.
 */
public enum FeatureFlagReloadAlertRetryDecision {
    /**
     * Schedule another delivery attempt later.
     */
    RETRY_LATER,

    /**
     * Stop retrying and move the delivery to dead-letter storage.
     */
    GIVE_UP
}
