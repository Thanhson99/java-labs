package com.example.javalabs.basic;

import java.util.Optional;

/**
 * Cache for one customer that supports stale-while-revalidate behavior.
 *
 * <p>Fresh data is returned normally. Recently stale data can still be returned as a fallback while
 * a refresh is attempted. This keeps reads available during short downstream failures.</p>
 */
public final class StaleCustomerCache {

    private final CustomerDirectory delegate;
    private final long freshTtlMillis;
    private final long staleTtlMillis;
    private final TimeSource timeSource;
    private CacheEntry entry;

    /**
     * Creates a stale-while-revalidate cache for one customer at a time.
     *
     * @param delegate source directory used to load and refresh customers
     * @param freshTtlMillis time during which data is considered fresh
     * @param staleTtlMillis time during which stale data may still be served
     * @param timeSource clock used to make freshness checks deterministic
     * @throws IllegalArgumentException when dependencies or TTL values are invalid
     */
    public StaleCustomerCache(
            CustomerDirectory delegate,
            long freshTtlMillis,
            long staleTtlMillis,
            TimeSource timeSource) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (freshTtlMillis <= 0) {
            throw new IllegalArgumentException("freshTtlMillis must be positive");
        }
        if (staleTtlMillis < freshTtlMillis) {
            throw new IllegalArgumentException("staleTtlMillis must be greater than or equal to freshTtlMillis");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.delegate = delegate;
        this.freshTtlMillis = freshTtlMillis;
        this.staleTtlMillis = staleTtlMillis;
        this.timeSource = timeSource;
    }

    /**
     * Returns a customer using fresh data, stale fallback, or a delegate load.
     *
     * @param customerId customer identifier
     * @return customer when found
     * @throws IllegalArgumentException when {@code customerId} is blank
     * @throws IllegalStateException when the delegate cannot find the customer outside a stale fallback
     */
    public Optional<Customer> get(String customerId) {
        validateId(customerId);
        long now = timeSource.currentTimeMillis();
        if (entry != null && entry.customerId().equals(customerId)) {
            long age = now - entry.loadedAtMillis();
            if (age < freshTtlMillis) {
                return Optional.of(entry.customer());
            }
            if (age < staleTtlMillis) {
                // Try to refresh, but still return the existing stale value if refresh fails.
                tryRefresh(customerId, now);
                return Optional.of(entry.customer());
            }
        }

        Customer loaded = delegate.findById(customerId)
                .orElseThrow(() -> new IllegalStateException("customer not found: " + customerId));
        entry = new CacheEntry(customerId, loaded, now);
        return Optional.of(loaded);
    }

    /**
     * Checks whether the cached value for an id is inside the stale window.
     *
     * @param customerId customer identifier
     * @return {@code true} when data is no longer fresh but still serveable as stale
     * @throws IllegalArgumentException when {@code customerId} is blank
     */
    public boolean isStale(String customerId) {
        validateId(customerId);
        if (entry == null || !entry.customerId().equals(customerId)) {
            return false;
        }
        long age = timeSource.currentTimeMillis() - entry.loadedAtMillis();
        return age >= freshTtlMillis && age < staleTtlMillis;
    }

    /**
     * Attempts to refresh stale data without breaking fallback behavior.
     *
     * @param customerId customer identifier
     * @param now current timestamp in milliseconds
     */
    private void tryRefresh(String customerId, long now) {
        try {
            Optional<Customer> refreshed = delegate.findById(customerId);
            refreshed.ifPresent(customer -> entry = new CacheEntry(customerId, customer, now));
        } catch (RuntimeException ignored) {
            // Keep serving the stale value inside the stale window.
        }
    }

    /**
     * Validates a customer id.
     *
     * @param customerId customer identifier
     * @throws IllegalArgumentException when {@code customerId} is blank
     */
    private static void validateId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
    }

    /**
     * Single cached customer value plus load timestamp.
     *
     * @param customerId customer identifier
     * @param customer cached customer
     * @param loadedAtMillis load timestamp
     */
    private record CacheEntry(String customerId, Customer customer, long loadedAtMillis) {
    }
}
