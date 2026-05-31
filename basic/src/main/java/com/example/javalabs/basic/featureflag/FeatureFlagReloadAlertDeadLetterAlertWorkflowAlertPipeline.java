package com.example.javalabs.basic.featureflag;

/**
 * Alerts when the dead-letter alert workflow itself becomes unhealthy.
 *
 * <p>This is a meta-alert pipeline: it watches the pipeline that sends dead-letter backlog alerts.
 * The implementation stays thin and delegates analysis, policy, suppression, routing, and delivery
 * to focused collaborators.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline {

    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer;
    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy;
    private final FeatureFlagReloadAlertSuppressor suppressor;
    private final FeatureFlagReloadAlertRouter router;
    private final FeatureFlagReloadAlertDispatcher dispatcher;

    /**
     * Creates the alert pipeline from focused workflow components.
     *
     * @param analyzer converts workflow metrics into health
     * @param policy converts health into active or inactive alerts
     * @param suppressor applies duplicate-alert cooldown
     * @param router selects the delivery channel
     * @param dispatcher sends deliverable routes to a sink
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer,
            FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy,
            FeatureFlagReloadAlertSuppressor suppressor,
            FeatureFlagReloadAlertRouter router,
            FeatureFlagReloadAlertDispatcher dispatcher) {
        if (analyzer == null) {
            throw new IllegalArgumentException("analyzer must not be null");
        }
        if (policy == null) {
            throw new IllegalArgumentException("policy must not be null");
        }
        if (suppressor == null) {
            throw new IllegalArgumentException("suppressor must not be null");
        }
        if (router == null) {
            throw new IllegalArgumentException("router must not be null");
        }
        if (dispatcher == null) {
            throw new IllegalArgumentException("dispatcher must not be null");
        }
        this.analyzer = analyzer;
        this.policy = policy;
        this.suppressor = suppressor;
        this.router = router;
        this.dispatcher = dispatcher;
    }

    /**
     * Runs health analysis, alert policy, suppression, routing, and dispatch for one snapshot.
     *
     * @param snapshot immutable workflow metrics snapshot
     * @return full result containing every stage of the alert pipeline
     * @throws IllegalArgumentException when {@code snapshot} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult run(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot must not be null");
        }

        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport healthReport =
                analyzer.analyze(snapshot);
        FeatureFlagReloadAlert alert = policy.evaluate(healthReport);
        FeatureFlagReloadAlertDecision decision = suppressor.evaluate(alert);
        FeatureFlagReloadAlertRoute route = router.route(decision);
        FeatureFlagReloadAlertDispatchResult dispatchResult = dispatcher.dispatch(route);
        // Preserve every pipeline stage so meta-alert behavior is transparent to tests and operators.
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult(
                healthReport,
                alert,
                decision,
                route,
                dispatchResult
        );
    }
}
