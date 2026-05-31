package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzerTest {

    @Test
    void reportsHealthyWhenNoWorkflowRunsExist() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer = analyzer();

        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report =
                analyzer.analyze(new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        0, 0, 0, 0, 0, 0, 0, 0));

        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, report.status());
        assertTrue(report.healthy());
        assertEquals(0.0, report.criticalRate(), 0.0001);
        assertEquals(0.0, report.suppressionRate(), 0.0001);
        assertEquals(0.0, report.deliveryRate(), 0.0001);
        assertTrue(report.warnings().isEmpty());
    }

    @Test
    void reportsWarningWhenCriticalRateIsElevated() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer = analyzer();

        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report =
                analyzer.analyze(new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        10, 7, 0, 3, 3, 0, 3, 7));

        assertEquals(FeatureFlagReloadHealthStatus.WARNING, report.status());
        assertEquals(0.3, report.criticalRate(), 0.0001);
        assertEquals(1.0, report.deliveryRate(), 0.0001);
        assertEquals(List.of("dead-letter alert workflow critical rate is elevated: 30%"),
                report.warnings());
    }

    @Test
    void reportsCriticalWhenSuppressionRateIsTooHigh() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer = analyzer();

        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report =
                analyzer.analyze(new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        10, 0, 0, 10, 10, 8, 2, 8));

        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, report.status());
        assertEquals(1.0, report.criticalRate(), 0.0001);
        assertEquals(0.8, report.suppressionRate(), 0.0001);
        assertEquals(0.2, report.deliveryRate(), 0.0001);
        assertEquals(List.of(
                "dead-letter alert workflow critical rate is 100%",
                "dead-letter alert workflow suppression rate is 80%"
        ), report.warnings());
    }

    @Test
    void reportsCriticalWhenCriticalAlertsNeverDeliver() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(0.9, 1.0, 0.9, 1.0);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report =
                analyzer.analyze(new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        4, 0, 0, 4, 4, 4, 0, 4));

        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, report.status());
        assertTrue(report.warnings().contains("critical dead-letter alerts were observed but none were delivered"));
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(-0.1, 0.8, 0.3, 0.7));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(0.5, 0.4, 0.3, 0.7));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(0.2, 0.8, 0.9, 0.7));
        assertThrows(IllegalArgumentException.class, () -> analyzer().analyze(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        null, List.of(), 0.0, 0.0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, null, 0.0, 0.0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY, List.of(), 1.1, 0.0, 0.0));
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer() {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(0.25, 0.75, 0.5, 0.8);
    }
}
