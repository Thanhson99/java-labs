package com.example.javalabs.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Caches both found and missing single-customer lookups for a short time.
 *
 * <p>Regular caches often store only successful lookups. Negative caching also stores "not found"
 * results so repeated requests for a missing id do not keep hitting the repository.</p>
 */
public final class NegativeCachingCustomerDirectory implements CustomerDirectory {

    private final CustomerDirectory delegate;
    private final long ttlMillis;
    private final TimeSource timeSource;
    private final Map<String, CacheEntry> cache = new HashMap<>();

    /**
     * Creates a negative cache around a customer directory.
     *
     * @param delegate source directory used on cache misses
     * @param ttlMillis time-to-live for found and not-found results
     * @param timeSource clock used to make expiration deterministic in tests
     * @throws IllegalArgumentException when dependencies or TTL are invalid
     */
    public NegativeCachingCustomerDirectory(CustomerDirectory delegate, long ttlMillis, TimeSource timeSource) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("ttlMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.delegate = delegate;
        this.ttlMillis = ttlMillis;
        this.timeSource = timeSource;
    }

    /**
     * Finds a customer, caching both present and empty results.
     *
     * @param id customer identifier
     * @return customer when present
     * @throws IllegalArgumentException when {@code id} is blank
     */
    @Override
    public Optional<Customer> findById(String id) {
        validateId(id);
        long now = timeSource.currentTimeMillis();
        CacheEntry cached = cache.get(id);
        if (cached != null && now - cached.cachedAtMillis < ttlMillis) {
            // Empty Optional is cached too, preventing repeated misses from hitting the delegate.
            return cached.customer;
        }

        Optional<Customer> loaded = delegate.findById(id);
        cache.put(id, new CacheEntry(loaded, now));
        return loaded;
    }

    /**
     * Delegates batch lookups directly because this example focuses on single-id negative caching.
     *
     * @param ids customer identifiers to load
     * @return customers keyed by id
     */
    @Override
    public Map<String, Customer> findByIds(Collection<String> ids) {
        return delegate.findByIds(ids);
    }

    /**
     * Removes one cached positive or negative result.
     *
     * @param id customer identifier
     * @throws IllegalArgumentException when {@code id} is blank
     */
    public void invalidate(String id) {
        validateId(id);
        cache.remove(id);
    }

    /**
     * @return number of positive and negative entries retained in cache
     */
    public int cachedEntryCount() {
        return cache.size();
    }

    /**
     * Validates a customer id.
     *
     * @param id customer identifier
     * @throws IllegalArgumentException when {@code id} is blank
     */
    private static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
    }

    /**
     * Cached lookup result, including empty results.
     *
     * @param customer optional customer from the delegate
     * @param cachedAtMillis time when the lookup result was cached
     */
    private record CacheEntry(Optional<Customer> customer, long cachedAtMillis) {
    }
}
