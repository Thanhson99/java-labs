package com.example.javalabs.basic.autoscaling;

/**
 * Aggregate counters derived from retained autoscaling decisions.
 *
 * @param retainedEvents number of retained decision events
 * @param scaleOutCount number of retained scale-out decisions
 * @param scaleInCount number of retained scale-in decisions
 * @param holdCount number of retained hold decisions
 * @param droppedEvents number of older events dropped because capacity was exceeded
 */
public record AutoscalingDecisionLogSummary(
        int retainedEvents,
        int scaleOutCount,
        int scaleInCount,
        int holdCount,
        long droppedEvents) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when counters are negative or inconsistent
     */
    public AutoscalingDecisionLogSummary {
        if (retainedEvents < 0) {
            throw new IllegalArgumentException("retainedEvents must not be negative");
        }
        if (scaleOutCount < 0 || scaleInCount < 0 || holdCount < 0) {
            throw new IllegalArgumentException("decision counts must not be negative");
        }
        if (droppedEvents < 0) {
            throw new IllegalArgumentException("droppedEvents must not be negative");
        }
        if (scaleOutCount + scaleInCount + holdCount != retainedEvents) {
            throw new IllegalArgumentException("decision counts must equal retainedEvents");
        }
    }
}
