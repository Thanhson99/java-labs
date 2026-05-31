package com.example.javalabs.basic;

/**
 * Customer data used by the optimization examples.
 *
 * @param id unique customer identifier
 * @param displayName human-readable name
 * @param segment business grouping such as retail or enterprise
 */
public record Customer(String id, String displayName, String segment) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when id, display name, or segment is blank
     */
    public Customer {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        if (segment == null || segment.isBlank()) {
            throw new IllegalArgumentException("segment must not be blank");
        }
    }
}
