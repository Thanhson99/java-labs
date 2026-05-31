package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Summary of one bounded replay pass over reload alert dead letters.
 *
 * @param requestedLimit maximum number of dead letters requested for this pass
 * @param attempted number of dead letters actually replayed
 * @param delivered number of replayed alerts delivered to the sink
 * @param skipped number of replayed alerts skipped by the dispatcher
 * @param dispatchResults per-record dispatch outcomes
 */
public record FeatureFlagReloadAlertReplayResult(
        int requestedLimit,
        int attempted,
        int delivered,
        int skipped,
        List<FeatureFlagReloadAlertDispatchResult> dispatchResults) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when counters are inconsistent or dispatch results are invalid
     */
    public FeatureFlagReloadAlertReplayResult {
        if (requestedLimit <= 0) {
            throw new IllegalArgumentException("requestedLimit must be positive");
        }
        if (attempted < 0) {
            throw new IllegalArgumentException("attempted must not be negative");
        }
        if (delivered < 0) {
            throw new IllegalArgumentException("delivered must not be negative");
        }
        if (skipped < 0) {
            throw new IllegalArgumentException("skipped must not be negative");
        }
        if (attempted != delivered + skipped) {
            throw new IllegalArgumentException("attempted must equal delivered plus skipped");
        }
        if (dispatchResults == null) {
            throw new IllegalArgumentException("dispatchResults must not be null");
        }
        dispatchResults = List.copyOf(dispatchResults);
        if (dispatchResults.size() != attempted) {
            throw new IllegalArgumentException("dispatchResults size must equal attempted");
        }
    }

    /**
     * @return {@code true} when at least one replay was attempted and none were skipped
     */
    public boolean allDelivered() {
        return attempted > 0 && skipped == 0;
    }
}
