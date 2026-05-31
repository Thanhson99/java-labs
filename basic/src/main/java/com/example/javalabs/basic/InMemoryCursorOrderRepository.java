package com.example.javalabs.basic;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * In-memory cursor repository that keeps orders sorted by id.
 */
public final class InMemoryCursorOrderRepository implements CursorOrderRepository {

    private final List<Order> orders;
    private int findAfterCount;

    /**
     * Creates a cursor repository sorted by order id.
     *
     * @param orders orders to expose through cursor pages
     * @throws IllegalArgumentException when {@code orders} is {@code null} or contains {@code null}
     */
    public InMemoryCursorOrderRepository(List<Order> orders) {
        if (orders == null) {
            throw new IllegalArgumentException("orders must not be null");
        }
        if (orders.stream().anyMatch(order -> order == null)) {
            throw new IllegalArgumentException("orders must not contain null");
        }
        this.orders = orders.stream()
                .sorted(Comparator.comparing(Order::id))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CursorPage<Order> findAfter(CursorPageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        findAfterCount++;
        int startIndex = request.lastSeenId()
                .map(this::indexAfter)
                .orElse(0);
        int toIndex = Math.min(startIndex + request.size(), orders.size());
        List<Order> pageItems = orders.subList(startIndex, toIndex);
        // The cursor is the last id returned, which makes the next page stable even when page size changes.
        Optional<String> nextCursor = toIndex < orders.size() && !pageItems.isEmpty()
                ? Optional.of(pageItems.get(pageItems.size() - 1).id())
                : Optional.empty();
        return new CursorPage<>(pageItems, nextCursor);
    }

    /**
     * @return number of cursor reads made by callers
     */
    public int findAfterCount() {
        return findAfterCount;
    }

    /**
     * Finds the first sorted order index strictly after a cursor id.
     *
     * @param lastSeenId cursor id from the previous page
     * @return index to start the next page
     */
    private int indexAfter(String lastSeenId) {
        for (int index = 0; index < orders.size(); index++) {
            if (orders.get(index).id().compareTo(lastSeenId) > 0) {
                return index;
            }
        }
        return orders.size();
    }
}
