package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexedInventoryCatalogTest {

    private final List<InventoryItem> items = List.of(
            new InventoryItem("Keyboard", 12, "hardware"),
            new InventoryItem("Mouse", 5, "hardware"),
            new InventoryItem("Notebook", 20, "stationery"),
            new InventoryItem("Pen", 3, "stationery"),
            new InventoryItem("Monitor", 20, "hardware")
    );

    @Test
    void indexedLookupReturnsSameItemsAsScanningLookup() {
        IndexedInventoryCatalog catalog = new IndexedInventoryCatalog(items);

        List<InventoryItem> indexed = catalog.findByCategoryIndexed("hardware");
        List<InventoryItem> scanning = catalog.findByCategoryScanning("hardware");

        assertEquals(scanning, indexed);
        assertEquals(List.of("Keyboard", "Monitor", "Mouse"),
                indexed.stream().map(InventoryItem::name).toList());
    }

    @Test
    void categoryLookupIsCaseInsensitiveAndTrimmed() {
        IndexedInventoryCatalog catalog = new IndexedInventoryCatalog(items);

        assertEquals(catalog.findByCategoryIndexed("hardware"),
                catalog.findByCategoryIndexed(" HARDWARE "));
    }

    @Test
    void unknownCategoryReturnsEmptyList() {
        IndexedInventoryCatalog catalog = new IndexedInventoryCatalog(items);

        assertEquals(List.of(), catalog.findByCategoryIndexed("books"));
        assertEquals(2, catalog.indexedCategoryCount());
    }

    @Test
    void rejectsBlankCategory() {
        IndexedInventoryCatalog catalog = new IndexedInventoryCatalog(items);

        assertThrows(IllegalArgumentException.class, () -> catalog.findByCategoryIndexed(" "));
    }
}
