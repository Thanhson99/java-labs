package com.example.javalabs.basic.metrics;

/**
 * Immutable throughput view from {@link BucketedThroughputWindow}.
 *
 * @param totalEvents number of retained events inside the rolling window
 * @param windowMillis total window size represented by all buckets
 * @param eventsPerSecond retained event rate normalized to one second
 */
public record ThroughputSnapshot(
        long totalEvents,
        long windowMillis,
        double eventsPerSecond) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when event count, window, or rate is invalid
     */
    public ThroughputSnapshot {
        if (totalEvents < 0) {
            throw new IllegalArgumentException("totalEvents must not be negative");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        if (eventsPerSecond < 0.0) {
            throw new IllegalArgumentException("eventsPerSecond must not be negative");
        }
    }
}
