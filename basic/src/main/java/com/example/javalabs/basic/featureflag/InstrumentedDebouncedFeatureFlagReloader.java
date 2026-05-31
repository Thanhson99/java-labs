package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Adds metrics around a debounced feature flag reloader without changing reload behavior.
 */
public final class InstrumentedDebouncedFeatureFlagReloader {

    private final DebouncedFeatureFlagReloader delegate;
    private final FeatureFlagReloadMetrics metrics;

    /**
     * Creates an instrumented wrapper around a debounced reloader.
     *
     * @param delegate reload workflow to call
     * @param metrics mutable metrics collector
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public InstrumentedDebouncedFeatureFlagReloader(
            DebouncedFeatureFlagReloader delegate,
            FeatureFlagReloadMetrics metrics) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("metrics must not be null");
        }
        this.delegate = delegate;
        this.metrics = metrics;
    }

    /**
     * Submits a desired config and records one submission metric.
     *
     * @param newRules desired complete rule set
     * @return timestamp when the debounced reload becomes due
     */
    public long submit(List<FeatureFlagRule> newRules) {
        long dueAtMillis = delegate.submit(newRules);
        metrics.recordSubmission();
        return dueAtMillis;
    }

    /**
     * Flushes the delegate when due and records the resulting outcome.
     *
     * @return debounced reload result
     */
    public DebouncedFeatureFlagReloadResult flushIfDue() {
        DebouncedFeatureFlagReloadResult result = delegate.flushIfDue();
        metrics.recordFlushResult(result);
        return result;
    }

    /**
     * @return immutable metrics snapshot
     */
    public FeatureFlagReloadMetricsSnapshot metricsSnapshot() {
        return metrics.snapshot();
    }
}
