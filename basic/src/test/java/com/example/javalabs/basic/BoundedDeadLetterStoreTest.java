package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoundedDeadLetterStoreTest {

    @Test
    void keepsMessagesUntilCapacityIsReached() {
        BoundedDeadLetterStore store = new BoundedDeadLetterStore(3);

        store.save(message("m-1"));
        store.save(message("m-2"));

        assertEquals(2, store.size());
        assertEquals(0, store.droppedCount());
        assertEquals(List.of(message("m-1"), message("m-2")), store.findAll());
    }

    @Test
    void dropsOldestMessageWhenCapacityIsExceeded() {
        BoundedDeadLetterStore store = new BoundedDeadLetterStore(2);

        store.save(message("m-1"));
        store.save(message("m-2"));
        store.save(message("m-3"));

        assertEquals(2, store.size());
        assertEquals(1, store.droppedCount());
        assertEquals(List.of(message("m-2"), message("m-3")), store.findAll());
    }

    @Test
    void tracksMultipleDroppedMessages() {
        BoundedDeadLetterStore store = new BoundedDeadLetterStore(1);

        store.save(message("m-1"));
        store.save(message("m-2"));
        store.save(message("m-3"));

        assertEquals(List.of(message("m-3")), store.findAll());
        assertEquals(2, store.droppedCount());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new BoundedDeadLetterStore(0));

        BoundedDeadLetterStore store = new BoundedDeadLetterStore(1);
        assertThrows(IllegalArgumentException.class, () -> store.save(null));
    }

    private static DeadLetterMessage message(String key) {
        return new DeadLetterMessage(key, "payload-" + key, "failed");
    }
}
