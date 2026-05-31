package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizerTest {

    @Test
    void summarizesRetainedIncidentsByStatusDeliveryAndChannel() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(5);
        log.record(alertResult("critical on-call", FeatureFlagReloadHealthStatus.CRITICAL,
                FeatureFlagReloadAlertChannel.ON_CALL, true));
        log.record(alertResult("warning dashboard", FeatureFlagReloadHealthStatus.WARNING,
                FeatureFlagReloadAlertChannel.DASHBOARD, true));
        log.record(alertResult("critical suppressed", FeatureFlagReloadHealthStatus.CRITICAL,
                FeatureFlagReloadAlertChannel.NONE, false));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer().summarize(log);

        assertEquals(3, summary.totalIncidents());
        assertEquals(2, summary.criticalIncidents());
        assertEquals(1, summary.warningIncidents());
        assertEquals(2, summary.deliveredIncidents());
        assertEquals(1, summary.undeliveredIncidents());
        assertEquals(1, summary.onCallIncidents());
        assertEquals(1, summary.dashboardIncidents());
        assertEquals(1, summary.suppressedIncidents());
        assertEquals(0, summary.droppedIncidents());
        assertEquals(2.0 / 3.0, summary.deliveryRate(), 0.0001);
        assertTrue(summary.hasUndeliveredIncidents());
    }

    @Test
    void includesDroppedIncidentCountFromBoundedLog() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(1);
        log.record(alertResult("first", FeatureFlagReloadHealthStatus.CRITICAL,
                FeatureFlagReloadAlertChannel.ON_CALL, true));
        log.record(alertResult("second", FeatureFlagReloadHealthStatus.CRITICAL,
                FeatureFlagReloadAlertChannel.ON_CALL, true));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer().summarize(log);

        assertEquals(1, summary.totalIncidents());
        assertEquals(1, summary.droppedIncidents());
        assertEquals(1.0, summary.deliveryRate(), 0.0001);
        assertFalse(summary.hasUndeliveredIncidents());
    }

    @Test
    void returnsZeroSummaryForEmptyLog() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(3);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer().summarize(log);

        assertEquals(0, summary.totalIncidents());
        assertEquals(0.0, summary.deliveryRate(), 0.0001);
        assertFalse(summary.hasUndeliveredIncidents());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer summarizer =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer();

        assertThrows(IllegalArgumentException.class, () -> summarizer.summarize(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        -1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        1, 1, 1, 1, 0, 1, 0, 0, 0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        1, 1, 0, 1, 1, 1, 0, 0, 0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        1, 1, 0, 1, 0, 1, 1, 0, 0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        1, 1, 0, 1, 0, 1, 0, 0, 0, 1.1));
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult alertResult(
            String warning,
            FeatureFlagReloadHealthStatus status,
            FeatureFlagReloadAlertChannel channel,
            boolean delivered) {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        status,
                        List.of(warning),
                        status == FeatureFlagReloadHealthStatus.CRITICAL ? 1.0 : 0.0,
                        delivered ? 0.0 : 1.0,
                        delivered ? 1.0 : 0.0
                );
        FeatureFlagReloadAlert alert = new FeatureFlagReloadAlert(
                true,
                status,
                "feature flag reload dead-letter alert workflow needs attention",
                List.of(warning)
        );
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                alert,
                delivered,
                delivered ? "alert emitted" : "alert cooldown active",
                1_000
        );
        FeatureFlagReloadAlertRoute route = new FeatureFlagReloadAlertRoute(
                channel,
                decision,
                delivered ? "alert routed" : "alert suppressed"
        );
        FeatureFlagReloadAlertDispatchResult dispatchResult = delivered
                ? FeatureFlagReloadAlertDispatchResult.delivered(new FeatureFlagReloadAlertDelivery(
                        channel,
                        status,
                        alert.message(),
                        alert.details()
                ))
                : FeatureFlagReloadAlertDispatchResult.skipped("alert suppressed");
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult(
                report,
                alert,
                decision,
                route,
                dispatchResult
        );
    }
}
