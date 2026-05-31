package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Preloads important customer ids into a cache-backed directory.
 *
 * <p>Warmup trades some startup or scheduled work for lower latency on the first real requests.
 * Duplicate ids are removed while preserving order so the source is not hit unnecessarily.</p>
 */
public final class CacheWarmupService {

    private final CustomerDirectory customerDirectory;

    /**
     * Creates a warmup service around a cache-backed directory.
     *
     * @param customerDirectory directory to read during warmup
     * @throws IllegalArgumentException when {@code customerDirectory} is {@code null}
     */
    public CacheWarmupService(CustomerDirectory customerDirectory) {
        if (customerDirectory == null) {
            throw new IllegalArgumentException("customerDirectory must not be null");
        }
        this.customerDirectory = customerDirectory;
    }

    /**
     * Loads unique customer ids so a cache can be primed before real traffic arrives.
     *
     * @param customerIds requested ids, possibly containing duplicates
     * @return warmup report with requested, loaded, and missing ids
     * @throws IllegalArgumentException when {@code customerIds} is {@code null} or contains blank ids
     */
    public CacheWarmupReport warmup(List<String> customerIds) {
        if (customerIds == null) {
            throw new IllegalArgumentException("customerIds must not be null");
        }

        Set<String> uniqueIds = new LinkedHashSet<>();
        for (String customerId : customerIds) {
            if (customerId == null || customerId.isBlank()) {
                throw new IllegalArgumentException("customerIds must not contain blank ids");
            }
            // LinkedHashSet removes duplicates without changing first-seen order.
            uniqueIds.add(customerId);
        }

        List<String> loadedIds = new ArrayList<>();
        List<String> missingIds = new ArrayList<>();
        for (String customerId : uniqueIds) {
            Optional<Customer> customer = customerDirectory.findById(customerId);
            if (customer.isPresent()) {
                loadedIds.add(customerId);
            } else {
                missingIds.add(customerId);
            }
        }

        return new CacheWarmupReport(new ArrayList<>(uniqueIds), loadedIds, missingIds);
    }
}
