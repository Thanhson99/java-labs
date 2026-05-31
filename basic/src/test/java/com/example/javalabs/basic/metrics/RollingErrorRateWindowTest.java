package com.example.javalabs.basic.metrics;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.ServiceCallOutcome;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies fixed-size rolling error-rate tracking.
 */
class RollingErrorRateWindowTest {

    /**
     * Confirms snapshots use retained success and failure samples.
     */
    @Test
    void tracksErrorRateFromRecordedOutcomes() {
        RollingErrorRateWindow window = new RollingErrorRateWindow(5, 0.50);

        window.record(ServiceCallOutcome.SUCCESS);
        window.record(ServiceCallOutcome.FAILURE);
        ErrorRateSnapshot snapshot = window.record(ServiceCallOutcome.SUCCESS);

        assertEquals(3, snapshot.sampleCount());
        assertEquals(1, snapshot.failureCount());
        assertEquals(1.0 / 3.0, snapshot.errorRate());
        assertTrue(snapshot.healthy());
    }

    /**
     * Confirms the oldest outcome is removed when capacity is exceeded.
     */
    @Test
    void evictsOldestOutcomeWhenFull() {
        RollingErrorRateWindow window = new RollingErrorRateWindow(3, 0.50);

        window.record(ServiceCallOutcome.FAILURE);
        window.record(ServiceCallOutcome.SUCCESS);
        window.record(ServiceCallOutcome.SUCCESS);
        ErrorRateSnapshot snapshot = window.record(ServiceCallOutcome.SUCCESS);

        assertEquals(3, snapshot.sampleCount());
        assertEquals(0, snapshot.failureCount());
        assertEquals(0.0, snapshot.errorRate());
        assertTrue(snapshot.healthy());
    }

    /**
     * Confirms health changes when the retained error rate crosses the threshold.
     */
    @Test
    void marksWindowUnhealthyWhenThresholdIsExceeded() {
        RollingErrorRateWindow window = new RollingErrorRateWindow(4, 0.25);

        window.record(ServiceCallOutcome.SUCCESS);
        window.record(ServiceCallOutcome.FAILURE);
        ErrorRateSnapshot snapshot = window.record(ServiceCallOutcome.FAILURE);

        assertEquals(2, snapshot.failureCount());
        assertEquals(2.0 / 3.0, snapshot.errorRate());
        assertFalse(snapshot.healthy());
    }

    /**
     * Confirms clear resets the retained rolling state.
     */
    @Test
    void clearResetsWindow() {
        RollingErrorRateWindow window = new RollingErrorRateWindow(2, 0.50);
        window.record(ServiceCallOutcome.FAILURE);
        window.record(ServiceCallOutcome.FAILURE);

        window.clear();

        ErrorRateSnapshot snapshot = window.snapshot();
        assertEquals(0, snapshot.sampleCount());
        assertEquals(0, snapshot.failureCount());
        assertEquals(0.0, snapshot.errorRate());
        assertTrue(snapshot.healthy());
    }

    /**
     * Documents validation for constructor and record boundaries.
     */
    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> new RollingErrorRateWindow(0, 0.50));
        assertThrows(IllegalArgumentException.class, () -> new RollingErrorRateWindow(5, -0.01));
        assertThrows(IllegalArgumentException.class, () -> new RollingErrorRateWindow(5, 1.01));

        RollingErrorRateWindow window = new RollingErrorRateWindow(2, 0.50);
        assertThrows(IllegalArgumentException.class, () -> window.record(null));
    }
}
