package com.example.javalabs.basic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Minimal connection pool that demonstrates reuse and borrowing limits.
 *
 * @param <T> the connection type managed by the pool
 */
public final class SimpleConnectionPool<T> {

    private final int maxSize;
    private final IntFunction<T> connectionFactory;
    private final Deque<T> availableConnections = new ArrayDeque<>();
    private int createdCount;
    private int leasedCount;

    /**
     * Creates a pool that lazily builds connections up to a configured limit.
     *
     * @param maxSize maximum number of connections that may exist at once
     * @param connectionFactory factory that creates a connection for a new numeric identifier
     */
    public SimpleConnectionPool(int maxSize, IntFunction<T> connectionFactory) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        this.maxSize = maxSize;
        this.connectionFactory = Objects.requireNonNull(connectionFactory, "connectionFactory");
    }

    /**
     * Borrows a connection from the pool.
     *
     * @return a pooled handle that must be closed when work is complete
     */
    public PooledConnection<T> borrow() {
        T connection;
        if (!availableConnections.isEmpty()) {
            connection = availableConnections.removeFirst();
        } else if (createdCount < maxSize) {
            createdCount++;
            connection = connectionFactory.apply(createdCount);
        } else {
            throw new IllegalStateException("no connections available in the pool");
        }

        leasedCount++;
        return new PooledConnection<>(connection, this);
    }

    int createdCount() {
        return createdCount;
    }

    int leasedCount() {
        return leasedCount;
    }

    int availableCount() {
        return availableConnections.size();
    }

    private void release(T connection) {
        leasedCount--;
        availableConnections.addLast(connection);
    }

    /**
     * Wrapper that ensures returning the connection to the pool is explicit and visible.
     *
     * @param <T> the underlying connection type
     */
    public static final class PooledConnection<T> implements AutoCloseable {
        private final T value;
        private final SimpleConnectionPool<T> owner;
        private boolean closed;

        private PooledConnection(T value, SimpleConnectionPool<T> owner) {
            this.value = value;
            this.owner = owner;
        }

        public T value() {
            return value;
        }

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                owner.release(value);
            }
        }
    }
}
