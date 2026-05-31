package com.example.javalabs.basic;

import java.util.List;

/**
 * Page of data returned by a repository.
 *
 * @param items current page items
 * @param page current zero-based page number
 * @param size requested page size
 * @param totalItems total available items
 * @param <T> item type
 */
public record Page<T>(List<T> items, int page, int size, int totalItems) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when page metadata is invalid
     */
    public Page {
        if (items == null) {
            throw new IllegalArgumentException("items must not be null");
        }
        if (page < 0) {
            throw new IllegalArgumentException("page must not be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
        if (totalItems < 0) {
            throw new IllegalArgumentException("totalItems must not be negative");
        }
        items = List.copyOf(items);
    }

    /**
     * @return {@code true} when another offset-based page exists after this one
     */
    public boolean hasNext() {
        return (page + 1) * size < totalItems;
    }
}
