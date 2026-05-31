package com.example.javalabs.basic.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies rolling latency statistics over a bounded sample window.
 */
class RollingLatencyWindowTest {

    /**
     * Confirms average, min, and max are calculated from retained samples.
     */
    @Test
    void recordsLatencyStatistics() {
        RollingLatencyWindow window = new RollingLatencyWindow(5);

        window.record(20);
        window.record(40);
        window.record(10);

        LatencyWindowSnapshot snapshot = window.snapshot();
        assertEquals(3, snapshot.sampleCount());
        assertEquals(70.0 / 3, snapshot.averageMillis());
        assertEquals(10, snapshot.minMillis());
        assertEquals(40, snapshot.maxMillis());
    }

    /**
     * Confirms the oldest sample is removed when capacity is exceeded.
     */
    @Test
    void evictsOldestSampleWhenWindowIsFull() {
        RollingLatencyWindow window = new RollingLatencyWindow(3);

        window.record(100);
        window.record(20);
        window.record(30);
        window.record(40);

        LatencyWindowSnapshot snapshot = window.snapshot();
        assertEquals(3, snapshot.sampleCount());
        assertEquals(30.0, snapshot.averageMillis());
        assertEquals(20, snapshot.minMillis());
        assertEquals(40, snapshot.maxMillis());
    }

    /**
     * Confirms monotonic queues discard expired min and max candidates correctly.
     */
    @Test
    void updatesMinAndMaxAfterEviction() {
        RollingLatencyWindow window = new RollingLatencyWindow(2);

        window.record(5);
        window.record(100);
        window.record(20);

        LatencyWindowSnapshot snapshot = window.snapshot();
        assertEquals(2, snapshot.sampleCount());
        assertEquals(60.0, snapshot.averageMillis());
        assertEquals(20, snapshot.minMillis());
        assertEquals(100, snapshot.maxMillis());
    }

    /**
     * Confirms clearing the window removes all retained statistics.
     */
    @Test
    void clearResetsWindow() {
        RollingLatencyWindow window = new RollingLatencyWindow(2);
        window.record(10);
        window.record(30);

        window.clear();

        assertEquals(0, window.size());
        assertEquals(LatencyWindowSnapshot.empty(), window.snapshot());
    }

    /**
     * Documents validation for capacity and latency samples.
     */
    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> new RollingLatencyWindow(0));

        RollingLatencyWindow window = new RollingLatencyWindow(2);
        assertThrows(IllegalArgumentException.class, () -> window.record(-1));
    }
}
