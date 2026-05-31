package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CachedCustomerDirectoryTest {

    private final List<Customer> customers = List.of(
            new Customer("c-1", "Alice", "retail"),
            new Customer("c-2", "Bob", "enterprise"),
            new Customer("c-3", "Cara", "partner")
    );

    @Test
    void repeatedSingleLookupUsesCachedValueBeforeTtlExpires() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(customers);
        CachedCustomerDirectory cache = new CachedCustomerDirectory(delegate, 5_000, timeSource);

        assertEquals("Alice", cache.findById("c-1").orElseThrow().displayName());
        assertEquals("Alice", cache.findById("c-1").orElseThrow().displayName());

        assertEquals(1, delegate.singleLookupCount());
        assertEquals(1, cache.cachedEntryCount());
    }

    @Test
    void expiredEntryIsLoadedAgain() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(customers);
        CachedCustomerDirectory cache = new CachedCustomerDirectory(delegate, 5_000, timeSource);

        cache.findById("c-1");
        timeSource.advanceMillis(5_000);
        cache.findById("c-1");

        assertEquals(2, delegate.singleLookupCount());
    }

    @Test
    void batchLookupOnlyLoadsMissingCustomers() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(customers);
        CachedCustomerDirectory cache = new CachedCustomerDirectory(delegate, 5_000, timeSource);

        cache.findById("c-1");
        Map<String, Customer> result = cache.findByIds(List.of("c-1", "c-2", "c-3"));

        assertEquals(3, result.size());
        assertEquals(1, delegate.singleLookupCount());
        assertEquals(1, delegate.batchLookupCount());
        assertEquals(3, cache.cachedEntryCount());
    }

    @Test
    void invalidateForcesTheNextLookupToReload() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(customers);
        CachedCustomerDirectory cache = new CachedCustomerDirectory(delegate, 5_000, timeSource);

        cache.findById("c-1");
        cache.invalidate("c-1");
        cache.findById("c-1");

        assertEquals(2, delegate.singleLookupCount());
        assertTrue(cache.findById("missing").isEmpty());
    }

    @Test
    void boundedCacheEvictsLeastRecentlyUsedEntry() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(customers);
        CachedCustomerDirectory cache = new CachedCustomerDirectory(delegate, 5_000, timeSource, 2);

        cache.findById("c-1");
        cache.findById("c-2");
        cache.findById("c-1");
        cache.findById("c-3");

        assertEquals(2, cache.cachedEntryCount());
        assertEquals(3, delegate.singleLookupCount());

        cache.findById("c-2");

        assertEquals(4, delegate.singleLookupCount());
    }
}
