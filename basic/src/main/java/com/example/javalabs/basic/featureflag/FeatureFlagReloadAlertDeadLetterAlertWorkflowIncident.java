package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Audit-friendly incident captured from a dead-letter alert workflow alert result.
 *
 * <p>The incident deliberately stores a small immutable summary instead of the entire pipeline
 * result. This keeps the audit record stable and prevents callers from depending on every internal
 * object used by the alert pipeline.</p>
 *
 * @param status workflow health status at the time of the alert
 * @param channel selected alert channel
 * @param delivered whether the alert was delivered to the sink
 * @param message alert message
 * @param warnings health warnings that triggered the alert
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident(
        FeatureFlagReloadHealthStatus status,
        FeatureFlagReloadAlertChannel channel,
        boolean delivered,
        String message,
        List<String> warnings) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when status, channel, message, or warnings are invalid
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (status == FeatureFlagReloadHealthStatus.HEALTHY) {
            throw new IllegalArgumentException("status must not be HEALTHY");
        }
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (warnings == null) {
            throw new IllegalArgumentException("warnings must not be null");
        }
        warnings = List.copyOf(warnings);
    }
}
