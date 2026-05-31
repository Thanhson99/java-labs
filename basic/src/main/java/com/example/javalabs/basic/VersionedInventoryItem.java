package com.example.javalabs.basic;

/**
 * Inventory item with a version field for optimistic locking.
 *
 * @param sku stock keeping unit
 * @param quantity available quantity
 * @param version version increased after every successful write
 */
public record VersionedInventoryItem(String sku, int quantity, int version) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when SKU is blank or numeric values are negative
     */
    public VersionedInventoryItem {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative");
        }
    }

    /**
     * Creates a copy with reduced quantity while preserving the current version.
     *
     * @param requestedQuantity quantity to reserve
     * @return updated item with reduced stock and unchanged version
     * @throws IllegalArgumentException when {@code requestedQuantity} is not positive
     * @throws IllegalStateException when requested quantity exceeds available stock
     */
    public VersionedInventoryItem reserve(int requestedQuantity) {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("requestedQuantity must be positive");
        }
        if (requestedQuantity > quantity) {
            throw new IllegalStateException("not enough stock");
        }
        return new VersionedInventoryItem(sku, quantity - requestedQuantity, version);
    }
}
