package com.example.javalabs.basic.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies spike detection against a bounded rolling latency baseline.
 */
class LatencySpikeDetectorTest {

    /**
     * Confirms spike detection waits until enough baseline samples exist.
     */
    @Test
    void waitsForMinimumBaselineBeforeDetectingSpikes() {
        LatencySpikeDetector detector = new LatencySpikeDetector(5, 3, 2.0);

        assertFalse(detector.recordAndEvaluate(100).spike());
        assertFalse(detector.recordAndEvaluate(100).spike());
        assertFalse(detector.recordAndEvaluate(100).spike());

        LatencySpikeDecision decision = detector.recordAndEvaluate(250);

        assertTrue(decision.spike());
        assertEquals(100.0, decision.baselineAverageMillis());
        assertEquals(200.0, decision.thresholdMillis());
        assertEquals(3, decision.sampleCountBefore());
    }

    /**
     * Confirms normal samples are recorded without being flagged as spikes.
     */
    @Test
    void recordsNormalLatencyWithoutSpike() {
        LatencySpikeDetector detector = new LatencySpikeDetector(4, 2, 3.0);

        detector.recordAndEvaluate(50);
        detector.recordAndEvaluate(70);
        LatencySpikeDecision decision = detector.recordAndEvaluate(100);

        assertFalse(decision.spike());
        assertEquals(60.0, decision.baselineAverageMillis());
        assertEquals(180.0, decision.thresholdMillis());
        assertEquals(3, decision.snapshotAfter().sampleCount());
    }

    /**
     * Confirms the detector uses the rolling window and evicts old baseline samples.
     */
    @Test
    void usesRollingBaselineAfterEviction() {
        LatencySpikeDetector detector = new LatencySpikeDetector(3, 3, 2.0);

        detector.recordAndEvaluate(10);
        detector.recordAndEvaluate(10);
        detector.recordAndEvaluate(10);
        detector.recordAndEvaluate(100);
        detector.recordAndEvaluate(100);
        LatencySpikeDecision decision = detector.recordAndEvaluate(100);

        assertTrue(decision.baselineAverageMillis() > 60.0);
        assertFalse(decision.spike());
        assertEquals(3, decision.snapshotAfter().sampleCount());
    }

    /**
     * Confirms validation protects detector configuration and sample input.
     */
    @Test
    void rejectsInvalidConfigurationAndSamples() {
        assertThrows(IllegalArgumentException.class, () -> new LatencySpikeDetector(0, 1, 2.0));
        assertThrows(IllegalArgumentException.class, () -> new LatencySpikeDetector(2, 3, 2.0));
        assertThrows(IllegalArgumentException.class, () -> new LatencySpikeDetector(2, 1, 1.0));

        LatencySpikeDetector detector = new LatencySpikeDetector(2, 1, 2.0);
        assertThrows(IllegalArgumentException.class, () -> detector.recordAndEvaluate(-1));
    }
}
