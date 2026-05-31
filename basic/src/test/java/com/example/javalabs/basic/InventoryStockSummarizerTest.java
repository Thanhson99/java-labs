package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryStockSummarizerTest {

    @Test
    void summarizesStockInOnePass() {
        List<InventoryItem> items = List.of(
                new InventoryItem("Keyboard", 12, "hardware"),
                new InventoryItem("Mouse", 5, "hardware"),
                new InventoryItem("Notebook", 20, "stationery"),
                new InventoryItem("Pen", 3, "stationery")
        );

        InventoryStockSummary summary = InventoryStockSummarizer.summarizeOnePass(items);

        assertEquals(4, summary.itemCount());
        assertEquals(40, summary.totalQuantity());
        assertEquals(3, summary.minQuantity());
        assertEquals(20, summary.maxQuantity());
        assertEquals(10.0, summary.averageQuantity());
    }

    @Test
    void rejectsEmptyInput() {
        assertThrows(IllegalArgumentException.class,
                () -> InventoryStockSummarizer.summarizeOnePass(List.of()));
    }
}
