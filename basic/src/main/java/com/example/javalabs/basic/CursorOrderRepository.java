package com.example.javalabs.basic;

/**
 * Repository abstraction for cursor-based order reads.
 */
public interface CursorOrderRepository {

    /**
     * Loads the next ordered page after a cursor.
     *
     * @param request cursor request
     * @return page and optional cursor for the following read
     * @throws IllegalArgumentException when {@code request} is {@code null}
     */
    CursorPage<Order> findAfter(CursorPageRequest request);
}
