package com.example.javalabs.basic.featureflag;

/**
 * Counters for the debounced feature flag reload workflow.
 *
 * @param submissions reload submissions received
 * @param idleFlushes flush calls that found no pending reload
 * @param waitingFlushes flush calls that kept waiting because the debounce window had not elapsed
 * @param flushedAttempts reload attempts that passed the debounce gate
 * @param rateLimitedBlocks attempts blocked by the rate limiter
 * @param fingerprintSkips attempts skipped because the configuration fingerprint did not change
 * @param safeReloadApplied validated reloads applied to the live registry
 * @param safeReloadRejected validated reloads rejected before changing the live registry
 */
public record FeatureFlagReloadMetricsSnapshot(
        int submissions,
        int idleFlushes,
        int waitingFlushes,
        int flushedAttempts,
        int rateLimitedBlocks,
        int fingerprintSkips,
        int safeReloadApplied,
        int safeReloadRejected) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when any counter is negative
     */
    public FeatureFlagReloadMetricsSnapshot {
        if (submissions < 0
                || idleFlushes < 0
                || waitingFlushes < 0
                || flushedAttempts < 0
                || rateLimitedBlocks < 0
                || fingerprintSkips < 0
                || safeReloadApplied < 0
                || safeReloadRejected < 0) {
            throw new IllegalArgumentException("metrics must not be negative");
        }
    }

    /**
     * Counts completed paths that did not mutate live feature flag config.
     *
     * @return total no-mutation outcomes
     */
    public int completedWithoutMutation() {
        return idleFlushes + waitingFlushes + rateLimitedBlocks + fingerprintSkips + safeReloadRejected;
    }
}
