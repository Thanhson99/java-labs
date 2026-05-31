package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CacheWarmupServiceTest {

    @Test
    void preloadsUniqueCustomerIdsIntoCache() {
        InMemoryCustomerDirectory delegate = directory();
        TwoLevelCustomerCache cache = new TwoLevelCustomerCache(delegate, 2, 4);
        CacheWarmupService service = new CacheWarmupService(cache);

        CacheWarmupReport report = service.warmup(List.of("c-1", "c-2", "c-1"));

        assertEquals(List.of("c-1", "c-2"), report.requestedIds());
        assertEquals(List.of("c-1", "c-2"), report.loadedIds());
        assertEquals(List.of(), report.missingIds());
        assertEquals(2, delegate.singleLookupCount());

        cache.findById("c-1");
        cache.findById("c-2");

        assertEquals(2, delegate.singleLookupCount());
    }

    @Test
    void reportsMissingIdsWithoutFailingWholeWarmup() {
        InMemoryCustomerDirectory delegate = directory();
        CacheWarmupService service = new CacheWarmupService(new TwoLevelCustomerCache(delegate, 2, 4));

        CacheWarmupReport report = service.warmup(List.of("c-1", "missing", "c-2"));

        assertEquals(3, report.requestedCount());
        assertEquals(2, report.loadedCount());
        assertEquals(1, report.missingCount());
        assertEquals(List.of("missing"), report.missingIds());
        assertEquals(3, delegate.singleLookupCount());
    }

    @Test
    void emptyWarmupDoesNotCallDelegate() {
        InMemoryCustomerDirectory delegate = directory();
        CacheWarmupService service = new CacheWarmupService(new TwoLevelCustomerCache(delegate, 2, 4));

        CacheWarmupReport report = service.warmup(List.of());

        assertEquals(0, report.requestedCount());
        assertEquals(0, delegate.singleLookupCount());
    }

    @Test
    void rejectsInvalidInputs() {
        InMemoryCustomerDirectory delegate = directory();

        assertThrows(IllegalArgumentException.class, () -> new CacheWarmupService(null));

        CacheWarmupService service = new CacheWarmupService(new TwoLevelCustomerCache(delegate, 2, 4));
        assertThrows(IllegalArgumentException.class, () -> service.warmup(null));
        assertThrows(IllegalArgumentException.class, () -> service.warmup(List.of("c-1", " ")));
    }

    private static InMemoryCustomerDirectory directory() {
        return new InMemoryCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail"),
                new Customer("c-2", "Bob", "retail"),
                new Customer("c-3", "Cara", "enterprise")
        ));
    }
}
