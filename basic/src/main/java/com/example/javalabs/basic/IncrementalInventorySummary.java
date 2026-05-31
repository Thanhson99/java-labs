package com.example.javalabs.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Maintains inventory stock summary incrementally as items are added, updated, or removed.
 *
 * <p>This avoids recalculating totals and min/max values by scanning the whole inventory after
 * every change. The class keeps small indexes that can be updated in logarithmic time.</p>
 */
public final class IncrementalInventorySummary {

    private final Map<String, InventoryItem> itemsByName = new HashMap<>();
    private final TreeMap<Integer, Integer> quantityCounts = new TreeMap<>();
    private int totalQuantity;

    /**
     * Adds a new item or replaces an existing item with the same name.
     *
     * @param item inventory item to store
     * @throws NullPointerException when {@code item} is {@code null}
     */
    public void addOrUpdate(InventoryItem item) {
        InventoryItem previous = itemsByName.put(item.name(), item);
        if (previous != null) {
            // Remove the old quantity before adding the replacement so min/max stay correct.
            removeQuantity(previous.quantity());
            totalQuantity -= previous.quantity();
        }

        addQuantity(item.quantity());
        totalQuantity += item.quantity();
    }

    /**
     * Removes an item from the summary by name.
     *
     * @param itemName item name to remove
     * @return true when an item existed and was removed
     * @throws IllegalArgumentException when {@code itemName} is blank
     */
    public boolean remove(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("itemName must not be blank");
        }

        InventoryItem removed = itemsByName.remove(itemName);
        if (removed == null) {
            return false;
        }

        removeQuantity(removed.quantity());
        totalQuantity -= removed.quantity();
        return true;
    }

    /**
     * Builds an immutable summary from the current incremental state.
     *
     * @return stock summary containing item count, total quantity, minimum stock, and maximum stock
     */
    public InventoryStockSummary snapshot() {
        if (itemsByName.isEmpty()) {
            return new InventoryStockSummary(0, 0, 0, 0);
        }

        return new InventoryStockSummary(
                itemsByName.size(),
                totalQuantity,
                quantityCounts.firstKey(),
                quantityCounts.lastKey()
        );
    }

    private void addQuantity(int quantity) {
        quantityCounts.merge(quantity, 1, Integer::sum);
    }

    /**
     * Removes one occurrence of a quantity from the min/max index.
     */
    private void removeQuantity(int quantity) {
        int count = quantityCounts.getOrDefault(quantity, 0);
        if (count <= 1) {
            quantityCounts.remove(quantity);
        } else {
            quantityCounts.put(quantity, count - 1);
        }
    }
}
