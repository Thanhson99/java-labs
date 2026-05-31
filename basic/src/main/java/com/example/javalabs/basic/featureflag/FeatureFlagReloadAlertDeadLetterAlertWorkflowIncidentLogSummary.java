package com.example.javalabs.basic.featureflag;

/**
 * Immutable dashboard summary of incident-log contents.
 *
 * <p>The summary is intentionally derived data. It can be rebuilt from the incident log at any time,
 * which keeps the log as the source of truth and avoids duplicated mutable counters.</p>
 *
 * @param totalIncidents number of retained incidents
 * @param criticalIncidents retained incidents with critical status
 * @param warningIncidents retained incidents with warning status
 * @param deliveredIncidents incidents whose alert was delivered
 * @param undeliveredIncidents incidents whose alert was not delivered
 * @param onCallIncidents incidents routed to on-call
 * @param dashboardIncidents incidents routed to dashboard
 * @param suppressedIncidents incidents routed to none
 * @param droppedIncidents incidents dropped by the bounded log
 * @param deliveryRate delivered incidents divided by total incidents
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
        int totalIncidents,
        int criticalIncidents,
        int warningIncidents,
        int deliveredIncidents,
        int undeliveredIncidents,
        int onCallIncidents,
        int dashboardIncidents,
        int suppressedIncidents,
        int droppedIncidents,
        double deliveryRate) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when counters are negative, inconsistent, or rates are invalid
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary {
        validateNonNegative(totalIncidents, "totalIncidents");
        validateNonNegative(criticalIncidents, "criticalIncidents");
        validateNonNegative(warningIncidents, "warningIncidents");
        validateNonNegative(deliveredIncidents, "deliveredIncidents");
        validateNonNegative(undeliveredIncidents, "undeliveredIncidents");
        validateNonNegative(onCallIncidents, "onCallIncidents");
        validateNonNegative(dashboardIncidents, "dashboardIncidents");
        validateNonNegative(suppressedIncidents, "suppressedIncidents");
        validateNonNegative(droppedIncidents, "droppedIncidents");
        if (criticalIncidents + warningIncidents != totalIncidents) {
            throw new IllegalArgumentException("status counters must equal totalIncidents");
        }
        if (deliveredIncidents + undeliveredIncidents != totalIncidents) {
            throw new IllegalArgumentException("delivery counters must equal totalIncidents");
        }
        if (onCallIncidents + dashboardIncidents + suppressedIncidents != totalIncidents) {
            throw new IllegalArgumentException("channel counters must equal totalIncidents");
        }
        if (deliveryRate < 0.0 || deliveryRate > 1.0) {
            throw new IllegalArgumentException("deliveryRate must be between 0 and 1");
        }
    }

    /**
     * @return {@code true} when at least one retained incident was not delivered
     */
    public boolean hasUndeliveredIncidents() {
        return undeliveredIncidents > 0;
    }

    /**
     * Validates a non-negative counter.
     *
     * @param value counter value
     * @param name counter name for error messages
     * @throws IllegalArgumentException when {@code value} is negative
     */
    private static void validateNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
    }
}
