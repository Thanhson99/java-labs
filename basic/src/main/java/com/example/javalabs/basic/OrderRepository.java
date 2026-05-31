package com.example.javalabs.basic;

import java.util.List;

/**
 * Repository abstraction for order reads.
 */
public interface OrderRepository {

    /**
     * Loads every order at once.
     *
     * @return immutable or defensive list of all orders
     */
    List<Order> findAll();

    /**
     * Loads one offset-based page of orders.
     *
     * @param request page request
     * @return page of orders and pagination metadata
     * @throws IllegalArgumentException when {@code request} is {@code null}
     */
    Page<Order> findPage(PageRequest request);
}
