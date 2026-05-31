package com.example.javalabs.basic.metrics;

import java.util.Arrays;

/**
 * Estimates latency percentiles with fixed memory and predictable read cost.
 *
 * <p>The histogram trades exact values for speed. Each sample increments one bucket, and percentile
 * reads scan cumulative bucket counts instead of sorting every recorded sample. Values above the
 * configured maximum are clamped into the last bucket so the structure never grows.</p>
 */
public final class LatencyHistogram {

    private final long bucketSizeMillis;
    private final long[] bucketCounts;
    private long sampleCount;

    /**
     * Creates a latency histogram.
     *
     * @param bucketSizeMillis width of each latency bucket
     * @param bucketCount number of buckets retained
     * @throws IllegalArgumentException when either argument is not positive
     */
    public LatencyHistogram(long bucketSizeMillis, int bucketCount) {
        if (bucketSizeMillis <= 0) {
            throw new IllegalArgumentException("bucketSizeMillis must be positive");
        }
        if (bucketCount <= 0) {
            throw new IllegalArgumentException("bucketCount must be positive");
        }
        this.bucketSizeMillis = bucketSizeMillis;
        this.bucketCounts = new long[bucketCount];
    }

    /**
     * Records one latency sample.
     *
     * @param latencyMillis latency value in milliseconds
     * @throws IllegalArgumentException when {@code latencyMillis} is negative
     */
    public void record(long latencyMillis) {
        if (latencyMillis < 0) {
            throw new IllegalArgumentException("latencyMillis must not be negative");
        }
        bucketCounts[bucketIndex(latencyMillis)]++;
        sampleCount++;
    }

    /**
     * Estimates the requested percentile from cumulative bucket counts.
     *
     * @param percentile percentile between {@code 0.0} and {@code 1.0}; for example {@code 0.95}
     * @return percentile estimate using the upper bound of the matching bucket
     * @throws IllegalArgumentException when {@code percentile} is outside 0..1
     */
    public LatencyPercentileSnapshot percentile(double percentile) {
        validatePercentile(percentile);
        if (sampleCount == 0) {
            return new LatencyPercentileSnapshot(0, percentile, 0);
        }

        long targetRank = Math.max(1, (long) Math.ceil(sampleCount * percentile));
        long cumulative = 0;
        for (int index = 0; index < bucketCounts.length; index++) {
            cumulative += bucketCounts[index];
            if (cumulative >= targetRank) {
                return new LatencyPercentileSnapshot(sampleCount, percentile, bucketUpperBound(index));
            }
        }

        return new LatencyPercentileSnapshot(sampleCount, percentile, bucketUpperBound(bucketCounts.length - 1));
    }

    /**
     * Removes all recorded samples while keeping the histogram configuration.
     */
    public void clear() {
        Arrays.fill(bucketCounts, 0);
        sampleCount = 0;
    }

    /**
     * Returns how many samples have been recorded.
     *
     * @return total sample count
     */
    public long sampleCount() {
        return sampleCount;
    }

    /**
     * Returns a defensive copy of bucket counts for inspection and tests.
     *
     * @return bucket counts ordered from fastest to slowest bucket
     */
    public long[] bucketCounts() {
        return Arrays.copyOf(bucketCounts, bucketCounts.length);
    }

    /**
     * Returns the largest latency represented by the histogram before clamping.
     *
     * @return maximum bucket upper bound in milliseconds
     */
    public long maxTrackedLatencyMillis() {
        return bucketUpperBound(bucketCounts.length - 1);
    }

    /**
     * Finds the bucket for a latency sample, clamping overflow into the last bucket.
     */
    private int bucketIndex(long latencyMillis) {
        long rawIndex = latencyMillis / bucketSizeMillis;
        if (rawIndex >= bucketCounts.length) {
            return bucketCounts.length - 1;
        }
        return (int) rawIndex;
    }

    /**
     * Returns the inclusive upper bound represented by a bucket.
     */
    private long bucketUpperBound(int index) {
        return ((long) index + 1) * bucketSizeMillis - 1;
    }

    /**
     * Validates percentile input at the public boundary.
     */
    private static void validatePercentile(double percentile) {
        if (percentile < 0.0 || percentile > 1.0) {
            throw new IllegalArgumentException("percentile must be between 0.0 and 1.0");
        }
    }
}
