package com.example.javalabs.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository that counts lookup calls so optimization behavior is visible in tests.
 */
public final class InMemoryCustomerDirectory implements CustomerDirectory {

    private final Map<String, Customer> customersById;
    private int singleLookupCount;
    private int batchLookupCount;

    /**
     * Creates a directory from an initial customer collection.
     *
     * @param customers customers to store by id
     * @throws IllegalArgumentException when {@code customers} is {@code null}, empty, or contains {@code null}
     */
    public InMemoryCustomerDirectory(Collection<Customer> customers) {
        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("customers must not be empty");
        }

        this.customersById = new HashMap<>();
        for (Customer customer : customers) {
            if (customer == null) {
                throw new IllegalArgumentException("customers must not contain null");
            }
            customersById.put(customer.id(), customer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        singleLookupCount++;
        return Optional.ofNullable(customersById.get(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Customer> findByIds(Collection<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("ids must not be null");
        }
        batchLookupCount++;
        Map<String, Customer> result = new HashMap<>();
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("ids must not contain blank values");
            }
            Customer customer = customersById.get(id);
            if (customer != null) {
                result.put(id, customer);
            }
        }
        return result;
    }

    /**
     * @return number of single-id lookup calls made to this fake repository
     */
    public int singleLookupCount() {
        return singleLookupCount;
    }

    /**
     * @return number of batch lookup calls made to this fake repository
     */
    public int batchLookupCount() {
        return batchLookupCount;
    }
}
