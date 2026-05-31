package com.example.javalabs.basic;

/**
 * Read model returned to an API caller after combining order and customer data.
 *
 * @param orderId order identifier
 * @param customerName customer display name
 * @param customerSegment customer business segment
 * @param totalAmount total order value
 */
public record OrderSummary(
        String orderId,
        String customerName,
        String customerSegment,
        double totalAmount) {
}
