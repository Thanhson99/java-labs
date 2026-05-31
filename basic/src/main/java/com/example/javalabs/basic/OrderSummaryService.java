package com.example.javalabs.basic;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demonstrates a practical backend optimization: replacing repeated lookups with batch loading.
 */
public final class OrderSummaryService {

    private final CustomerDirectory customerDirectory;

    /**
     * Creates a service that enriches orders with customer data.
     *
     * @param customerDirectory directory used to fetch customer details
     * @throws IllegalArgumentException when {@code customerDirectory} is null
     */
    public OrderSummaryService(CustomerDirectory customerDirectory) {
        if (customerDirectory == null) {
            throw new IllegalArgumentException("customerDirectory must not be null");
        }
        this.customerDirectory = customerDirectory;
    }

    /**
     * Simple implementation that is easy to write but can cause N+1 database queries.
     *
     * @param orders orders to enrich with customer data
     * @return summaries in the same order as the input
     * @throws IllegalArgumentException when {@code orders} is null or empty
     * @throws IllegalStateException when an order references a missing customer
     */
    public List<OrderSummary> buildSummariesWithRepeatedLookup(List<Order> orders) {
        validateOrders(orders);
        return orders.stream()
                .map(order -> {
                    Customer customer = customerDirectory.findById(order.customerId())
                            .orElseThrow(() -> new IllegalStateException("customer not found: " + order.customerId()));
                    return toSummary(order, customer);
                })
                .toList();
    }

    /**
     * Optimized implementation that loads all customers once and reuses the result map.
     *
     * @param orders orders to enrich with customer data
     * @return summaries in the same order as the input
     * @throws IllegalArgumentException when {@code orders} is null or empty
     * @throws IllegalStateException when an order references a missing customer
     */
    public List<OrderSummary> buildSummariesWithBatchLookup(List<Order> orders) {
        validateOrders(orders);
        Set<String> customerIds = new LinkedHashSet<>();
        for (Order order : orders) {
            customerIds.add(order.customerId());
        }

        Map<String, Customer> customersById = customerDirectory.findByIds(customerIds);
        return orders.stream()
                .map(order -> {
                    Customer customer = customersById.get(order.customerId());
                    if (customer == null) {
                        throw new IllegalStateException("customer not found: " + order.customerId());
                    }
                    return toSummary(order, customer);
                })
                .toList();
    }

    /**
     * Converts one joined order/customer pair into the DTO used by callers.
     */
    private static OrderSummary toSummary(Order order, Customer customer) {
        return new OrderSummary(
                order.id(),
                customer.displayName(),
                customer.segment(),
                order.totalAmount()
        );
    }

    /**
     * Validates input once at the public boundary.
     */
    private static void validateOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("orders must not be empty");
        }
    }
}
