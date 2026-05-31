package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerLookupServiceTest {

    @Test
    void deduplicatesIdsBeforeBatchLookupAndPreservesOriginalOrder() {
        RecordingCustomerDirectory directory = new RecordingCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail"),
                new Customer("c-2", "Bob", "enterprise"),
                new Customer("c-3", "Cara", "partner")
        ));
        CustomerLookupService service = new CustomerLookupService(directory);

        CustomerLookupResult result =
                service.loadCustomersPreservingOrder(List.of("c-1", "c-2", "c-1", "c-3", "c-2"));

        assertEquals(5, result.requestedCount());
        assertEquals(3, result.uniqueLookupCount());
        assertEquals(List.of("Alice", "Bob", "Alice", "Cara", "Bob"),
                result.customers().stream().map(Customer::displayName).toList());
        assertEquals(1, directory.batchLookupCount());
        assertEquals(Set.of("c-1", "c-2", "c-3"), directory.lastRequestedIds());
    }

    @Test
    void failsWhenAnyRequestedCustomerIsMissing() {
        RecordingCustomerDirectory directory = new RecordingCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail")
        ));
        CustomerLookupService service = new CustomerLookupService(directory);

        assertThrows(IllegalStateException.class,
                () -> service.loadCustomersPreservingOrder(List.of("c-1", "missing")));
    }

    private static final class RecordingCustomerDirectory implements CustomerDirectory {
        private final Map<String, Customer> customersById;
        private int batchLookupCount;
        private Set<String> lastRequestedIds = Set.of();

        private RecordingCustomerDirectory(List<Customer> customers) {
            this.customersById = customers.stream()
                    .collect(java.util.stream.Collectors.toMap(Customer::id, customer -> customer));
        }

        @Override
        public Optional<Customer> findById(String id) {
            return Optional.ofNullable(customersById.get(id));
        }

        @Override
        public Map<String, Customer> findByIds(Collection<String> ids) {
            batchLookupCount++;
            lastRequestedIds = new HashSet<>(ids);
            return ids.stream()
                    .filter(customersById::containsKey)
                    .collect(java.util.stream.Collectors.toMap(id -> id, customersById::get));
        }

        private int batchLookupCount() {
            return batchLookupCount;
        }

        private Set<String> lastRequestedIds() {
            return lastRequestedIds;
        }
    }
}
