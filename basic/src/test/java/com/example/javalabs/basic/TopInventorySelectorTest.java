package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TopInventorySelectorTest {

    private final List<InventoryItem> items = List.of(
            new InventoryItem("Keyboard", 12, "hardware"),
            new InventoryItem("Mouse", 5, "hardware"),
            new InventoryItem("Notebook", 20, "stationery"),
            new InventoryItem("Pen", 3, "stationery"),
            new InventoryItem("Monitor", 20, "hardware"),
            new InventoryItem("Cable", 8, "hardware")
    );

    @Test
    void boundedHeapReturnsSameTopItemsAsFullSort() {
        List<InventoryItem> fullSort = TopInventorySelector.topByFullSort(items, 3);
        List<InventoryItem> boundedHeap = TopInventorySelector.topByBoundedHeap(items, 3);

        assertEquals(fullSort, boundedHeap);
        assertEquals(List.of("Monitor", "Notebook", "Keyboard"),
                boundedHeap.stream().map(InventoryItem::name).toList());
    }

    @Test
    void limitLargerThanInputReturnsAllItemsSorted() {
        List<InventoryItem> result = TopInventorySelector.topByBoundedHeap(items, 20);

        assertEquals(items.size(), result.size());
        assertEquals("Monitor", result.get(0).name());
        assertEquals("Pen", result.get(result.size() - 1).name());
    }

    @Test
    void rejectsInvalidLimit() {
        assertThrows(IllegalArgumentException.class,
                () -> TopInventorySelector.topByBoundedHeap(items, 0));
    }
}
