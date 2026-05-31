package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaleCustomerCacheTest {

    @Test
    void returnsFreshCachedCustomerInsideFreshTtl() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        MutableCustomerDirectory delegate = new MutableCustomerDirectory(new Customer("c-1", "Alice", "retail"));
        StaleCustomerCache cache = new StaleCustomerCache(delegate, 1_000, 5_000, timeSource);

        assertEquals("Alice", cache.get("c-1").orElseThrow().displayName());
        delegate.customer = new Customer("c-1", "Alice Updated", "retail");
        timeSource.advanceMillis(500);

        assertEquals("Alice", cache.get("c-1").orElseThrow().displayName());
        assertEquals(1, delegate.lookupCount);
    }

    @Test
    void refreshesWhenValueIsStaleButStillInsideStaleWindow() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        MutableCustomerDirectory delegate = new MutableCustomerDirectory(new Customer("c-1", "Alice", "retail"));
        StaleCustomerCache cache = new StaleCustomerCache(delegate, 1_000, 5_000, timeSource);

        cache.get("c-1");
        delegate.customer = new Customer("c-1", "Alice Updated", "retail");
        timeSource.advanceMillis(1_000);

        assertTrue(cache.isStale("c-1"));
        assertEquals("Alice Updated", cache.get("c-1").orElseThrow().displayName());
        assertFalse(cache.isStale("c-1"));
        assertEquals(2, delegate.lookupCount);
    }

    @Test
    void servesStaleValueWhenRefreshFailsInsideStaleWindow() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        MutableCustomerDirectory delegate = new MutableCustomerDirectory(new Customer("c-1", "Alice", "retail"));
        StaleCustomerCache cache = new StaleCustomerCache(delegate, 1_000, 5_000, timeSource);

        cache.get("c-1");
        delegate.fail = true;
        timeSource.advanceMillis(1_000);

        assertEquals("Alice", cache.get("c-1").orElseThrow().displayName());
        assertTrue(cache.isStale("c-1"));
    }

    @Test
    void failsWhenValueIsBeyondStaleWindowAndReloadFails() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        MutableCustomerDirectory delegate = new MutableCustomerDirectory(new Customer("c-1", "Alice", "retail"));
        StaleCustomerCache cache = new StaleCustomerCache(delegate, 1_000, 5_000, timeSource);

        cache.get("c-1");
        delegate.fail = true;
        timeSource.advanceMillis(5_000);

        assertThrows(IllegalStateException.class, () -> cache.get("c-1"));
    }

    private static final class MutableCustomerDirectory implements CustomerDirectory {
        private Customer customer;
        private boolean fail;
        private int lookupCount;

        private MutableCustomerDirectory(Customer customer) {
            this.customer = customer;
        }

        @Override
        public Optional<Customer> findById(String id) {
            lookupCount++;
            if (fail) {
                throw new IllegalStateException("customer source unavailable");
            }
            return Optional.ofNullable(customer);
        }

        @Override
        public Map<String, Customer> findByIds(Collection<String> ids) {
            throw new UnsupportedOperationException("not needed in this test");
        }
    }
}
