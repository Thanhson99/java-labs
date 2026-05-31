package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderSummaryServiceTest {

    private final List<Customer> customers = List.of(
            new Customer("c-1", "Alice", "retail"),
            new Customer("c-2", "Bob", "enterprise")
    );

    private final List<Order> orders = List.of(
            new Order("o-1", "c-1", 120.0),
            new Order("o-2", "c-1", 35.0),
            new Order("o-3", "c-2", 900.0)
    );

    @Test
    void repeatedLookupBuildsSummariesButQueriesOncePerOrder() {
        InMemoryCustomerDirectory directory = new InMemoryCustomerDirectory(customers);
        OrderSummaryService service = new OrderSummaryService(directory);

        List<OrderSummary> summaries = service.buildSummariesWithRepeatedLookup(orders);

        assertEquals(3, summaries.size());
        assertEquals("Alice", summaries.get(0).customerName());
        assertEquals(3, directory.singleLookupCount());
        assertEquals(0, directory.batchLookupCount());
    }

    @Test
    void batchLookupBuildsSameSummariesWithOneDataAccessCall() {
        InMemoryCustomerDirectory directory = new InMemoryCustomerDirectory(customers);
        OrderSummaryService service = new OrderSummaryService(directory);

        List<OrderSummary> summaries = service.buildSummariesWithBatchLookup(orders);

        assertEquals(List.of(
                new OrderSummary("o-1", "Alice", "retail", 120.0),
                new OrderSummary("o-2", "Alice", "retail", 35.0),
                new OrderSummary("o-3", "Bob", "enterprise", 900.0)
        ), summaries);
        assertEquals(0, directory.singleLookupCount());
        assertEquals(1, directory.batchLookupCount());
    }

    @Test
    void missingCustomerFailsAtTheServiceBoundary() {
        InMemoryCustomerDirectory directory = new InMemoryCustomerDirectory(customers);
        OrderSummaryService service = new OrderSummaryService(directory);

        List<Order> invalidOrders = List.of(new Order("o-9", "missing", 12.0));

        assertThrows(IllegalStateException.class,
                () -> service.buildSummariesWithBatchLookup(invalidOrders));
    }
}
