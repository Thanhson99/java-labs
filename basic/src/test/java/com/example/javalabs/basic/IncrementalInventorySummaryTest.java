package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncrementalInventorySummaryTest {

    @Test
    void updatesSummaryWhenItemsAreAdded() {
        IncrementalInventorySummary summary = new IncrementalInventorySummary();

        summary.addOrUpdate(new InventoryItem("Keyboard", 12, "hardware"));
        summary.addOrUpdate(new InventoryItem("Mouse", 5, "hardware"));
        summary.addOrUpdate(new InventoryItem("Notebook", 20, "stationery"));

        assertEquals(new InventoryStockSummary(3, 37, 5, 20), summary.snapshot());
        assertEquals(37.0 / 3.0, summary.snapshot().averageQuantity());
    }

    @Test
    void updatingExistingItemAdjustsTotalsWithoutIncreasingCount() {
        IncrementalInventorySummary summary = new IncrementalInventorySummary();

        summary.addOrUpdate(new InventoryItem("Keyboard", 12, "hardware"));
        summary.addOrUpdate(new InventoryItem("Mouse", 5, "hardware"));
        summary.addOrUpdate(new InventoryItem("Keyboard", 30, "hardware"));

        assertEquals(new InventoryStockSummary(2, 35, 5, 30), summary.snapshot());
    }

    @Test
    void removingItemUpdatesMinAndMax() {
        IncrementalInventorySummary summary = new IncrementalInventorySummary();
        summary.addOrUpdate(new InventoryItem("Keyboard", 12, "hardware"));
        summary.addOrUpdate(new InventoryItem("Mouse", 5, "hardware"));
        summary.addOrUpdate(new InventoryItem("Notebook", 20, "stationery"));

        assertTrue(summary.remove("Mouse"));
        assertEquals(new InventoryStockSummary(2, 32, 12, 20), summary.snapshot());

        assertTrue(summary.remove("Notebook"));
        assertEquals(new InventoryStockSummary(1, 12, 12, 12), summary.snapshot());
    }

    @Test
    void removingUnknownItemDoesNotChangeSummary() {
        IncrementalInventorySummary summary = new IncrementalInventorySummary();
        summary.addOrUpdate(new InventoryItem("Keyboard", 12, "hardware"));

        assertFalse(summary.remove("missing"));
        assertEquals(new InventoryStockSummary(1, 12, 12, 12), summary.snapshot());
    }

    @Test
    void emptySummaryUsesZeroValues() {
        assertEquals(new InventoryStockSummary(0, 0, 0, 0), new IncrementalInventorySummary().snapshot());
    }
}
