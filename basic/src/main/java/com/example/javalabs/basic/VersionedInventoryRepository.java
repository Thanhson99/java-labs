package com.example.javalabs.basic;

import java.util.Optional;

/**
 * Repository for inventory rows protected by optimistic locking.
 */
public interface VersionedInventoryRepository {

    /**
     * Finds one inventory item by SKU.
     *
     * @param sku stock keeping unit
     * @return item when present
     * @throws IllegalArgumentException when {@code sku} is blank
     */
    Optional<VersionedInventoryItem> findBySku(String sku);

    /**
     * Saves an item only when the stored version matches the expected version.
     *
     * @param item item to save
     * @param expectedVersion version read before computing the update
     * @return saved item with incremented version
     * @throws IllegalArgumentException when inputs are invalid
     * @throws IllegalStateException when the item does not exist
     * @throws OptimisticLockException when versions do not match
     */
    VersionedInventoryItem save(VersionedInventoryItem item, int expectedVersion);
}
