package com.example.javalabs.basic;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Demonstrates map building, grouping, and sorting operations.
 */
public final class InventoryAnalytics {

    private InventoryAnalytics() {
    }

    /**
     * Groups total quantity by category.
     *
     * @param items the items to aggregate
     * @return a map of category to total quantity
     */
    public static Map<String, Integer> totalQuantityByCategory(List<InventoryItem> items) {
        validateItems(items);
        return items.stream()
                .collect(Collectors.groupingBy(
                        InventoryItem::category,
                        Collectors.summingInt(InventoryItem::quantity)
                ));
    }

    /**
     * Sorts items by quantity descending and then by name ascending.
     *
     * @param items the items to sort
     * @return a sorted copy of the input list
     */
    public static List<InventoryItem> sortByStockDescending(List<InventoryItem> items) {
        validateItems(items);
        return items.stream()
                .sorted(Comparator.comparingInt(InventoryItem::quantity)
                        .reversed()
                        .thenComparing(InventoryItem::name))
                .toList();
    }

    /**
     * Finds the first low-stock item using a threshold.
     *
     * @param items the items to inspect
     * @param threshold the maximum quantity considered low stock
     * @return the first matching item name, or {@code "none"} when absent
     */
    public static String firstLowStockItem(List<InventoryItem> items, int threshold) {
        validateItems(items);
        return items.stream()
                .filter(item -> item.quantity() <= threshold)
                .map(InventoryItem::name)
                .sorted()
                .findFirst()
                .orElse("none");
    }

    private static void validateItems(List<InventoryItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
    }
}
