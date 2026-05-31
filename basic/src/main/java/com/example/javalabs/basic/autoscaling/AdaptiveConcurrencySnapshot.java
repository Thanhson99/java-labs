package com.example.javalabs.basic.autoscaling;

/**
 * Immutable state view of {@link AdaptiveConcurrencyLimiter}.
 *
 * @param currentLimit current maximum concurrent work allowed
 * @param inFlight current number of acquired slots; it may temporarily exceed the current limit
 *        after an adaptive decrease until older work finishes
 * @param healthyStreak consecutive healthy completions counted toward limit recovery
 */
public record AdaptiveConcurrencySnapshot(
        int currentLimit,
        int inFlight,
        int healthyStreak) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when counters are negative or inconsistent
     */
    public AdaptiveConcurrencySnapshot {
        if (currentLimit <= 0) {
            throw new IllegalArgumentException("currentLimit must be positive");
        }
        if (inFlight < 0) {
            throw new IllegalArgumentException("inFlight must not be negative");
        }
        if (healthyStreak < 0) {
            throw new IllegalArgumentException("healthyStreak must not be negative");
        }
    }
}
