package com.example.javalabs.basic.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies approximate latency percentile reads from fixed histogram buckets.
 */
class LatencyHistogramTest {

    /**
     * Confirms samples are counted in their configured bucket ranges.
     */
    @Test
    void recordsSamplesIntoBuckets() {
        LatencyHistogram histogram = new LatencyHistogram(100, 5);

        histogram.record(0);
        histogram.record(99);
        histogram.record(100);
        histogram.record(450);
        histogram.record(999);

        assertEquals(5, histogram.sampleCount());
        assertArrayEquals(new long[] {2, 1, 0, 0, 2}, histogram.bucketCounts());
        assertEquals(499, histogram.maxTrackedLatencyMillis());
    }

    /**
     * Confirms percentile reads use cumulative bucket counts.
     */
    @Test
    void estimatesPercentileFromCumulativeCounts() {
        LatencyHistogram histogram = new LatencyHistogram(50, 4);

        histogram.record(10);
        histogram.record(20);
        histogram.record(70);
        histogram.record(180);

        LatencyPercentileSnapshot p50 = histogram.percentile(0.50);
        LatencyPercentileSnapshot p95 = histogram.percentile(0.95);

        assertEquals(4, p50.sampleCount());
        assertEquals(49, p50.estimatedLatencyMillis());
        assertEquals(199, p95.estimatedLatencyMillis());
    }

    /**
     * Confirms empty histograms return a zero-latency percentile snapshot.
     */
    @Test
    void emptyHistogramReturnsZeroEstimate() {
        LatencyHistogram histogram = new LatencyHistogram(100, 3);

        LatencyPercentileSnapshot snapshot = histogram.percentile(0.99);

        assertEquals(0, snapshot.sampleCount());
        assertEquals(0, snapshot.estimatedLatencyMillis());
    }

    /**
     * Confirms clearing removes counts without changing histogram configuration.
     */
    @Test
    void clearRemovesRecordedCounts() {
        LatencyHistogram histogram = new LatencyHistogram(100, 2);
        histogram.record(10);
        histogram.record(250);

        histogram.clear();

        assertEquals(0, histogram.sampleCount());
        assertArrayEquals(new long[] {0, 0}, histogram.bucketCounts());
        assertEquals(199, histogram.maxTrackedLatencyMillis());
    }

    /**
     * Documents validation at construction, record, and percentile boundaries.
     */
    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> new LatencyHistogram(0, 2));
        assertThrows(IllegalArgumentException.class, () -> new LatencyHistogram(100, 0));

        LatencyHistogram histogram = new LatencyHistogram(100, 2);
        assertThrows(IllegalArgumentException.class, () -> histogram.record(-1));
        assertThrows(IllegalArgumentException.class, () -> histogram.percentile(-0.01));
        assertThrows(IllegalArgumentException.class, () -> histogram.percentile(1.01));
    }
}
