package com.example.javalabs.basic;

import java.util.List;

/**
 * Result of loading customers while preserving the original request order.
 *
 * @param customers customers matching the requested ids
 * @param requestedCount number of ids requested by the caller
 * @param uniqueLookupCount number of unique ids sent to the repository
 */
public record CustomerLookupResult(List<Customer> customers, int requestedCount, int uniqueLookupCount) {

    public CustomerLookupResult {
        if (customers == null) {
            throw new IllegalArgumentException("customers must not be null");
        }
        if (requestedCount < 0) {
            throw new IllegalArgumentException("requestedCount must not be negative");
        }
        if (uniqueLookupCount < 0) {
            throw new IllegalArgumentException("uniqueLookupCount must not be negative");
        }
        customers = List.copyOf(customers);
    }
}
