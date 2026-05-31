package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a category index for repeated inventory lookups.
 *
 * <p>The indexed path pays a one-time construction cost so repeated category searches do not need
 * to scan every inventory item.</p>
 */
public final class IndexedInventoryCatalog {

    private final List<InventoryItem> allItems;
    private final Map<String, List<InventoryItem>> itemsByCategory = new HashMap<>();

    /**
     * Creates a catalog and precomputes a normalized category index.
     *
     * @param items source inventory items
     * @throws IllegalArgumentException when {@code items} is null or empty
     */
    public IndexedInventoryCatalog(List<InventoryItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
        this.allItems = List.copyOf(items);
        for (InventoryItem item : items) {
            // Normalize keys once so indexed lookups are case-insensitive and cheap.
            itemsByCategory.computeIfAbsent(normalizeCategory(item.category()), unused -> new ArrayList<>())
                    .add(item);
        }
        itemsByCategory.replaceAll((category, categoryItems) -> categoryItems.stream()
                .sorted(Comparator.comparing(InventoryItem::name))
                .toList());
    }

    /**
     * Lookup using a prebuilt category index.
     *
     * @param category category to find
     * @return matching items sorted by name
     * @throws IllegalArgumentException when {@code category} is blank
     */
    public List<InventoryItem> findByCategoryIndexed(String category) {
        return itemsByCategory.getOrDefault(normalizeCategory(category), List.of());
    }

    /**
     * Lookup by scanning every item. Useful as a baseline for comparison.
     *
     * @param category category to find
     * @return matching items sorted by name
     * @throws IllegalArgumentException when {@code category} is blank
     */
    public List<InventoryItem> findByCategoryScanning(String category) {
        String normalizedCategory = normalizeCategory(category);
        return allItems.stream()
                .filter(item -> normalizeCategory(item.category()).equals(normalizedCategory))
                .sorted(Comparator.comparing(InventoryItem::name))
                .toList();
    }

    /**
     * Returns the number of distinct normalized categories held in the index.
     *
     * @return indexed category count
     */
    public int indexedCategoryCount() {
        return itemsByCategory.size();
    }

    /**
     * Converts category text into the canonical index key.
     */
    private static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category must not be blank");
        }
        return category.trim().toLowerCase();
    }
}
