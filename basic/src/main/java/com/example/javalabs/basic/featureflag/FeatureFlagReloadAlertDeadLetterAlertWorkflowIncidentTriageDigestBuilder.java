package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.TimeSource;

/**
 * Builds incident triage digests from the bounded incident log.
 *
 * <p>The builder is a small orchestration layer. It composes summarizing, planning, formatting, and
 * timestamping without changing the incident log, which keeps each step independently testable.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder {

    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer summarizer;
    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner planner;
    private final FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter formatter;
    private final TimeSource timeSource;

    /**
     * Creates a digest builder with explicit dependencies.
     *
     * @param summarizer converts the incident log into counters
     * @param planner converts counters into prioritized actions
     * @param formatter renders the action plan as deterministic text
     * @param timeSource supplies testable timestamps for generated digests
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer summarizer,
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner planner,
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter formatter,
            TimeSource timeSource) {
        if (summarizer == null) {
            throw new IllegalArgumentException("summarizer must not be null");
        }
        if (planner == null) {
            throw new IllegalArgumentException("planner must not be null");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("formatter must not be null");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.summarizer = summarizer;
        this.planner = planner;
        this.formatter = formatter;
        this.timeSource = timeSource;
    }

    /**
     * Builds a digest from the current incident-log snapshot.
     *
     * @param log incident log to summarize and triage
     * @return immutable digest ready for logs, exports, or dashboard previews
     * @throws IllegalArgumentException when {@code log} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest build(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log) {
        if (log == null) {
            throw new IllegalArgumentException("log must not be null");
        }

        // Each derived view is rebuilt from the current log snapshot, leaving the log authoritative.
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary = summarizer.summarize(log);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan = planner.plan(summary);
        String formattedPlan = formatter.format(plan);
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest(
                timeSource.currentTimeMillis(),
                summary,
                plan,
                formattedPlan
        );
    }
}


