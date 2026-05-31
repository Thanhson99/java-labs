package com.example.javalabs.basic.autoscaling;

/**
 * Decision produced by {@link AutoscalingPolicy}.
 *
 * @param action recommended scaling action
 * @param targetInstances desired instance count after applying the action
 * @param reason human-readable explanation of the dominant signal
 */
public record AutoscalingDecision(
        ScalingAction action,
        int targetInstances,
        String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when action, target, or reason is invalid
     */
    public AutoscalingDecision {
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        if (targetInstances <= 0) {
            throw new IllegalArgumentException("targetInstances must be positive");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
