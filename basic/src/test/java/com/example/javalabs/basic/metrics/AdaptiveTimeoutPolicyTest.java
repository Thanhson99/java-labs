package com.example.javalabs.basic.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies adaptive timeout decisions from latency histogram data.
 */
class AdaptiveTimeoutPolicyTest {

    /**
     * Confirms an empty histogram uses the minimum timeout as a safe fallback.
     */
    @Test
    void usesMinimumTimeoutWhenNoLatencySamplesExist() {
        AdaptiveTimeoutPolicy policy = new AdaptiveTimeoutPolicy(new LatencyHistogram(100, 5), 0.95, 0.25, 50, 500);

        AdaptiveTimeoutDecision decision = policy.decide();

        assertEquals(50, decision.timeoutMillis());
        assertEquals(0, decision.baselineLatencyMillis());
        assertEquals(0, decision.sampleCount());
    }

    /**
     * Confirms timeout is calculated from percentile latency plus margin.
     */
    @Test
    void calculatesTimeoutFromPercentileAndMargin() {
        AdaptiveTimeoutPolicy policy = new AdaptiveTimeoutPolicy(new LatencyHistogram(100, 5), 0.50, 0.25, 50, 500);

        policy.recordLatency(20);
        policy.recordLatency(140);
        policy.recordLatency(180);

        AdaptiveTimeoutDecision decision = policy.decide();

        assertEquals(199, decision.baselineLatencyMillis());
        assertEquals(249, decision.timeoutMillis());
        assertEquals(3, decision.sampleCount());
    }

    /**
     * Confirms timeout decisions are clamped to the configured upper bound.
     */
    @Test
    void clampsTimeoutToMaximum() {
        AdaptiveTimeoutPolicy policy = new AdaptiveTimeoutPolicy(new LatencyHistogram(100, 10), 0.99, 1.0, 50, 300);

        policy.recordLatency(900);

        AdaptiveTimeoutDecision decision = policy.decide();

        assertEquals(999, decision.baselineLatencyMillis());
        assertEquals(300, decision.timeoutMillis());
    }

    /**
     * Confirms clearing latency data returns the policy to minimum-timeout fallback.
     */
    @Test
    void clearResetsLatencyData() {
        AdaptiveTimeoutPolicy policy = new AdaptiveTimeoutPolicy(new LatencyHistogram(100, 5), 0.95, 0.25, 50, 500);
        policy.recordLatency(250);

        policy.clear();

        assertEquals(50, policy.decide().timeoutMillis());
    }

    /**
     * Documents validation for constructor and sample recording boundaries.
     */
    @Test
    void rejectsInvalidInput() {
        LatencyHistogram histogram = new LatencyHistogram(100, 5);
        assertThrows(IllegalArgumentException.class,
                () -> new AdaptiveTimeoutPolicy(null, 0.95, 0.25, 50, 500));
        assertThrows(IllegalArgumentException.class,
                () -> new AdaptiveTimeoutPolicy(histogram, -0.01, 0.25, 50, 500));
        assertThrows(IllegalArgumentException.class,
                () -> new AdaptiveTimeoutPolicy(histogram, 0.95, -0.01, 50, 500));
        assertThrows(IllegalArgumentException.class,
                () -> new AdaptiveTimeoutPolicy(histogram, 0.95, 0.25, 0, 500));
        assertThrows(IllegalArgumentException.class,
                () -> new AdaptiveTimeoutPolicy(histogram, 0.95, 0.25, 500, 100));

        AdaptiveTimeoutPolicy policy = new AdaptiveTimeoutPolicy(histogram, 0.95, 0.25, 50, 500);
        assertThrows(IllegalArgumentException.class, () -> policy.recordLatency(-1));
    }
}
