package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NegativeCachingCustomerDirectoryTest {

    @Test
    void cachesMissingCustomerWithinTtl() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail")
        ));
        NegativeCachingCustomerDirectory cache =
                new NegativeCachingCustomerDirectory(delegate, 5_000, timeSource);

        assertTrue(cache.findById("missing").isEmpty());
        assertTrue(cache.findById("missing").isEmpty());

        assertEquals(1, delegate.singleLookupCount());
        assertEquals(1, cache.cachedEntryCount());
    }

    @Test
    void expiredMissingCustomerIsLoadedAgain() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail")
        ));
        NegativeCachingCustomerDirectory cache =
                new NegativeCachingCustomerDirectory(delegate, 5_000, timeSource);

        cache.findById("missing");
        timeSource.advanceMillis(5_000);
        cache.findById("missing");

        assertEquals(2, delegate.singleLookupCount());
    }

    @Test
    void invalidatingMissingCustomerForcesReload() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail")
        ));
        NegativeCachingCustomerDirectory cache =
                new NegativeCachingCustomerDirectory(delegate, 5_000, timeSource);

        cache.findById("missing");
        cache.invalidate("missing");
        cache.findById("missing");

        assertEquals(2, delegate.singleLookupCount());
        assertEquals(1, cache.cachedEntryCount());
    }

    @Test
    void cachesFoundCustomerToo() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail")
        ));
        NegativeCachingCustomerDirectory cache =
                new NegativeCachingCustomerDirectory(delegate, 5_000, timeSource);

        assertEquals("Alice", cache.findById("c-1").orElseThrow().displayName());
        assertEquals("Alice", cache.findById("c-1").orElseThrow().displayName());

        assertEquals(1, delegate.singleLookupCount());
    }
}
