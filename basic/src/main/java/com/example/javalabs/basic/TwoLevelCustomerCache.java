package com.example.javalabs.basic;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Customer cache with a small L1 cache and a larger L2 cache.
 *
 * <p>L1 represents the fastest and smallest layer. L2 represents a bigger backup layer. A hit in
 * L2 is promoted back into L1 so frequently used customers stay close to the caller.</p>
 */
public final class TwoLevelCustomerCache implements CustomerDirectory {

    private final CustomerDirectory delegate;
    private final Map<String, Customer> l1Cache;
    private final Map<String, Customer> l2Cache;

    /**
     * Creates a two-level cache around a delegate directory.
     *
     * @param delegate source directory used on cache misses
     * @param l1Capacity maximum entries in the fast L1 cache
     * @param l2Capacity maximum entries in the larger L2 cache
     * @throws IllegalArgumentException when dependencies or capacities are invalid
     */
    public TwoLevelCustomerCache(CustomerDirectory delegate, int l1Capacity, int l2Capacity) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (l1Capacity <= 0) {
            throw new IllegalArgumentException("l1Capacity must be positive");
        }
        if (l2Capacity < l1Capacity) {
            throw new IllegalArgumentException("l2Capacity must be greater than or equal to l1Capacity");
        }
        this.delegate = delegate;
        this.l1Cache = lruMap(l1Capacity);
        this.l2Cache = lruMap(l2Capacity);
    }

    /**
     * Finds a customer through L1, then L2, then delegate.
     *
     * @param id customer identifier
     * @return customer when present
     * @throws IllegalArgumentException when {@code id} is blank
     */
    @Override
    public Optional<Customer> findById(String id) {
        validateId(id);

        Customer l1Customer = l1Cache.get(id);
        if (l1Customer != null) {
            return Optional.of(l1Customer);
        }

        Customer l2Customer = l2Cache.get(id);
        if (l2Customer != null) {
            // Promote L2 hits back into L1 so hot entries stay in the fastest layer.
            l1Cache.put(id, l2Customer);
            return Optional.of(l2Customer);
        }

        Optional<Customer> loaded = delegate.findById(id);
        loaded.ifPresent(customer -> {
            l1Cache.put(customer.id(), customer);
            l2Cache.put(customer.id(), customer);
        });
        return loaded;
    }

    /**
     * Finds many customers by reusing the single-id cache path for each id.
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
        Map<String, Customer> result = new LinkedHashMap<>();
        for (String id : ids) {
            findById(id).ifPresent(customer -> result.put(id, customer));
        }
        return result;
    }

    /**
     * Removes one customer from both cache layers.
     *
     * @param id customer identifier
     * @throws IllegalArgumentException when {@code id} is blank
     */
    public void invalidate(String id) {
        validateId(id);
        l1Cache.remove(id);
        l2Cache.remove(id);
    }

    /**
     * @return number of entries currently in L1
     */
    public int l1Size() {
        return l1Cache.size();
    }

    /**
     * @return number of entries currently in L2
     */
    public int l2Size() {
        return l2Cache.size();
    }

    /**
     * Creates an access-order LRU map.
     *
     * @param capacity maximum retained entries
     * @return mutable LRU map
     */
    private static Map<String, Customer> lruMap(int capacity) {
        return new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Customer> eldest) {
                // LinkedHashMap calls this after inserts; returning true evicts the least recently used entry.
                return size() > capacity;
            }
        };
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
}
