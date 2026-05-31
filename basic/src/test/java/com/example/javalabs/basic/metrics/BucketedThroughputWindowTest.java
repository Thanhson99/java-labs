package com.example.javalabs.basic.metrics;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.ServiceCallOutcome;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies fixed-memory throughput tracking with time buckets.
 */
class BucketedThroughputWindowTest {

    /**
     * Confirms events in the same bucket accumulate into one counter.
     */
    @Test
    void recordsEventsInCurrentBucket() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        BucketedThroughputWindow window = new BucketedThroughputWindow(1_000, 3, timeSource);

        window.record();
        ThroughputSnapshot snapshot = window.record();

        assertEquals(2, snapshot.totalEvents());
        assertEquals(3_000, snapshot.windowMillis());
        assertEquals(2 * 1_000.0 / 3_000, snapshot.eventsPerSecond());
        assertArrayEquals(new long[] {2, 0, 0}, window.bucketCounts());
    }

    /**
     * Confirms advancing time rotates to the next bucket without losing retained buckets.
     */
    @Test
    void rotatesBucketsAsTimeAdvances() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        BucketedThroughputWindow window = new BucketedThroughputWindow(1_000, 3, timeSource);

        window.record();
        timeSource.advanceMillis(1_000);
        window.record();

        assertEquals(2, window.snapshot().totalEvents());
        assertArrayEquals(new long[] {1, 1, 0}, window.bucketCounts());
    }

    /**
     * Confirms buckets outside the retained window expire.
     */
    @Test
    void expiresBucketsOutsideWindow() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        BucketedThroughputWindow window = new BucketedThroughputWindow(1_000, 3, timeSource);

        window.record();
        timeSource.advanceMillis(3_000);
        ThroughputSnapshot snapshot = window.snapshot();

        assertEquals(0, snapshot.totalEvents());
        assertArrayEquals(new long[] {0, 0, 0}, window.bucketCounts());
    }

    /**
     * Confirms clear removes retained throughput history.
     */
    @Test
    void clearResetsWindow() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        BucketedThroughputWindow window = new BucketedThroughputWindow(1_000, 2, timeSource);
        window.record();

        window.clear();

        assertEquals(0, window.snapshot().totalEvents());
        assertArrayEquals(new long[] {0, 0}, window.bucketCounts());
    }

    /**
     * Documents validation for constructor boundaries.
     */
    @Test
    void rejectsInvalidConfiguration() {
        ManualTimeSource timeSource = new ManualTimeSource(0);

        assertThrows(IllegalArgumentException.class, () -> new BucketedThroughputWindow(0, 2, timeSource));
        assertThrows(IllegalArgumentException.class, () -> new BucketedThroughputWindow(1_000, 0, timeSource));
        assertThrows(IllegalArgumentException.class, () -> new BucketedThroughputWindow(1_000, 2, null));
    }
}
