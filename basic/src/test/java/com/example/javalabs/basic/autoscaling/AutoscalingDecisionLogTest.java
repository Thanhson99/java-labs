package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.ManualTimeSource;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies bounded autoscaling decision audit history.
 */
class AutoscalingDecisionLogTest {

    /**
     * Confirms recorded decisions receive timestamps from the configured clock.
     */
    @Test
    void recordsTimestampedDecisionEvents() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        AutoscalingDecisionLog log = new AutoscalingDecisionLog(3, timeSource);

        AutoscalingDecisionEvent event = log.record(scaleOut(3));

        assertEquals(1_000, event.timestampMillis());
        assertEquals(ScalingAction.SCALE_OUT, event.decision().action());
        assertEquals(List.of(event), log.findAll());
    }

    /**
     * Confirms the log drops the oldest event when capacity is exceeded.
     */
    @Test
    void evictsOldestDecisionWhenCapacityIsExceeded() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        AutoscalingDecisionLog log = new AutoscalingDecisionLog(2, timeSource);

        log.record(scaleOut(2));
        timeSource.advanceMillis(1);
        AutoscalingDecisionEvent second = log.record(hold(2));
        timeSource.advanceMillis(1);
        AutoscalingDecisionEvent third = log.record(scaleIn(1));

        assertEquals(List.of(second, third), log.findAll());
        assertEquals(1, log.droppedEvents());
    }

    /**
     * Confirms summary counters are derived from retained events only.
     */
    @Test
    void summarizesRetainedDecisionActions() {
        AutoscalingDecisionLog log = new AutoscalingDecisionLog(3, new ManualTimeSource(0));

        log.record(scaleOut(3));
        log.record(scaleIn(2));
        log.record(hold(2));

        AutoscalingDecisionLogSummary summary = log.summarize();
        assertEquals(3, summary.retainedEvents());
        assertEquals(1, summary.scaleOutCount());
        assertEquals(1, summary.scaleInCount());
        assertEquals(1, summary.holdCount());
        assertEquals(0, summary.droppedEvents());
    }

    /**
     * Documents validation for constructor and record boundaries.
     */
    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> new AutoscalingDecisionLog(0, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class, () -> new AutoscalingDecisionLog(2, null));

        AutoscalingDecisionLog log = new AutoscalingDecisionLog(2, new ManualTimeSource(0));
        assertThrows(IllegalArgumentException.class, () -> log.record(null));
    }

    /**
     * Creates a scale-out decision for tests.
     */
    private static AutoscalingDecision scaleOut(int targetInstances) {
        return new AutoscalingDecision(ScalingAction.SCALE_OUT, targetInstances, "high load");
    }

    /**
     * Creates a scale-in decision for tests.
     */
    private static AutoscalingDecision scaleIn(int targetInstances) {
        return new AutoscalingDecision(ScalingAction.SCALE_IN, targetInstances, "low load");
    }

    /**
     * Creates a hold decision for tests.
     */
    private static AutoscalingDecision hold(int targetInstances) {
        return new AutoscalingDecision(ScalingAction.HOLD, targetInstances, "stable");
    }
}
