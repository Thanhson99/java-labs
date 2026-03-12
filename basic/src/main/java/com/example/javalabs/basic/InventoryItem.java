package com.example.javalabs.basic;

/**
 * Small immutable model used in map and sorting examples.
 *
 * @param name the item name
 * @param quantity the amount in stock
 * @param category the item's category
 */
public record InventoryItem(String name, int quantity, String category) {

    public InventoryItem {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category must not be blank");
        }
    }
}
