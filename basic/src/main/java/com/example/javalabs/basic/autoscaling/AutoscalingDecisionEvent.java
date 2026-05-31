package com.example.javalabs.basic.autoscaling;

/**
 * Timestamped autoscaling decision retained for diagnostics.
 *
 * @param timestampMillis time when the decision was recorded
 * @param decision autoscaling decision payload
 */
public record AutoscalingDecisionEvent(
        long timestampMillis,
        AutoscalingDecision decision) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when timestamp or decision is invalid
     */
    public AutoscalingDecisionEvent {
        if (timestampMillis < 0) {
            throw new IllegalArgumentException("timestampMillis must not be negative");
        }
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
    }
}
