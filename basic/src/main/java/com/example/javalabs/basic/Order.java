package com.example.javalabs.basic;

/**
 * Order data used by the optimization examples.
 *
 * @param id unique order identifier
 * @param customerId owner of the order
 * @param totalAmount total order value
 */
public record Order(String id, String customerId, double totalAmount) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when ids are blank or the total is negative
     */
    public Order {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        if (totalAmount < 0) {
            throw new IllegalArgumentException("totalAmount must not be negative");
        }
    }
}
