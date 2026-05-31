package com.example.javalabs.basic;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Abstraction for reading customer data.
 *
 * <p>The two methods intentionally model a common performance tradeoff. Calling
 * {@link #findById(String)} inside a loop is easy to write, but can become an N+1 query problem.
 * Calling {@link #findByIds(Collection)} batches the lookup so the service does one data access
 * operation for many orders.</p>
 */
public interface CustomerDirectory {

    /**
     * Finds one customer by id.
     *
     * @param id customer identifier
     * @return customer when present
     * @throws IllegalArgumentException when {@code id} is blank
     */
    Optional<Customer> findById(String id);

    /**
     * Finds many customers in one data-access call.
     *
     * @param ids customer identifiers to load
     * @return customers keyed by id; missing ids are omitted
     * @throws IllegalArgumentException when {@code ids} is {@code null} or contains blank values
     */
    Map<String, Customer> findByIds(Collection<String> ids);
}
