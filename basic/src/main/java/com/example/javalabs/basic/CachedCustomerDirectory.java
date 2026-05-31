package com.example.javalabs.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Cache decorator for {@link CustomerDirectory}.
 *
 * <p>This class demonstrates a common backend optimization: keep hot read data in memory for a
 * short time, but let the cached value expire so stale data does not live forever.</p>
 */
public final class CachedCustomerDirectory implements CustomerDirectory {

    private final CustomerDirectory delegate;
    private final long ttlMillis;
    private final TimeSource timeSource;
    private final int maxEntries;
    private final Map<String, CacheEntry> cache;

    /**
     * Creates an unbounded TTL cache around another directory.
     *
     * @param delegate source directory used on cache misses
     * @param ttlMillis time-to-live for cached customers in milliseconds
     * @param timeSource clock used to make expiration deterministic in tests
     * @throws IllegalArgumentException when dependencies or TTL are invalid
     */
    public CachedCustomerDirectory(CustomerDirectory delegate, long ttlMillis, TimeSource timeSource) {
        this(delegate, ttlMillis, timeSource, Integer.MAX_VALUE);
    }

    /**
     * Creates a bounded TTL cache around another directory.
     *
     * @param delegate source directory used on cache misses
     * @param ttlMillis time-to-live for cached customers in milliseconds
     * @param timeSource clock used to make expiration deterministic in tests
     * @param maxEntries maximum number of customers retained in the cache
     * @throws IllegalArgumentException when dependencies, TTL, or size limit are invalid
     */
    public CachedCustomerDirectory(
            CustomerDirectory delegate,
            long ttlMillis,
            TimeSource timeSource,
            int maxEntries) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("ttlMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("maxEntries must be positive");
        }
        this.delegate = delegate;
        this.ttlMillis = ttlMillis;
        this.timeSource = timeSource;
        this.maxEntries = maxEntries;
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                // Access-order LinkedHashMap makes this a small LRU cache when maxEntries is reached.
                return size() > CachedCustomerDirectory.this.maxEntries;
            }
        };
    }

    /**
     * Finds one customer, using the cache when the entry is still fresh.
     *
     * @param id customer identifier
     * @return customer when present in cache or delegate
     * @throws IllegalArgumentException when {@code id} is blank
     */
    @Override
    public Optional<Customer> findById(String id) {
        validateId(id);
        long now = timeSource.currentTimeMillis();
        Customer cachedCustomer = getIfFresh(id, now);
        if (cachedCustomer != null) {
            return Optional.of(cachedCustomer);
        }

        Optional<Customer> loadedCustomer = delegate.findById(id);
        loadedCustomer.ifPresent(customer -> put(customer, now));
        return loadedCustomer;
    }

    /**
     * Finds many customers while loading only cache misses from the delegate.
     *
     * @param ids customer identifiers to load
     * @return customers keyed by id
     * @throws IllegalArgumentException when {@code ids} is {@code null} or contains blank values
     */
    @Override
    public Map<String, Customer> findByIds(Collection<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("ids must not be null");
        }

        long now = timeSource.currentTimeMillis();
        Map<String, Customer> result = new HashMap<>();
        Set<String> misses = new LinkedHashSet<>();

        for (String id : ids) {
            validateId(id);
            Customer cachedCustomer = getIfFresh(id, now);
            if (cachedCustomer == null) {
                // LinkedHashSet deduplicates misses while preserving deterministic lookup order.
                misses.add(id);
            } else {
                result.put(id, cachedCustomer);
            }
        }

        if (!misses.isEmpty()) {
            Map<String, Customer> loadedCustomers = delegate.findByIds(misses);
            loadedCustomers.values().forEach(customer -> put(customer, now));
            result.putAll(loadedCustomers);
        }

        return result;
    }

    /**
     * Removes one customer from the cache after a write or external update.
     *
     * @param id customer identifier
     * @throws IllegalArgumentException when {@code id} is blank
     */
    public void invalidate(String id) {
        validateId(id);
        cache.remove(id);
    }

    /**
     * @return number of entries currently retained in cache
     */
    public int cachedEntryCount() {
        return cache.size();
    }

    /**
     * Returns a cached customer only when the entry is still inside its TTL window.
     *
     * @param id customer identifier
     * @param now current timestamp in milliseconds
     * @return cached customer, or {@code null} when missing or expired
     */
    private Customer getIfFresh(String id, long now) {
        CacheEntry entry = cache.get(id);
        if (entry == null) {
            return null;
        }
        if (now - entry.cachedAtMillis >= ttlMillis) {
            cache.remove(id);
            return null;
        }
        return entry.customer();
    }

    /**
     * Stores a freshly loaded customer in the cache.
     *
     * @param customer customer loaded from the delegate
     * @param now current timestamp in milliseconds
     */
    private void put(Customer customer, long now) {
        cache.put(customer.id(), new CacheEntry(customer, now));
    }

    /**
     * Validates a customer id shared by cache operations.
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
     * Cache value plus the timestamp when it was loaded.
     *
     * @param customer cached customer
     * @param cachedAtMillis timestamp when the customer was cached
     */
    private record CacheEntry(Customer customer, long cachedAtMillis) {
    }
}
