package com.example.javalabs.basic.featureflag;

/**
 * Runs the complete dead-letter backlog alert flow from health analysis to dispatch.
 *
 * <p>The workflow is intentionally thin. It does not decide thresholds, suppression rules, routing
 * rules, or delivery behavior itself; it composes small classes that each own one decision.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflow {

    private final FeatureFlagReloadAlertDeadLetterMonitor monitor;
    private final FeatureFlagReloadAlertDeadLetterAlertPolicy policy;
    private final FeatureFlagReloadAlertSuppressor suppressor;
    private final FeatureFlagReloadAlertRouter router;
    private final FeatureFlagReloadAlertDispatcher dispatcher;

    /**
     * Creates the workflow from focused collaborators.
     *
     * @param monitor analyzes dead-letter backlog health
     * @param policy converts health into active or inactive alerts
     * @param suppressor suppresses repeated alerts during cooldown
     * @param router selects the alert channel
     * @param dispatcher delivers routed alerts to a sink
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflow(
            FeatureFlagReloadAlertDeadLetterMonitor monitor,
            FeatureFlagReloadAlertDeadLetterAlertPolicy policy,
            FeatureFlagReloadAlertSuppressor suppressor,
            FeatureFlagReloadAlertRouter router,
            FeatureFlagReloadAlertDispatcher dispatcher) {
        if (monitor == null) {
            throw new IllegalArgumentException("monitor must not be null");
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
        this.monitor = monitor;
        this.policy = policy;
        this.suppressor = suppressor;
        this.router = router;
        this.dispatcher = dispatcher;
    }

    /**
     * Executes one full alert evaluation pass for a dead-letter store.
     *
     * @param store dead-letter store to analyze
     * @return full workflow result with every intermediate decision
     * @throws IllegalArgumentException when {@code store} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowResult run(
            FeatureFlagReloadAlertDeadLetterStore store) {
        if (store == null) {
            throw new IllegalArgumentException("store must not be null");
        }

        FeatureFlagReloadAlertDeadLetterHealthReport healthReport = monitor.analyze(store);
        FeatureFlagReloadAlert alert = policy.evaluate(healthReport);
        FeatureFlagReloadAlertDecision decision = suppressor.evaluate(alert);
        FeatureFlagReloadAlertRoute route = router.route(decision);
        FeatureFlagReloadAlertDispatchResult dispatchResult = dispatcher.dispatch(route);
        // Return every intermediate value so tests and operators can see where the flow stopped.
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowResult(
                healthReport,
                alert,
                decision,
                route,
                dispatchResult
        );
    }
}
