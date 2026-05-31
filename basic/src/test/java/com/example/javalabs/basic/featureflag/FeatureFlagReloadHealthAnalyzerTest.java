package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadHealthAnalyzerTest {

    @Test
    void reportsHealthyWhenRatesAreBelowWarningThresholds() {
        FeatureFlagReloadHealthAnalyzer analyzer = analyzer();

        FeatureFlagReloadHealthReport report = analyzer.analyze(new FeatureFlagReloadMetricsSnapshot(
                5,
                1,
                1,
                10,
                1,
                2,
                7,
                1
        ));

        assertTrue(report.healthy());
        assertFalse(report.needsAttention());
        assertEquals(List.of(), report.warnings());
        assertEquals(0.10, report.blockRate(), 0.001);
        assertEquals(0.125, report.rejectionRate(), 0.001);
        assertEquals(0.20, report.skipRate(), 0.001);
    }

    @Test
    void warnsWhenBlockRateIsElevated() {
        FeatureFlagReloadHealthReport report = analyzer().analyze(new FeatureFlagReloadMetricsSnapshot(
                5,
                0,
                0,
                10,
                3,
                0,
                7,
                0
        ));

        assertEquals(FeatureFlagReloadHealthStatus.WARNING, report.status());
        assertEquals(List.of("reload block rate is elevated: 30%"), report.warnings());
    }

    @Test
    void reportsCriticalWhenRejectionRateIsTooHigh() {
        FeatureFlagReloadHealthReport report = analyzer().analyze(new FeatureFlagReloadMetricsSnapshot(
                5,
                0,
                0,
                10,
                1,
                0,
                2,
                3
        ));

        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, report.status());
        assertEquals(List.of("reload rejection rate is critical: 60%"), report.warnings());
    }

    @Test
    void warnsWhenSubmissionsNeverFlush() {
        FeatureFlagReloadHealthReport report = analyzer().analyze(new FeatureFlagReloadMetricsSnapshot(
                3,
                0,
                3,
                0,
                0,
                0,
                0,
                0
        ));

        assertEquals(FeatureFlagReloadHealthStatus.WARNING, report.status());
        assertEquals(List.of("config submissions are waiting without flushed attempts"), report.warnings());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadHealthAnalyzer(-0.1, 0.5, 0.2, 0.5));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadHealthAnalyzer(0.6, 0.5, 0.2, 0.5));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadHealthAnalyzer(0.2, 0.5, 0.6, 0.5));
        assertThrows(IllegalArgumentException.class, () -> analyzer().analyze(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadHealthReport(null, List.of(), 0.0, 0.0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadHealthReport(FeatureFlagReloadHealthStatus.HEALTHY, null, 0.0, 0.0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadHealthReport(FeatureFlagReloadHealthStatus.HEALTHY, List.of(), -0.1, 0.0, 0.0));
    }

    private static FeatureFlagReloadHealthAnalyzer analyzer() {
        return new FeatureFlagReloadHealthAnalyzer(0.20, 0.50, 0.20, 0.50);
    }
}
