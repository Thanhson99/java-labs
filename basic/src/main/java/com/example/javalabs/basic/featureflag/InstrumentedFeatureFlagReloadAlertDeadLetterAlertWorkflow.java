package com.example.javalabs.basic.featureflag;

/**
 * Records metrics around the dead-letter alert workflow without changing its decisions.
 *
 * <p>This decorator is useful for learning the instrumentation pattern: wrap a working component,
 * observe the result, and leave the wrapped component's behavior untouched.</p>
 */
public final class InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow {

    private final FeatureFlagReloadAlertDeadLetterAlertWorkflow workflow;
    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics metrics;

    /**
     * Creates an instrumented workflow wrapper.
     *
     * @param workflow workflow to execute
     * @param metrics metrics sink that records each result
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow(
            FeatureFlagReloadAlertDeadLetterAlertWorkflow workflow,
            FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics metrics) {
        if (workflow == null) {
            throw new IllegalArgumentException("workflow must not be null");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("metrics must not be null");
        }
        this.workflow = workflow;
        this.metrics = metrics;
    }

    /**
     * Runs the wrapped workflow and records its outcome.
     *
     * @param store dead-letter store analyzed by the wrapped workflow
     * @return original workflow result
     * @throws IllegalArgumentException when {@code store} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowResult run(
            FeatureFlagReloadAlertDeadLetterStore store) {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowResult result = workflow.run(store);
        metrics.record(result);
        return result;
    }

    /**
     * @return immutable snapshot of all outcomes recorded so far
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot metricsSnapshot() {
        return metrics.snapshot();
    }
}
