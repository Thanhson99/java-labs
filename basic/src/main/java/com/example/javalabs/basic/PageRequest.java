package com.example.javalabs.basic;

/**
 * Small pagination request used by repository examples.
 *
 * @param page zero-based page number
 * @param size maximum number of items in one page
 */
public record PageRequest(int page, int size) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when the page number or size is invalid
     */
    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("page must not be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
    }

    /**
     * @return zero-based offset represented by this request
     */
    public int offset() {
        return page * size;
    }

    /**
     * @return request for the following page with the same size
     */
    public PageRequest next() {
        return new PageRequest(page + 1, size);
    }
}
