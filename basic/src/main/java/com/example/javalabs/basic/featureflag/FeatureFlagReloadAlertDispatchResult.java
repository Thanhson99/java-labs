package com.example.javalabs.basic.featureflag;

import java.util.Optional;

/**
 * Result of dispatching a routed feature flag reload alert.
 *
 * @param delivered whether an alert payload was sent to the sink
 * @param reason short reason for the outcome
 * @param delivery delivered payload when applicable
 */
public record FeatureFlagReloadAlertDispatchResult(
        boolean delivered,
        String reason,
        Optional<FeatureFlagReloadAlertDelivery> delivery) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when reason or delivery container is invalid
     */
    public FeatureFlagReloadAlertDispatchResult {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        if (delivery == null) {
            throw new IllegalArgumentException("delivery must not be null");
        }
    }

    /**
     * Creates a skipped dispatch result.
     *
     * @param reason skip reason
     * @return skipped dispatch result
     */
    public static FeatureFlagReloadAlertDispatchResult skipped(String reason) {
        return new FeatureFlagReloadAlertDispatchResult(false, reason, Optional.empty());
    }

    /**
     * Creates a delivered dispatch result.
     *
     * @param delivery delivered payload
     * @return delivered dispatch result
     * @throws IllegalArgumentException when {@code delivery} is {@code null}
     */
    public static FeatureFlagReloadAlertDispatchResult delivered(FeatureFlagReloadAlertDelivery delivery) {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery must not be null");
        }
        return new FeatureFlagReloadAlertDispatchResult(true, "alert delivered", Optional.of(delivery));
    }
}
