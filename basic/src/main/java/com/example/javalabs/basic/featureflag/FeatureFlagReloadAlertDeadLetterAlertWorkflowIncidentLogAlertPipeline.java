package com.example.javalabs.basic.featureflag;

/**
 * Alerts when the incident log for the dead-letter alert workflow becomes unhealthy.
 *
 * <p>This is another thin orchestration layer: the monitor owns health analysis, the policy owns
 * alert decisions, the suppressor owns duplicate control, the router owns channel selection, and
 * the dispatcher owns delivery.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline {

    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor;
    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy;
    private final FeatureFlagReloadAlertSuppressor suppressor;
    private final FeatureFlagReloadAlertRouter router;
    private final FeatureFlagReloadAlertDispatcher dispatcher;

    /**
     * Creates an incident-log alert pipeline from focused collaborators.
     *
     * @param monitor analyzes incident-log health
     * @param policy converts health into active or inactive alerts
     * @param suppressor applies duplicate-alert cooldown
     * @param router selects the delivery channel
     * @param dispatcher sends deliverable routes to a sink
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor,
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy,
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
     * Runs health analysis, alert policy, suppression, routing, and dispatch for one incident log.
     *
     * @param log incident log to analyze
     * @return full result containing every stage of the alert pipeline
     * @throws IllegalArgumentException when {@code log} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult run(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log) {
        if (log == null) {
            throw new IllegalArgumentException("log must not be null");
        }

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport healthReport =
                monitor.analyze(log);
        FeatureFlagReloadAlert alert = policy.evaluate(healthReport);
        FeatureFlagReloadAlertDecision decision = suppressor.evaluate(alert);
        FeatureFlagReloadAlertRoute route = router.route(decision);
        FeatureFlagReloadAlertDispatchResult dispatchResult = dispatcher.dispatch(route);
        // Preserve each stage so callers can tell whether health, policy, suppression, routing, or delivery stopped.
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult(
                healthReport,
                alert,
                decision,
                route,
                dispatchResult
        );
    }
}
