package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.metrics.ErrorRateSnapshot;
import com.example.javalabs.basic.metrics.LatencyPercentileSnapshot;
import com.example.javalabs.basic.metrics.ThroughputSnapshot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies autoscaling decisions from throughput, latency, and error-rate metrics.
 */
class AutoscalingPolicyTest {

    /**
     * Confirms unhealthy error rate takes scale-out priority.
     */
    @Test
    void scalesOutWhenErrorRateIsUnhealthy() {
        AutoscalingPolicy policy = policy();

        AutoscalingDecision decision = policy.evaluate(
                2,
                new ThroughputSnapshot(10, 10_000, 1.0),
                new LatencyPercentileSnapshot(10, 0.95, 100),
                new ErrorRateSnapshot(10, 5, 0.50, false)
        );

        assertEquals(ScalingAction.SCALE_OUT, decision.action());
        assertEquals(3, decision.targetInstances());
        assertEquals("error rate unhealthy", decision.reason());
    }

    /**
     * Confirms high throughput can trigger scale-out.
     */
    @Test
    void scalesOutWhenThroughputIsHigh() {
        AutoscalingDecision decision = policy().evaluate(
                2,
                new ThroughputSnapshot(1_000, 10_000, 100.0),
                new LatencyPercentileSnapshot(10, 0.95, 100),
                new ErrorRateSnapshot(10, 0, 0.0, true)
        );

        assertEquals(ScalingAction.SCALE_OUT, decision.action());
        assertEquals(3, decision.targetInstances());
    }

    /**
     * Confirms high latency can trigger scale-out even when throughput is moderate.
     */
    @Test
    void scalesOutWhenLatencyIsHigh() {
        AutoscalingDecision decision = policy().evaluate(
                2,
                new ThroughputSnapshot(100, 10_000, 10.0),
                new LatencyPercentileSnapshot(10, 0.95, 600),
                new ErrorRateSnapshot(10, 0, 0.0, true)
        );

        assertEquals(ScalingAction.SCALE_OUT, decision.action());
        assertEquals(3, decision.targetInstances());
    }

    /**
     * Confirms low throughput and low latency together allow scale-in.
     */
    @Test
    void scalesInWhenDemandIsLow() {
        AutoscalingDecision decision = policy().evaluate(
                3,
                new ThroughputSnapshot(5, 10_000, 0.5),
                new LatencyPercentileSnapshot(10, 0.95, 80),
                new ErrorRateSnapshot(10, 0, 0.0, true)
        );

        assertEquals(ScalingAction.SCALE_IN, decision.action());
        assertEquals(2, decision.targetInstances());
    }

    /**
     * Confirms bounded policies hold when already at min or max.
     */
    @Test
    void holdsAtBounds() {
        AutoscalingPolicy policy = policy();

        AutoscalingDecision atMax = policy.evaluate(
                5,
                new ThroughputSnapshot(1_000, 10_000, 100.0),
                new LatencyPercentileSnapshot(10, 0.95, 100),
                new ErrorRateSnapshot(10, 0, 0.0, true)
        );
        AutoscalingDecision atMin = policy.evaluate(
                1,
                new ThroughputSnapshot(5, 10_000, 0.5),
                new LatencyPercentileSnapshot(10, 0.95, 80),
                new ErrorRateSnapshot(10, 0, 0.0, true)
        );

        assertEquals(ScalingAction.HOLD, atMax.action());
        assertEquals(5, atMax.targetInstances());
        assertEquals(ScalingAction.HOLD, atMin.action());
        assertEquals(1, atMin.targetInstances());
    }

    /**
     * Confirms stable metrics keep the current capacity.
     */
    @Test
    void holdsWhenMetricsAreStable() {
        AutoscalingDecision decision = policy().evaluate(
                2,
                new ThroughputSnapshot(100, 10_000, 10.0),
                new LatencyPercentileSnapshot(10, 0.95, 200),
                new ErrorRateSnapshot(10, 0, 0.0, true)
        );

        assertEquals(ScalingAction.HOLD, decision.action());
        assertEquals(2, decision.targetInstances());
    }

    /**
     * Documents validation for policy construction and evaluation inputs.
     */
    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> new AutoscalingPolicy(0, 5, 50, 5, 500, 100));
        assertThrows(IllegalArgumentException.class, () -> new AutoscalingPolicy(2, 1, 50, 5, 500, 100));
        assertThrows(IllegalArgumentException.class, () -> new AutoscalingPolicy(1, 5, 5, 50, 500, 100));
        assertThrows(IllegalArgumentException.class, () -> new AutoscalingPolicy(1, 5, 50, 5, 100, 500));

        AutoscalingPolicy policy = policy();
        assertThrows(IllegalArgumentException.class,
                () -> policy.evaluate(0, throughput(), latency(), healthyErrorRate()));
        assertThrows(IllegalArgumentException.class,
                () -> policy.evaluate(2, null, latency(), healthyErrorRate()));
        assertThrows(IllegalArgumentException.class,
                () -> policy.evaluate(2, throughput(), null, healthyErrorRate()));
        assertThrows(IllegalArgumentException.class,
                () -> policy.evaluate(2, throughput(), latency(), null));
    }

    /**
     * Creates the shared policy used by behavior tests.
     */
    private static AutoscalingPolicy policy() {
        return new AutoscalingPolicy(1, 5, 50.0, 2.0, 500, 100);
    }

    /**
     * Creates a stable throughput snapshot.
     */
    private static ThroughputSnapshot throughput() {
        return new ThroughputSnapshot(100, 10_000, 10.0);
    }

    /**
     * Creates a stable latency snapshot.
     */
    private static LatencyPercentileSnapshot latency() {
        return new LatencyPercentileSnapshot(10, 0.95, 200);
    }

    /**
     * Creates a healthy error-rate snapshot.
     */
    private static ErrorRateSnapshot healthyErrorRate() {
        return new ErrorRateSnapshot(10, 0, 0.0, true);
    }
}
