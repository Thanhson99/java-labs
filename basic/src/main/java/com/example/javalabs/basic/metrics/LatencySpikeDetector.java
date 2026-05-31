package com.example.javalabs.basic.metrics;

/**
 * Detects latency spikes by comparing each new sample with a rolling baseline.
 *
 * <p>The detector reads the baseline before recording the new sample. That prevents a very slow
 * request from raising the average first and hiding its own spike. The underlying
 * {@link RollingLatencyWindow} keeps memory bounded while still exposing cheap summary reads.</p>
 */
public final class LatencySpikeDetector {

    private final RollingLatencyWindow window;
    private final int minimumBaselineSamples;
    private final double spikeMultiplier;

    /**
     * Creates a detector with a fresh rolling latency window.
     *
     * @param windowCapacity maximum number of recent samples retained
     * @param minimumBaselineSamples minimum samples required before spike detection starts
     * @param spikeMultiplier multiplier applied to baseline average to calculate threshold
     * @throws IllegalArgumentException when any argument is outside its valid range
     */
    public LatencySpikeDetector(int windowCapacity, int minimumBaselineSamples, double spikeMultiplier) {
        this(new RollingLatencyWindow(windowCapacity), minimumBaselineSamples, spikeMultiplier);
    }

    /**
     * Creates a detector with an injected rolling window.
     *
     * @param window rolling latency window used as the baseline store
     * @param minimumBaselineSamples minimum samples required before spike detection starts
     * @param spikeMultiplier multiplier applied to baseline average to calculate threshold
     * @throws IllegalArgumentException when dependencies or numeric limits are invalid
     */
    public LatencySpikeDetector(
            RollingLatencyWindow window,
            int minimumBaselineSamples,
            double spikeMultiplier) {
        if (window == null) {
            throw new IllegalArgumentException("window must not be null");
        }
        if (minimumBaselineSamples <= 0) {
            throw new IllegalArgumentException("minimumBaselineSamples must be positive");
        }
        if (minimumBaselineSamples > window.capacity()) {
            throw new IllegalArgumentException("minimumBaselineSamples must not exceed window capacity");
        }
        if (spikeMultiplier <= 1.0) {
            throw new IllegalArgumentException("spikeMultiplier must be greater than 1.0");
        }
        this.window = window;
        this.minimumBaselineSamples = minimumBaselineSamples;
        this.spikeMultiplier = spikeMultiplier;
    }

    /**
     * Evaluates one latency sample, records it, and returns the spike decision.
     *
     * @param latencyMillis latency value in milliseconds
     * @return spike decision containing baseline and post-recording snapshot
     * @throws IllegalArgumentException when {@code latencyMillis} is negative
     */
    public LatencySpikeDecision recordAndEvaluate(long latencyMillis) {
        if (latencyMillis < 0) {
            throw new IllegalArgumentException("latencyMillis must not be negative");
        }

        LatencyWindowSnapshot before = window.snapshot();
        boolean enoughBaseline = before.sampleCount() >= minimumBaselineSamples;
        double threshold = enoughBaseline ? before.averageMillis() * spikeMultiplier : 0.0;
        boolean spike = enoughBaseline && latencyMillis > threshold;

        // Record after evaluating so the current sample cannot dilute its own baseline.
        window.record(latencyMillis);
        return new LatencySpikeDecision(
                spike,
                latencyMillis,
                before.averageMillis(),
                threshold,
                before.sampleCount(),
                window.snapshot()
        );
    }

    /**
     * Returns the current rolling latency snapshot.
     *
     * @return immutable snapshot of the detector's retained samples
     */
    public LatencyWindowSnapshot snapshot() {
        return window.snapshot();
    }

    /**
     * Clears the detector baseline.
     */
    public void clear() {
        window.clear();
    }
}
