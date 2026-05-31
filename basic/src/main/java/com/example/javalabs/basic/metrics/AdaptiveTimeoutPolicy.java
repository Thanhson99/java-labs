package com.example.javalabs.basic.metrics;

/**
 * Calculates a timeout from latency percentile estimates.
 *
 * <p>Static timeouts are easy to configure but often become stale. This policy reads a percentile
 * from {@link LatencyHistogram}, applies a margin, and clamps the result to a configured range so
 * timeout values adapt without becoming unsafe.</p>
 */
public final class AdaptiveTimeoutPolicy {

    private final LatencyHistogram histogram;
    private final double percentile;
    private final double marginRatio;
    private final long minTimeoutMillis;
    private final long maxTimeoutMillis;

    /**
     * Creates an adaptive timeout policy.
     *
     * @param histogram latency histogram used as the data source
     * @param percentile percentile to read from the histogram, for example {@code 0.95}
     * @param marginRatio extra ratio added to the percentile latency, for example {@code 0.25}
     * @param minTimeoutMillis lower timeout bound used when data is empty or very low
     * @param maxTimeoutMillis upper timeout bound used to avoid waiting too long
     * @throws IllegalArgumentException when dependencies or numeric limits are invalid
     */
    public AdaptiveTimeoutPolicy(
            LatencyHistogram histogram,
            double percentile,
            double marginRatio,
            long minTimeoutMillis,
            long maxTimeoutMillis) {
        if (histogram == null) {
            throw new IllegalArgumentException("histogram must not be null");
        }
        if (percentile < 0.0 || percentile > 1.0) {
            throw new IllegalArgumentException("percentile must be between 0.0 and 1.0");
        }
        if (marginRatio < 0.0) {
            throw new IllegalArgumentException("marginRatio must not be negative");
        }
        if (minTimeoutMillis <= 0) {
            throw new IllegalArgumentException("minTimeoutMillis must be positive");
        }
        if (maxTimeoutMillis < minTimeoutMillis) {
            throw new IllegalArgumentException("maxTimeoutMillis must be greater than or equal to minTimeoutMillis");
        }
        this.histogram = histogram;
        this.percentile = percentile;
        this.marginRatio = marginRatio;
        this.minTimeoutMillis = minTimeoutMillis;
        this.maxTimeoutMillis = maxTimeoutMillis;
    }

    /**
     * Records one latency sample in the backing histogram.
     *
     * @param latencyMillis latency value in milliseconds
     * @throws IllegalArgumentException when {@code latencyMillis} is negative
     */
    public void recordLatency(long latencyMillis) {
        histogram.record(latencyMillis);
    }

    /**
     * Calculates the current adaptive timeout.
     *
     * @return timeout decision with baseline percentile context
     */
    public AdaptiveTimeoutDecision decide() {
        LatencyPercentileSnapshot snapshot = histogram.percentile(percentile);
        long baseline = snapshot.estimatedLatencyMillis();
        long unclampedTimeout = snapshot.sampleCount() == 0
                ? minTimeoutMillis
                : Math.round(baseline * (1.0 + marginRatio));
        long timeout = clamp(unclampedTimeout, minTimeoutMillis, maxTimeoutMillis);
        return new AdaptiveTimeoutDecision(timeout, baseline, percentile, snapshot.sampleCount());
    }

    /**
     * Clears the backing histogram data while keeping policy configuration.
     */
    public void clear() {
        histogram.clear();
    }

    /**
     * Restricts a value to an inclusive range.
     */
    private static long clamp(long value, long minValue, long maxValue) {
        return Math.max(minValue, Math.min(maxValue, value));
    }
}
