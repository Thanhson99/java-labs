package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitorTest {

    @Test
    void reportsHealthyWhenIncidentLogIsBelowThresholds() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(10);
        log.record(result("one", FeatureFlagReloadAlertChannel.ON_CALL, true));
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor = monitor();

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport report = monitor.analyze(log);

        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, report.status());
        assertTrue(report.healthy());
        assertEquals(1, report.incidentCount());
        assertEquals(10, report.capacity());
        assertEquals(0.1, report.utilization(), 0.0001);
        assertEquals(0, report.undeliveredCount());
        assertEquals(0, report.droppedCount());
        assertTrue(report.warnings().isEmpty());
    }

    @Test
    void reportsWarningWhenIncidentLogUtilizationIsElevated() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(4);
        log.record(result("one", FeatureFlagReloadAlertChannel.ON_CALL, true));
        log.record(result("two", FeatureFlagReloadAlertChannel.ON_CALL, true));
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor = monitor();

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport report = monitor.analyze(log);

        assertEquals(FeatureFlagReloadHealthStatus.WARNING, report.status());
        assertEquals(0.5, report.utilization(), 0.0001);
        assertEquals(List.of("incident log utilization is elevated: 50%"), report.warnings());
    }

    @Test
    void reportsCriticalForUndeliveredIncidentsAndDroppedHistory() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(2);
        log.record(result("one", FeatureFlagReloadAlertChannel.NONE, false));
        log.record(result("two", FeatureFlagReloadAlertChannel.NONE, false));
        log.record(result("three", FeatureFlagReloadAlertChannel.NONE, false));
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor = monitor();

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport report = monitor.analyze(log);

        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, report.status());
        assertEquals(2, report.incidentCount());
        assertEquals(1.0, report.utilization(), 0.0001);
        assertEquals(2, report.undeliveredCount());
        assertEquals(1, report.droppedCount());
        assertEquals(List.of(
                "incident log utilization is 100%",
                "incident log has 2 undelivered incidents",
                "incident log dropped 1 incidents"
        ), report.warnings());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(-0.1, 0.9, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(0.8, 0.7, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(0.5, 0.9, -1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(0.5, 0.9, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> monitor().analyze(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                        null, 0, 1, 0.0, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 2, 1, 0.0, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 1, 2, 1.1, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 1, 2, 0.5, 2, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 1, 2, 0.5, 0, 0, null));
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor() {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(0.5, 0.9, 1, 0);
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult result(
            String warning,
            FeatureFlagReloadAlertChannel channel,
            boolean delivered) {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        List.of(warning),
                        1.0,
                        delivered ? 0.0 : 1.0,
                        delivered ? 1.0 : 0.0
                );
        FeatureFlagReloadAlert alert = new FeatureFlagReloadAlert(
                true,
                FeatureFlagReloadHealthStatus.CRITICAL,
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
                delivered ? "critical reload alert routed to on-call" : "alert suppressed: alert cooldown active"
        );
        FeatureFlagReloadAlertDispatchResult dispatchResult = delivered
                ? FeatureFlagReloadAlertDispatchResult.delivered(new FeatureFlagReloadAlertDelivery(
                        channel,
                        FeatureFlagReloadHealthStatus.CRITICAL,
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
