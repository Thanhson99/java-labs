package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryAnalyticsTest {

    private final List<InventoryItem> items = List.of(
            new InventoryItem("Keyboard", 12, "hardware"),
            new InventoryItem("Mouse", 5, "hardware"),
            new InventoryItem("Notebook", 20, "stationery"),
            new InventoryItem("Pen", 3, "stationery")
    );

    @Test
    void groupsQuantityByCategory() {
        assertEquals(Map.of("hardware", 17, "stationery", 23),
                InventoryAnalytics.totalQuantityByCategory(items));
    }

    @Test
    void sortsByQuantityDescending() {
        assertEquals("Notebook", InventoryAnalytics.sortByStockDescending(items).get(0).name());
    }

    @Test
    void findsLowStockItem() {
        assertEquals("Pen", InventoryAnalytics.firstLowStockItem(items, 3));
    }
}
