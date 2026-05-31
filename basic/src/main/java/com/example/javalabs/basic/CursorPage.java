package com.example.javalabs.basic;

import java.util.List;
import java.util.Optional;

/**
 * Page returned by cursor-based reads.
 *
 * @param items current page items
 * @param nextCursor id to pass into the next request, or empty when there is no next page
 * @param <T> item type
 */
public record CursorPage<T>(List<T> items, Optional<String> nextCursor) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when items or cursor container is {@code null}
     */
    public CursorPage {
        if (items == null) {
            throw new IllegalArgumentException("items must not be null");
        }
        if (nextCursor == null) {
            throw new IllegalArgumentException("nextCursor must not be null");
        }
        items = List.copyOf(items);
    }

    /**
     * @return {@code true} when another cursor page can be requested
     */
    public boolean hasNext() {
        return nextCursor.isPresent();
    }
}
