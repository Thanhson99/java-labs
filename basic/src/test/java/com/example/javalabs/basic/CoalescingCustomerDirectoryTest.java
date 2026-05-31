package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoalescingCustomerDirectoryTest {

    @Test
    void concurrentLookupsForSameIdShareOneDelegateCall() throws Exception {
        BlockingCustomerDirectory delegate =
                new BlockingCustomerDirectory(new Customer("c-1", "Alice", "retail"));
        CoalescingCustomerDirectory directory = new CoalescingCustomerDirectory(delegate);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Optional<Customer>> first = executor.submit(() -> directory.findById("c-1"));
        assertTrue(delegate.awaitStarted(1_000));
        assertEquals(1, directory.inFlightLookupCount());

        Future<Optional<Customer>> second = executor.submit(() -> directory.findById("c-1"));
        Thread.sleep(50);
        assertEquals(1, delegate.lookupCount());

        delegate.release();

        assertEquals("Alice", first.get(1, TimeUnit.SECONDS).orElseThrow().displayName());
        assertEquals("Alice", second.get(1, TimeUnit.SECONDS).orElseThrow().displayName());
        assertEquals(1, delegate.lookupCount());
        assertEquals(0, directory.inFlightLookupCount());
        executor.shutdownNow();
    }

    @Test
    void sequentialLookupsCallDelegateAgainAfterInFlightLookupCompletes() {
        InMemoryCustomerDirectory delegate = new InMemoryCustomerDirectory(java.util.List.of(
                new Customer("c-1", "Alice", "retail")
        ));
        CoalescingCustomerDirectory directory = new CoalescingCustomerDirectory(delegate);

        directory.findById("c-1");
        directory.findById("c-1");

        assertEquals(2, delegate.singleLookupCount());
        assertEquals(0, directory.inFlightLookupCount());
    }

    @Test
    void rejectsBlankId() {
        CoalescingCustomerDirectory directory =
                new CoalescingCustomerDirectory(new InMemoryCustomerDirectory(java.util.List.of(
                        new Customer("c-1", "Alice", "retail")
                )));

        assertThrows(IllegalArgumentException.class, () -> directory.findById(" "));
    }

    private static final class BlockingCustomerDirectory implements CustomerDirectory {
        private final Customer customer;
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);
        private int lookupCount;

        private BlockingCustomerDirectory(Customer customer) {
            this.customer = customer;
        }

        @Override
        public Optional<Customer> findById(String id) {
            lookupCount++;
            started.countDown();
            try {
                release.await();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("lookup interrupted", exception);
            }
            return Optional.of(customer);
        }

        @Override
        public Map<String, Customer> findByIds(Collection<String> ids) {
            throw new UnsupportedOperationException("not needed in this test");
        }

        private boolean awaitStarted(long timeoutMillis) throws InterruptedException {
            return started.await(timeoutMillis, TimeUnit.MILLISECONDS);
        }

        private void release() {
            release.countDown();
        }

        private int lookupCount() {
            return lookupCount;
        }
    }
}
