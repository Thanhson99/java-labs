package com.example.javalabs.basic;

/**
 * Demonstrates cursor-based processing for large ordered datasets.
 */
public final class CursorOrderExportService {

    private final CursorOrderRepository repository;

    /**
     * Creates a cursor export service backed by a cursor-capable repository.
     *
     * @param repository repository used to read ordered pages
     * @throws IllegalArgumentException when {@code repository} is {@code null}
     */
    public CursorOrderExportService(CursorOrderRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        this.repository = repository;
    }

    /**
     * Exports all orders by following cursor pages.
     *
     * @param pageSize maximum page size for each repository call
     * @return aggregate export report
     * @throws IllegalArgumentException when {@code pageSize} is not positive
     */
    public OrderExportReport exportWithCursor(int pageSize) {
        CursorPageRequest request = CursorPageRequest.first(pageSize);
        int processedOrders = 0;
        int repositoryCalls = 0;
        double totalAmount = 0.0;

        while (true) {
            CursorPage<Order> page = repository.findAfter(request);
            repositoryCalls++;
            processedOrders += page.items().size();
            totalAmount += page.items().stream()
                    .mapToDouble(Order::totalAmount)
                    .sum();

            if (!page.hasNext()) {
                break;
            }
            // The next request starts after the last id returned by the current page.
            request = new CursorPageRequest(page.nextCursor(), pageSize);
        }

        return new OrderExportReport(processedOrders, totalAmount, repositoryCalls);
    }
}
