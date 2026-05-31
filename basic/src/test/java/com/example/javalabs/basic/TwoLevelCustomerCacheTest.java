package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TwoLevelCustomerCacheTest {

    @Test
    void returnsL1HitWithoutCallingDelegateAgain() {
        InMemoryCustomerDirectory delegate = directory();
        TwoLevelCustomerCache cache = new TwoLevelCustomerCache(delegate, 2, 4);

        assertEquals("Alice", cache.findById("c-1").orElseThrow().displayName());
        assertEquals("Alice", cache.findById("c-1").orElseThrow().displayName());

        assertEquals(1, delegate.singleLookupCount());
        assertEquals(1, cache.l1Size());
        assertEquals(1, cache.l2Size());
    }

    @Test
    void promotesL2HitBackIntoL1AfterL1Eviction() {
        InMemoryCustomerDirectory delegate = directory();
        TwoLevelCustomerCache cache = new TwoLevelCustomerCache(delegate, 1, 3);

        cache.findById("c-1");
        cache.findById("c-2");

        assertEquals(2, delegate.singleLookupCount());
        assertEquals("Alice", cache.findById("c-1").orElseThrow().displayName());

        assertEquals(2, delegate.singleLookupCount());
        assertEquals(1, cache.l1Size());
        assertEquals(2, cache.l2Size());
    }

    @Test
    void boundsBothCacheLayers() {
        InMemoryCustomerDirectory delegate = directory();
        TwoLevelCustomerCache cache = new TwoLevelCustomerCache(delegate, 2, 3);

        cache.findById("c-1");
        cache.findById("c-2");
        cache.findById("c-3");
        cache.findById("c-4");

        assertEquals(2, cache.l1Size());
        assertEquals(3, cache.l2Size());
        assertEquals(4, delegate.singleLookupCount());

        cache.findById("c-1");

        assertEquals(5, delegate.singleLookupCount());
    }

    @Test
    void batchLookupReusesCacheForDuplicateIds() {
        InMemoryCustomerDirectory delegate = directory();
        TwoLevelCustomerCache cache = new TwoLevelCustomerCache(delegate, 2, 4);

        Map<String, Customer> result = cache.findByIds(List.of("c-1", "c-2", "c-1"));

        assertEquals(List.of("c-1", "c-2"), result.keySet().stream().toList());
        assertEquals(2, delegate.singleLookupCount());
    }

    @Test
    void invalidateRemovesCustomerFromBothLayers() {
        InMemoryCustomerDirectory delegate = directory();
        TwoLevelCustomerCache cache = new TwoLevelCustomerCache(delegate, 2, 4);

        cache.findById("c-1");
        cache.invalidate("c-1");
        cache.findById("c-1");

        assertEquals(2, delegate.singleLookupCount());
    }

    @Test
    void rejectsInvalidConfiguration() {
        InMemoryCustomerDirectory delegate = directory();

        assertThrows(IllegalArgumentException.class, () -> new TwoLevelCustomerCache(null, 1, 2));
        assertThrows(IllegalArgumentException.class, () -> new TwoLevelCustomerCache(delegate, 0, 2));
        assertThrows(IllegalArgumentException.class, () -> new TwoLevelCustomerCache(delegate, 2, 1));
    }

    private static InMemoryCustomerDirectory directory() {
        return new InMemoryCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail"),
                new Customer("c-2", "Bob", "retail"),
                new Customer("c-3", "Cara", "enterprise"),
                new Customer("c-4", "Duy", "enterprise")
        ));
    }
}
