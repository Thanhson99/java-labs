package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.ServiceCallOutcome;
import com.example.javalabs.basic.metrics.ErrorRateSnapshot;

/**
 * Adjusts concurrency limits from recent service-call health.
 *
 * <p>The limiter starts with an initial concurrency limit. Unhealthy error-rate snapshots reduce
 * the limit quickly, while healthy successful completions raise it gradually after a recovery
 * streak. This mirrors the production pattern of fast backoff and slow recovery.</p>
 */
public final class AdaptiveConcurrencyLimiter {

    private final int minLimit;
    private final int maxLimit;
    private final int decreaseStep;
    private final int increaseStep;
    private final int recoveryStreakThreshold;
    private int currentLimit;
    private int inFlight;
    private int healthyStreak;

    /**
     * Creates an adaptive concurrency limiter.
     *
     * @param minLimit lowest allowed concurrency limit
     * @param maxLimit highest allowed concurrency limit
     * @param initialLimit starting concurrency limit
     * @param decreaseStep amount removed from the limit when health is unhealthy
     * @param increaseStep amount added after enough healthy completions
     * @param recoveryStreakThreshold healthy completions required before raising the limit
     * @throws IllegalArgumentException when limits or steps are invalid
     */
    public AdaptiveConcurrencyLimiter(
            int minLimit,
            int maxLimit,
            int initialLimit,
            int decreaseStep,
            int increaseStep,
            int recoveryStreakThreshold) {
        if (minLimit <= 0) {
            throw new IllegalArgumentException("minLimit must be positive");
        }
        if (maxLimit < minLimit) {
            throw new IllegalArgumentException("maxLimit must be greater than or equal to minLimit");
        }
        if (initialLimit < minLimit || initialLimit > maxLimit) {
            throw new IllegalArgumentException("initialLimit must be between minLimit and maxLimit");
        }
        if (decreaseStep <= 0) {
            throw new IllegalArgumentException("decreaseStep must be positive");
        }
        if (increaseStep <= 0) {
            throw new IllegalArgumentException("increaseStep must be positive");
        }
        if (recoveryStreakThreshold <= 0) {
            throw new IllegalArgumentException("recoveryStreakThreshold must be positive");
        }
        this.minLimit = minLimit;
        this.maxLimit = maxLimit;
        this.currentLimit = initialLimit;
        this.decreaseStep = decreaseStep;
        this.increaseStep = increaseStep;
        this.recoveryStreakThreshold = recoveryStreakThreshold;
    }

    /**
     * Attempts to acquire one concurrency slot.
     *
     * @return true when a slot was acquired
     */
    public synchronized boolean tryAcquire() {
        if (inFlight >= currentLimit) {
            return false;
        }
        inFlight++;
        return true;
    }

    /**
     * Completes one acquired slot and adjusts the concurrency limit from current health.
     *
     * @param outcome outcome of the completed work
     * @param errorRateSnapshot recent error-rate health snapshot
     * @return updated limiter state
     * @throws IllegalArgumentException when arguments are {@code null}
     * @throws IllegalStateException when no slot is in flight
     */
    public synchronized AdaptiveConcurrencySnapshot complete(
            ServiceCallOutcome outcome,
            ErrorRateSnapshot errorRateSnapshot) {
        if (outcome == null) {
            throw new IllegalArgumentException("outcome must not be null");
        }
        if (errorRateSnapshot == null) {
            throw new IllegalArgumentException("errorRateSnapshot must not be null");
        }
        if (inFlight == 0) {
            throw new IllegalStateException("no in-flight work to complete");
        }

        inFlight--;
        if (!errorRateSnapshot.healthy()) {
            decreaseLimit();
        } else if (outcome == ServiceCallOutcome.SUCCESS) {
            increaseAfterHealthyStreak();
        } else {
            healthyStreak = 0;
        }
        return snapshot();
    }

    /**
     * Returns the current limiter state.
     *
     * @return immutable limiter snapshot
     */
    public synchronized AdaptiveConcurrencySnapshot snapshot() {
        return new AdaptiveConcurrencySnapshot(currentLimit, inFlight, healthyStreak);
    }

    /**
     * Reduces the current limit quickly when recent health is poor.
     */
    private void decreaseLimit() {
        currentLimit = Math.max(minLimit, currentLimit - decreaseStep);
        healthyStreak = 0;
    }

    /**
     * Raises the current limit only after repeated healthy completions.
     */
    private void increaseAfterHealthyStreak() {
        healthyStreak++;
        if (healthyStreak >= recoveryStreakThreshold) {
            currentLimit = Math.min(maxLimit, currentLimit + increaseStep);
            healthyStreak = 0;
        }
    }
}
