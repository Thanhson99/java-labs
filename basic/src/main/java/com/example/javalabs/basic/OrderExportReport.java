package com.example.javalabs.basic;

/**
 * Result of exporting or processing order data.
 *
 * @param processedOrders number of processed orders
 * @param totalAmount sum of processed order amounts
 * @param repositoryCalls number of repository read calls used by the strategy
 */
public record OrderExportReport(int processedOrders, double totalAmount, int repositoryCalls) {
}
