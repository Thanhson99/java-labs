package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterMonitorTest {

    @Test
    void reportsHealthyWhenBacklogIsBelowWarningThreshold() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(10);
        store.record(delivery("one"), giveUpPlan());
        FeatureFlagReloadAlertDeadLetterMonitor monitor =
                new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0);

        FeatureFlagReloadAlertDeadLetterHealthReport report = monitor.analyze(store);

        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, report.status());
        assertTrue(report.healthy());
        assertEquals(1, report.backlogSize());
        assertEquals(10, report.capacity());
        assertEquals(0.1, report.utilization(), 0.0001);
        assertTrue(report.warnings().isEmpty());
    }

    @Test
    void reportsWarningWhenBacklogCrossesWarningThreshold() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(4);
        store.record(delivery("one"), giveUpPlan());
        store.record(delivery("two"), giveUpPlan());
        FeatureFlagReloadAlertDeadLetterMonitor monitor =
                new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0);

        FeatureFlagReloadAlertDeadLetterHealthReport report = monitor.analyze(store);

        assertEquals(FeatureFlagReloadHealthStatus.WARNING, report.status());
        assertFalse(report.healthy());
        assertEquals(0.5, report.utilization(), 0.0001);
        assertEquals(List.of("dead-letter backlog utilization is 50%"), report.warnings());
    }

    @Test
    void reportsCriticalWhenStoreIsFullOrRecordsWereDropped() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(2);
        store.record(delivery("one"), giveUpPlan());
        store.record(delivery("two"), giveUpPlan());
        store.record(delivery("three"), giveUpPlan());
        FeatureFlagReloadAlertDeadLetterMonitor monitor =
                new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0);

        FeatureFlagReloadAlertDeadLetterHealthReport report = monitor.analyze(store);

        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, report.status());
        assertEquals(2, report.backlogSize());
        assertEquals(2, report.capacity());
        assertEquals(1.0, report.utilization(), 0.0001);
        assertEquals(1, report.droppedCount());
        assertEquals(List.of(
                "dead-letter backlog utilization is 100%",
                "dead-letter store dropped 1 records"
        ), report.warnings());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(1);

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterMonitor(-0.1, 0.9, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterMonitor(0.6, 0.5, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 1.1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0).analyze(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterHealthReport(
                        null, 0, 1, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 2, 1, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 0, 1, 1.1, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 0, 1, 0, -1, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, 0, 1, 0, 0, null));

        assertEquals(1, store.capacity());
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
