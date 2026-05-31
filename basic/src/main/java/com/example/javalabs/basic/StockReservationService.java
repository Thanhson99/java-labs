package com.example.javalabs.basic;

/**
 * Service that reserves inventory using optimistic locking.
 *
 * <p>The service reads the current item version, calculates the new quantity, and saves with the
 * expected version. If another writer changed the same row first, the repository rejects the stale
 * write with an optimistic-lock error.</p>
 */
public final class StockReservationService {

    private final VersionedInventoryRepository repository;

    /**
     * Creates a reservation service.
     *
     * @param repository version-aware inventory repository
     * @throws IllegalArgumentException when {@code repository} is {@code null}
     */
    public StockReservationService(VersionedInventoryRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        this.repository = repository;
    }

    /**
     * Reserves stock for one SKU.
     *
     * @param sku stock keeping unit
     * @param quantity quantity to reserve
     * @return saved inventory item with incremented version
     * @throws IllegalArgumentException when inputs are invalid
     * @throws IllegalStateException when the item does not exist or stock is insufficient
     * @throws OptimisticLockException when the save detects a stale version
     */
    public VersionedInventoryItem reserve(String sku, int quantity) {
        VersionedInventoryItem current = repository.findBySku(sku)
                .orElseThrow(() -> new IllegalStateException("item not found: " + sku));
        VersionedInventoryItem updated = current.reserve(quantity);
        // Save with the version we originally read so concurrent writes cannot be overwritten silently.
        return repository.save(updated, current.version());
    }
}
