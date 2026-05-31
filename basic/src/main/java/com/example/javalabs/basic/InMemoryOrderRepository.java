package com.example.javalabs.basic;

import java.util.List;

/**
 * In-memory order repository that tracks whether callers load all data or page through it.
 */
public final class InMemoryOrderRepository implements OrderRepository {

    private final List<Order> orders;
    private int findAllCount;
    private int findPageCount;

    /**
     * Creates an in-memory repository from a fixed order snapshot.
     *
     * @param orders orders available to repository methods
     * @throws IllegalArgumentException when {@code orders} is {@code null} or contains {@code null}
     */
    public InMemoryOrderRepository(List<Order> orders) {
        if (orders == null) {
            throw new IllegalArgumentException("orders must not be null");
        }
        if (orders.stream().anyMatch(order -> order == null)) {
            throw new IllegalArgumentException("orders must not contain null");
        }
        this.orders = List.copyOf(orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Order> findAll() {
        findAllCount++;
        return orders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Order> findPage(PageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        findPageCount++;
        int fromIndex = Math.min(request.offset(), orders.size());
        int toIndex = Math.min(fromIndex + request.size(), orders.size());
        return new Page<>(orders.subList(fromIndex, toIndex), request.page(), request.size(), orders.size());
    }

    /**
     * @return number of full-list reads made by callers
     */
    public int findAllCount() {
        return findAllCount;
    }

    /**
     * @return number of page reads made by callers
     */
    public int findPageCount() {
        return findPageCount;
    }
}
