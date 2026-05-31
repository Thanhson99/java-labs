package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Demonstrates selecting top N inventory items without sorting the entire input list.
 */
public final class TopInventorySelector {

    private static final Comparator<InventoryItem> BEST_FIRST =
            Comparator.comparingInt(InventoryItem::quantity)
                    .reversed()
                    .thenComparing(InventoryItem::name);

    private static final Comparator<InventoryItem> WORST_FIRST =
            Comparator.comparingInt(InventoryItem::quantity)
                    .thenComparing(InventoryItem::name, Comparator.reverseOrder());

    /**
     * Utility class; instances are not needed because selection methods are static.
     */
    private TopInventorySelector() {
    }

    /**
     * Simple implementation that sorts every item before taking the first N.
     *
     * @param items items to inspect
     * @param limit maximum number of items to return
     * @return top items by quantity descending, then name ascending
     * @throws IllegalArgumentException when {@code items} is empty or {@code limit} is not positive
     */
    public static List<InventoryItem> topByFullSort(List<InventoryItem> items, int limit) {
        validate(items, limit);
        return items.stream()
                .sorted(BEST_FIRST)
                .limit(limit)
                .toList();
    }

    /**
     * Heap-based implementation that keeps only N candidates while scanning the list.
     *
     * @param items items to inspect
     * @param limit maximum number of items to return
     * @return top items by quantity descending, then name ascending
     * @throws IllegalArgumentException when {@code items} is empty or {@code limit} is not positive
     */
    public static List<InventoryItem> topByBoundedHeap(List<InventoryItem> items, int limit) {
        validate(items, limit);
        PriorityQueue<InventoryItem> heap = new PriorityQueue<>(WORST_FIRST);

        for (InventoryItem item : items) {
            if (heap.size() < limit) {
                heap.add(item);
            } else if (BEST_FIRST.compare(item, heap.peek()) < 0) {
                // Replace the current worst candidate only when the new item ranks better.
                heap.poll();
                heap.add(item);
            }
        }

        List<InventoryItem> result = new ArrayList<>(heap);
        // Heap order is internal, so sort the final bounded result for deterministic output.
        result.sort(BEST_FIRST);
        return result;
    }

    /**
     * Validates shared top-N inputs.
     *
     * @param items inventory items
     * @param limit requested top-N size
     * @throws IllegalArgumentException when inputs are invalid
     */
    private static void validate(List<InventoryItem> items, int limit) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
    }
}
