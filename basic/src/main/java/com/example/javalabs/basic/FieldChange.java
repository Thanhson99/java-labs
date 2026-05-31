package com.example.javalabs.basic;

/**
 * Describes one changed field between two snapshots.
 *
 * @param fieldName name of the changed field
 * @param oldValue value before the change
 * @param newValue value after the change
 */
public record FieldChange(String fieldName, String oldValue, String newValue) {

    /**
     * Validates and normalizes the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when {@code fieldName} is blank
     */
    public FieldChange {
        if (fieldName == null || fieldName.isBlank()) {
            throw new IllegalArgumentException("fieldName must not be blank");
        }
        // Null values are normalized to empty text so event payloads stay simple to render.
        oldValue = oldValue == null ? "" : oldValue;
        newValue = newValue == null ? "" : newValue;
    }
}
