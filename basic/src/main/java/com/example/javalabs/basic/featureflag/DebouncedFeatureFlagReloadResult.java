package com.example.javalabs.basic.featureflag;

import java.util.Optional;

/**
 * Result of checking whether a debounced feature flag reload is ready to run.
 *
 * @param status current debounce status
 * @param dueAtMillis timestamp when the pending config becomes eligible
 * @param reloadResult rate-limited reload result when a pending config was flushed
 */
public record DebouncedFeatureFlagReloadResult(
        DebouncedReloadStatus status,
        long dueAtMillis,
        Optional<RateLimitedFeatureFlagReloadResult> reloadResult) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when status, due timestamp, or result container is invalid
     */
    public DebouncedFeatureFlagReloadResult {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (dueAtMillis < 0) {
            throw new IllegalArgumentException("dueAtMillis must not be negative");
        }
        if (reloadResult == null) {
            throw new IllegalArgumentException("reloadResult must not be null");
        }
    }

    /**
     * Creates an idle result when no config is pending.
     *
     * @param nowMillis current timestamp used as the result timestamp
     * @return idle debounce result
     * @throws IllegalArgumentException when {@code nowMillis} is negative
     */
    public static DebouncedFeatureFlagReloadResult idle(long nowMillis) {
        return new DebouncedFeatureFlagReloadResult(DebouncedReloadStatus.IDLE, nowMillis, Optional.empty());
    }

    /**
     * Creates a waiting result when config is pending but not due.
     *
     * @param dueAtMillis timestamp when the pending config becomes eligible
     * @return waiting debounce result
     * @throws IllegalArgumentException when {@code dueAtMillis} is negative
     */
    public static DebouncedFeatureFlagReloadResult waiting(long dueAtMillis) {
        return new DebouncedFeatureFlagReloadResult(DebouncedReloadStatus.WAITING, dueAtMillis, Optional.empty());
    }

    /**
     * Creates a flushed result after delegating reload work.
     *
     * @param dueAtMillis timestamp when the pending config became eligible
     * @param reloadResult downstream reload result
     * @return flushed debounce result with downstream reload output
     * @throws IllegalArgumentException when {@code dueAtMillis} is negative
     * @throws IllegalArgumentException when {@code reloadResult} is {@code null}
     */
    public static DebouncedFeatureFlagReloadResult flushed(
            long dueAtMillis,
            RateLimitedFeatureFlagReloadResult reloadResult) {
        if (reloadResult == null) {
            throw new IllegalArgumentException("reloadResult must not be null");
        }
        return new DebouncedFeatureFlagReloadResult(
                DebouncedReloadStatus.FLUSHED,
                dueAtMillis,
                Optional.of(reloadResult)
        );
    }

    /**
     * @return {@code true} when no config is pending
     */
    public boolean idle() {
        return status == DebouncedReloadStatus.IDLE;
    }

    /**
     * @return {@code true} when config is pending but not due yet
     */
    public boolean waiting() {
        return status == DebouncedReloadStatus.WAITING;
    }

    /**
     * @return {@code true} when pending config was flushed
     */
    public boolean flushed() {
        return status == DebouncedReloadStatus.FLUSHED;
    }
}
