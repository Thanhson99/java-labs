package com.example.javalabs.basic.featureflag;

/**
 * Mutable counters for feature flag reload workflow observations.
 */
public final class FeatureFlagReloadMetrics {

    private int submissions;
    private int idleFlushes;
    private int waitingFlushes;
    private int flushedAttempts;
    private int rateLimitedBlocks;
    private int fingerprintSkips;
    private int safeReloadApplied;
    private int safeReloadRejected;

    /**
     * Records one submitted reload request.
     */
    public void recordSubmission() {
        submissions++;
    }

    /**
     * Records the outcome of one debounce flush attempt.
     *
     * @param result debounced reload result
     * @throws IllegalArgumentException when {@code result} is {@code null}
     */
    public void recordFlushResult(DebouncedFeatureFlagReloadResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        if (result.idle()) {
            // Flush ran with no pending work.
            idleFlushes++;
            return;
        }
        if (result.waiting()) {
            // Work exists, but the debounce delay has not elapsed yet.
            waitingFlushes++;
            return;
        }

        flushedAttempts++;
        RateLimitedFeatureFlagReloadResult rateLimitedResult = result.reloadResult().orElseThrow();
        if (rateLimitedResult.blocked()) {
            // Rate limiter protected the registry from too many reload attempts.
            rateLimitedBlocks++;
            return;
        }

        FingerprintingFeatureFlagReloadResult fingerprintResult =
                rateLimitedResult.fingerprintResult().orElseThrow();
        if (fingerprintResult.skipped()) {
            // Fingerprint matched previous config, so mutation was unnecessary.
            fingerprintSkips++;
            return;
        }

        SafeFeatureFlagReloadResult safeReloadResult = fingerprintResult.reloadResult().orElseThrow();
        if (safeReloadResult.applied()) {
            safeReloadApplied++;
        } else {
            safeReloadRejected++;
        }
    }

    /**
     * @return immutable snapshot of current counters
     */
    public FeatureFlagReloadMetricsSnapshot snapshot() {
        return new FeatureFlagReloadMetricsSnapshot(
                submissions,
                idleFlushes,
                waitingFlushes,
                flushedAttempts,
                rateLimitedBlocks,
                fingerprintSkips,
                safeReloadApplied,
                safeReloadRejected
        );
    }
}
