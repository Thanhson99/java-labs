package com.example.javalabs.basic;

import java.util.List;

/**
 * Calculates stock statistics without building intermediate grouped collections.
 */
public final class InventoryStockSummarizer {

    /**
     * Utility class; instances are not needed because summarization methods are static.
     */
    private InventoryStockSummarizer() {
    }

    /**
     * Calculates count, total, min, max, and average-ready data in a single pass.
     *
     * @param items inventory items to summarize
     * @return stock summary
     * @throws IllegalArgumentException when {@code items} is {@code null} or empty
     */
    public static InventoryStockSummary summarizeOnePass(List<InventoryItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }

        int count = 0;
        int total = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (InventoryItem item : items) {
            // All counters are updated during one scan; no grouping maps or intermediate lists are needed.
            int quantity = item.quantity();
            count++;
            total += quantity;
            min = Math.min(min, quantity);
            max = Math.max(max, quantity);
        }

        return new InventoryStockSummary(count, total, min, max);
    }
}
