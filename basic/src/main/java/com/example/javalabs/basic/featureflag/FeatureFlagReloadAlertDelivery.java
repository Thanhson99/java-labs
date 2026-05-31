package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Delivered alert payload for a route.
 *
 * @param channel destination channel
 * @param severity alert severity
 * @param message alert message
 * @param details alert details
 */
public record FeatureFlagReloadAlertDelivery(
        FeatureFlagReloadAlertChannel channel,
        FeatureFlagReloadHealthStatus severity,
        String message,
        List<String> details) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when the route payload is invalid
     */
    public FeatureFlagReloadAlertDelivery {
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }
        if (channel == FeatureFlagReloadAlertChannel.NONE) {
            throw new IllegalArgumentException("channel must be deliverable");
        }
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
}
