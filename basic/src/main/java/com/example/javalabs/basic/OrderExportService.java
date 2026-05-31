package com.example.javalabs.basic;

import java.util.List;

/**
 * Demonstrates processing a large dataset all at once versus page by page.
 */
public final class OrderExportService {

    private final OrderRepository repository;

    /**
     * Creates an export service backed by an order repository.
     *
     * @param repository order repository used for all export reads
     * @throws IllegalArgumentException when {@code repository} is {@code null}
     */
    public OrderExportService(OrderRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        this.repository = repository;
    }

    /**
     * Simple strategy that loads every order into memory before processing.
     *
     * @return export report with aggregate values
     */
    public OrderExportReport exportByLoadingAll() {
        List<Order> orders = repository.findAll();
        return new OrderExportReport(orders.size(), totalAmount(orders), 1);
    }

    /**
     * Memory-friendlier strategy that processes one page at a time.
     *
     * @param pageSize number of orders to load per repository call
     * @return export report with aggregate values
     * @throws IllegalArgumentException when {@code pageSize} is not positive
     */
    public OrderExportReport exportInPages(int pageSize) {
        PageRequest request = new PageRequest(0, pageSize);
        int processedOrders = 0;
        int repositoryCalls = 0;
        double totalAmount = 0.0;

        while (true) {
            Page<Order> page = repository.findPage(request);
            repositoryCalls++;
            processedOrders += page.items().size();
            // Aggregate each page immediately so only one page must be retained at a time.
            totalAmount += totalAmount(page.items());

            if (!page.hasNext()) {
                break;
            }
            request = request.next();
        }

        return new OrderExportReport(processedOrders, totalAmount, repositoryCalls);
    }

    /**
     * Calculates aggregate revenue for a page or full order list.
     *
     * @param orders orders to aggregate
     * @return sum of order totals
     */
    private static double totalAmount(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::totalAmount)
                .sum();
    }
}
