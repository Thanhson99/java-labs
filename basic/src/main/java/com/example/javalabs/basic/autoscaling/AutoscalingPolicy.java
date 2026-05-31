package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.metrics.ErrorRateSnapshot;
import com.example.javalabs.basic.metrics.LatencyPercentileSnapshot;
import com.example.javalabs.basic.metrics.ThroughputSnapshot;

/**
 * Converts throughput, latency, and error-rate metrics into a scaling recommendation.
 *
 * <p>The policy intentionally uses simple threshold rules so the decision is easy to trace in
 * tests. Scale-out has priority over scale-in because protecting user-facing latency is usually
 * more important than removing capacity immediately.</p>
 */
public final class AutoscalingPolicy {

    private final int minInstances;
    private final int maxInstances;
    private final double scaleOutEventsPerSecond;
    private final double scaleInEventsPerSecond;
    private final long scaleOutLatencyMillis;
    private final long scaleInLatencyMillis;

    /**
     * Creates an autoscaling policy.
     *
     * @param minInstances minimum allowed instance count
     * @param maxInstances maximum allowed instance count
     * @param scaleOutEventsPerSecond throughput threshold that triggers scale-out
     * @param scaleInEventsPerSecond throughput threshold below which scale-in may occur
     * @param scaleOutLatencyMillis latency threshold that triggers scale-out
     * @param scaleInLatencyMillis latency threshold below which scale-in may occur
     * @throws IllegalArgumentException when thresholds or bounds are invalid
     */
    public AutoscalingPolicy(
            int minInstances,
            int maxInstances,
            double scaleOutEventsPerSecond,
            double scaleInEventsPerSecond,
            long scaleOutLatencyMillis,
            long scaleInLatencyMillis) {
        if (minInstances <= 0) {
            throw new IllegalArgumentException("minInstances must be positive");
        }
        if (maxInstances < minInstances) {
            throw new IllegalArgumentException("maxInstances must be greater than or equal to minInstances");
        }
        if (scaleOutEventsPerSecond < 0.0 || scaleInEventsPerSecond < 0.0) {
            throw new IllegalArgumentException("throughput thresholds must not be negative");
        }
        if (scaleInEventsPerSecond > scaleOutEventsPerSecond) {
            throw new IllegalArgumentException("scaleInEventsPerSecond must not exceed scaleOutEventsPerSecond");
        }
        if (scaleOutLatencyMillis <= 0 || scaleInLatencyMillis <= 0) {
            throw new IllegalArgumentException("latency thresholds must be positive");
        }
        if (scaleInLatencyMillis > scaleOutLatencyMillis) {
            throw new IllegalArgumentException("scaleInLatencyMillis must not exceed scaleOutLatencyMillis");
        }
        this.minInstances = minInstances;
        this.maxInstances = maxInstances;
        this.scaleOutEventsPerSecond = scaleOutEventsPerSecond;
        this.scaleInEventsPerSecond = scaleInEventsPerSecond;
        this.scaleOutLatencyMillis = scaleOutLatencyMillis;
        this.scaleInLatencyMillis = scaleInLatencyMillis;
    }

    /**
     * Evaluates metrics and returns a scaling decision.
     *
     * @param currentInstances current running instance count
     * @param throughput recent throughput snapshot
     * @param latency recent latency percentile snapshot
     * @param errorRate recent error-rate snapshot
     * @return scaling decision with target instance count
     * @throws IllegalArgumentException when inputs are invalid
     */
    public AutoscalingDecision evaluate(
            int currentInstances,
            ThroughputSnapshot throughput,
            LatencyPercentileSnapshot latency,
            ErrorRateSnapshot errorRate) {
        if (currentInstances < minInstances || currentInstances > maxInstances) {
            throw new IllegalArgumentException("currentInstances must be within policy bounds");
        }
        if (throughput == null) {
            throw new IllegalArgumentException("throughput must not be null");
        }
        if (latency == null) {
            throw new IllegalArgumentException("latency must not be null");
        }
        if (errorRate == null) {
            throw new IllegalArgumentException("errorRate must not be null");
        }

        if (!errorRate.healthy()) {
            return scaleOut(currentInstances, "error rate unhealthy");
        }
        if (throughput.eventsPerSecond() >= scaleOutEventsPerSecond) {
            return scaleOut(currentInstances, "throughput above scale-out threshold");
        }
        if (latency.estimatedLatencyMillis() >= scaleOutLatencyMillis) {
            return scaleOut(currentInstances, "latency above scale-out threshold");
        }
        if (throughput.eventsPerSecond() <= scaleInEventsPerSecond
                && latency.estimatedLatencyMillis() <= scaleInLatencyMillis) {
            return scaleIn(currentInstances, "throughput and latency below scale-in thresholds");
        }
        return new AutoscalingDecision(ScalingAction.HOLD, currentInstances, "metrics inside stable band");
    }

    /**
     * Builds a scale-out decision while respecting the maximum bound.
     */
    private AutoscalingDecision scaleOut(int currentInstances, String reason) {
        if (currentInstances == maxInstances) {
            return new AutoscalingDecision(ScalingAction.HOLD, currentInstances, reason + "; already at max");
        }
        return new AutoscalingDecision(ScalingAction.SCALE_OUT, currentInstances + 1, reason);
    }

    /**
     * Builds a scale-in decision while respecting the minimum bound.
     */
    private AutoscalingDecision scaleIn(int currentInstances, String reason) {
        if (currentInstances == minInstances) {
            return new AutoscalingDecision(ScalingAction.HOLD, currentInstances, reason + "; already at min");
        }
        return new AutoscalingDecision(ScalingAction.SCALE_IN, currentInstances - 1, reason);
    }
}
