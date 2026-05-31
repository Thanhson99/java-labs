package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demonstrates de-duplicating repeated ids before a batch repository call.
 */
public final class CustomerLookupService {

    private final CustomerDirectory customerDirectory;

    /**
     * Creates a lookup service using a batch-capable customer directory.
     *
     * @param customerDirectory directory used to load customers
     * @throws IllegalArgumentException when {@code customerDirectory} is {@code null}
     */
    public CustomerLookupService(CustomerDirectory customerDirectory) {
        if (customerDirectory == null) {
            throw new IllegalArgumentException("customerDirectory must not be null");
        }
        this.customerDirectory = customerDirectory;
    }

    /**
     * Loads customers once per unique id, then returns them in the caller's original order.
     *
     * @param customerIds ids that may contain duplicates
     * @return lookup result with customers and lookup counts
     * @throws IllegalArgumentException when {@code customerIds} is empty or contains blank ids
     * @throws IllegalStateException when a requested customer cannot be found
     */
    public CustomerLookupResult loadCustomersPreservingOrder(List<String> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            throw new IllegalArgumentException("customerIds must not be empty");
        }

        Set<String> uniqueIds = new LinkedHashSet<>();
        for (String customerId : customerIds) {
            if (customerId == null || customerId.isBlank()) {
                throw new IllegalArgumentException("customerId must not be blank");
            }
            uniqueIds.add(customerId);
        }

        // One batch call prevents the N+1 lookup pattern while keeping the original response order below.
        Map<String, Customer> customersById = customerDirectory.findByIds(uniqueIds);
        List<Customer> orderedCustomers = new ArrayList<>();
        for (String customerId : customerIds) {
            Customer customer = customersById.get(customerId);
            if (customer == null) {
                throw new IllegalStateException("customer not found: " + customerId);
            }
            orderedCustomers.add(customer);
        }

        return new CustomerLookupResult(orderedCustomers, customerIds.size(), uniqueIds.size());
    }
}
