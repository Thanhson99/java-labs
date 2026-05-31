package com.example.javalabs.basic;

import java.util.Optional;

/**
 * Cursor-based page request.
 *
 * @param lastSeenId item id from the previous page, or empty for the first page
 * @param size maximum number of items in one page
 */
public record CursorPageRequest(Optional<String> lastSeenId, int size) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when cursor state or size is invalid
     */
    public CursorPageRequest {
        if (lastSeenId == null) {
            throw new IllegalArgumentException("lastSeenId must not be null");
        }
        if (lastSeenId.isPresent() && lastSeenId.orElseThrow().isBlank()) {
            throw new IllegalArgumentException("lastSeenId must not be blank");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
    }

    /**
     * Creates the first cursor request.
     *
     * @param size requested page size
     * @return cursor request without a previous id
     */
    public static CursorPageRequest first(int size) {
        return new CursorPageRequest(Optional.empty(), size);
    }
}
