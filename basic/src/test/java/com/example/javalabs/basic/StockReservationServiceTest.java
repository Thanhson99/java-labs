package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockReservationServiceTest {

    @Test
    void reservesStockAndIncrementsVersion() {
        InMemoryVersionedInventoryRepository repository = new InMemoryVersionedInventoryRepository(List.of(
                new VersionedInventoryItem("SKU-1", 10, 0)
        ));
        StockReservationService service = new StockReservationService(repository);

        VersionedInventoryItem saved = service.reserve("SKU-1", 3);

        assertEquals(new VersionedInventoryItem("SKU-1", 7, 1), saved);
        assertEquals(saved, repository.findBySku("SKU-1").orElseThrow());
    }

    @Test
    void rejectsWriteBasedOnStaleVersion() {
        InMemoryVersionedInventoryRepository repository = new InMemoryVersionedInventoryRepository(List.of(
                new VersionedInventoryItem("SKU-1", 10, 0)
        ));

        VersionedInventoryItem firstRead = repository.findBySku("SKU-1").orElseThrow();
        VersionedInventoryItem secondRead = repository.findBySku("SKU-1").orElseThrow();

        repository.save(firstRead.reserve(2), firstRead.version());

        assertThrows(OptimisticLockException.class,
                () -> repository.save(secondRead.reserve(3), secondRead.version()));
        assertEquals(new VersionedInventoryItem("SKU-1", 8, 1),
                repository.findBySku("SKU-1").orElseThrow());
    }

    @Test
    void rejectsReservationWhenStockIsNotEnough() {
        InMemoryVersionedInventoryRepository repository = new InMemoryVersionedInventoryRepository(List.of(
                new VersionedInventoryItem("SKU-1", 2, 0)
        ));
        StockReservationService service = new StockReservationService(repository);

        assertThrows(IllegalStateException.class, () -> service.reserve("SKU-1", 3));
    }
}
