package com.example.javalabs.basic.featureflag;

/**
 * Full result of alerting on dead-letter alert workflow health.
 *
 * <p>The result keeps every stage of the pipeline so tests and operators can see whether an alert
 * was inactive, suppressed, routed, or delivered.</p>
 *
 * @param healthReport health derived from workflow metrics
 * @param alert alert payload produced by policy
 * @param decision suppression decision
 * @param route selected delivery route
 * @param dispatchResult final dispatch outcome
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult(
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport healthReport,
        FeatureFlagReloadAlert alert,
        FeatureFlagReloadAlertDecision decision,
        FeatureFlagReloadAlertRoute route,
        FeatureFlagReloadAlertDispatchResult dispatchResult) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when any pipeline stage is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult {
        if (healthReport == null) {
            throw new IllegalArgumentException("healthReport must not be null");
        }
        if (alert == null) {
            throw new IllegalArgumentException("alert must not be null");
        }
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
        if (route == null) {
            throw new IllegalArgumentException("route must not be null");
        }
        if (dispatchResult == null) {
            throw new IllegalArgumentException("dispatchResult must not be null");
        }
    }

    /**
     * @return {@code true} when the final dispatcher sent a payload to the sink
     */
    public boolean delivered() {
        return dispatchResult.delivered();
    }
}
