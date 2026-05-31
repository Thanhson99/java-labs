package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TimeSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflowTest {

    @Test
    void recordsHealthyDeliveredAndSuppressedOutcomes() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        ManualTimeSource timeSource = new ManualTimeSource(0);
        InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow workflow =
                instrumentedWorkflow(sink, timeSource);

        workflow.run(new FeatureFlagReloadAlertDeadLetterStore(5));
        FeatureFlagReloadAlertDeadLetterStore criticalStore = new FeatureFlagReloadAlertDeadLetterStore(1);
        criticalStore.record(delivery("first"), giveUpPlan());
        workflow.run(criticalStore);
        workflow.run(criticalStore);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot snapshot =
                workflow.metricsSnapshot();
        assertEquals(3, snapshot.runs());
        assertEquals(1, snapshot.healthyReports());
        assertEquals(0, snapshot.warningReports());
        assertEquals(2, snapshot.criticalReports());
        assertEquals(2, snapshot.activeAlerts());
        assertEquals(2, snapshot.suppressedAlerts());
        assertEquals(1, snapshot.deliveredAlerts());
        assertEquals(2, snapshot.skippedDispatches());
        assertEquals(1, sink.size());
    }

    @Test
    void exposesImmutableSnapshotAfterEachRun() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow workflow =
                instrumentedWorkflow(sink, new ManualTimeSource(0));
        FeatureFlagReloadAlertDeadLetterStore criticalStore = new FeatureFlagReloadAlertDeadLetterStore(1);
        criticalStore.record(delivery("first"), giveUpPlan());

        FeatureFlagReloadAlertDeadLetterAlertWorkflowResult result = workflow.run(criticalStore);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot snapshot =
                workflow.metricsSnapshot();

        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, result.healthReport().status());
        assertEquals(1, snapshot.runs());
        assertEquals(1, snapshot.criticalReports());
        assertEquals(1, snapshot.deliveredAlerts());
    }

    @Test
    void rejectsInvalidInputs() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflow workflow =
                baseWorkflow(sink, new ManualTimeSource(0));
        FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics metrics =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics();

        assertThrows(IllegalArgumentException.class,
                () -> new InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow(null, metrics));
        assertThrows(IllegalArgumentException.class,
                () -> new InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow(workflow, null));
        assertThrows(IllegalArgumentException.class, () -> metrics.record(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        -1, 0, 0, 0, 0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        1, 1, 1, 0, 0, 0, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        1, 1, 0, 0, 2, 0, 0, 1));
    }

    private static InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow instrumentedWorkflow(
            InMemoryFeatureFlagReloadAlertSink sink,
            ManualTimeSource timeSource) {
        return new InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow(
                baseWorkflow(sink, timeSource),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics()
        );
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflow baseWorkflow(
            InMemoryFeatureFlagReloadAlertSink sink,
            ManualTimeSource timeSource) {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0),
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(true),
                new FeatureFlagReloadAlertSuppressor(1_000, timeSource),
                new FeatureFlagReloadAlertRouter(),
                new FeatureFlagReloadAlertDispatcher(sink)
        );
    }

    private static FeatureFlagReloadAlertDelivery delivery(String detail) {
        return new FeatureFlagReloadAlertDelivery(
                FeatureFlagReloadAlertChannel.ON_CALL,
                FeatureFlagReloadHealthStatus.CRITICAL,
                "feature flag reload workflow needs attention",
                List.of(detail)
        );
    }

    private static FeatureFlagReloadAlertRetryPlan giveUpPlan() {
        return new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.GIVE_UP,
                3,
                2_000,
                "max alert delivery attempts exhausted"
        );
    }
}

