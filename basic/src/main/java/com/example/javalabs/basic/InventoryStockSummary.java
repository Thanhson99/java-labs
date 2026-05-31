package com.example.javalabs.basic;

/**
 * Aggregate stock summary calculated in one pass.
 *
 * @param itemCount number of items processed
 * @param totalQuantity total quantity across all items
 * @param minQuantity smallest quantity
 * @param maxQuantity largest quantity
 */
public record InventoryStockSummary(
        int itemCount,
        int totalQuantity,
        int minQuantity,
        int maxQuantity) {

    /**
     * Calculates average stock quantity.
     *
     * @return average quantity, or {@code 0.0} when there are no items
     */
    public double averageQuantity() {
        if (itemCount == 0) {
            return 0.0;
        }
        return (double) totalQuantity / itemCount;
    }
}
