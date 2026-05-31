package com.example.javalabs.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository that demonstrates version checks before writes.
 */
public final class InMemoryVersionedInventoryRepository implements VersionedInventoryRepository {

    private final Map<String, VersionedInventoryItem> itemsBySku = new HashMap<>();

    /**
     * Creates an in-memory versioned repository.
     *
     * @param items initial items to store
     * @throws IllegalArgumentException when {@code items} is {@code null} or contains {@code null}
     */
    public InMemoryVersionedInventoryRepository(Iterable<VersionedInventoryItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("items must not be null");
        }
        for (VersionedInventoryItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException("items must not contain null");
            }
            itemsBySku.put(item.sku(), item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<VersionedInventoryItem> findBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        return Optional.ofNullable(itemsBySku.get(sku));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VersionedInventoryItem save(VersionedInventoryItem item, int expectedVersion) {
        if (item == null) {
            throw new IllegalArgumentException("item must not be null");
        }
        if (expectedVersion < 0) {
            throw new IllegalArgumentException("expectedVersion must not be negative");
        }
        VersionedInventoryItem current = itemsBySku.get(item.sku());
        if (current == null) {
            throw new IllegalStateException("item not found: " + item.sku());
        }
        if (current.version() != expectedVersion) {
            throw new OptimisticLockException(
                    "version conflict for " + item.sku() + ": expected "
                            + expectedVersion + " but was " + current.version()
            );
        }

        // The repository, not the service, owns version increments after a successful write.
        VersionedInventoryItem saved =
                new VersionedInventoryItem(item.sku(), item.quantity(), current.version() + 1);
        itemsBySku.put(saved.sku(), saved);
        return saved;
    }
}
