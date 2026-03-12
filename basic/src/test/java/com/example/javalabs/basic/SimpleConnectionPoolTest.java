package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleConnectionPoolTest {

    @Test
    void reusesReleasedConnections() {
        SimpleConnectionPool<FakeDatabaseConnection> pool =
                new SimpleConnectionPool<>(2, id -> new FakeDatabaseConnection(id, "users-db"));

        int firstId;
        try (SimpleConnectionPool.PooledConnection<FakeDatabaseConnection> first = pool.borrow()) {
            firstId = first.value().id();
            assertEquals(1, pool.leasedCount());
        }

        try (SimpleConnectionPool.PooledConnection<FakeDatabaseConnection> second = pool.borrow()) {
            assertEquals(firstId, second.value().id());
            assertEquals(1, pool.createdCount());
        }
    }

    @Test
    void rejectsBorrowsAbovePoolSize() {
        SimpleConnectionPool<FakeDatabaseConnection> pool =
                new SimpleConnectionPool<>(1, id -> new FakeDatabaseConnection(id, "users-db"));

        SimpleConnectionPool.PooledConnection<FakeDatabaseConnection> first = pool.borrow();
        try {
            assertThrows(IllegalStateException.class, pool::borrow);
        } finally {
            first.close();
        }
    }
}
