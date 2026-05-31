package com.example.javalabs.basic.featureflag;

/**
 * Full result of evaluating and delivering a dead-letter backlog alert.
 *
 * <p>Keeping all intermediate values makes the workflow easy to debug and easy to test: callers can
 * inspect whether the alert was inactive, suppressed, routed, or delivered.</p>
 *
 * @param healthReport dead-letter backlog health analysis
 * @param alert alert payload produced by policy
 * @param decision suppression decision
 * @param route selected delivery route
 * @param dispatchResult final dispatch outcome
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowResult(
        FeatureFlagReloadAlertDeadLetterHealthReport healthReport,
        FeatureFlagReloadAlert alert,
        FeatureFlagReloadAlertDecision decision,
        FeatureFlagReloadAlertRoute route,
        FeatureFlagReloadAlertDispatchResult dispatchResult) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when any workflow component is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowResult {
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
     * @return {@code true} when the dispatcher delivered an alert payload
     */
    public boolean delivered() {
        return dispatchResult.delivered();
    }
}
