package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Builds dashboard-friendly counters from the incident log.
 *
 * <p>The summarizer does not mutate or compact the log. It reads the immutable incident snapshot,
 * derives counters, and returns a value object suitable for dashboards, tests, or console output.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer {

    /**
     * Summarizes retained incidents and dropped-history count.
     *
     * @param log incident log to summarize
     * @return immutable summary of retained incidents
     * @throws IllegalArgumentException when {@code log} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summarize(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log) {
        if (log == null) {
            throw new IllegalArgumentException("log must not be null");
        }

        List<FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident> incidents = log.findAll();
        int criticalIncidents = 0;
        int warningIncidents = 0;
        int deliveredIncidents = 0;
        int onCallIncidents = 0;
        int dashboardIncidents = 0;
        int suppressedIncidents = 0;

        for (FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident incident : incidents) {
            if (incident.status() == FeatureFlagReloadHealthStatus.CRITICAL) {
                criticalIncidents++;
            } else {
                warningIncidents++;
            }

            if (incident.delivered()) {
                deliveredIncidents++;
            }

            if (incident.channel() == FeatureFlagReloadAlertChannel.ON_CALL) {
                onCallIncidents++;
            } else if (incident.channel() == FeatureFlagReloadAlertChannel.DASHBOARD) {
                dashboardIncidents++;
            } else {
                suppressedIncidents++;
            }
        }

        int totalIncidents = incidents.size();
        int undeliveredIncidents = totalIncidents - deliveredIncidents;
        double deliveryRate = totalIncidents == 0 ? 0.0 : (double) deliveredIncidents / totalIncidents;
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                totalIncidents,
                criticalIncidents,
                warningIncidents,
                deliveredIncidents,
                undeliveredIncidents,
                onCallIncidents,
                dashboardIncidents,
                suppressedIncidents,
                log.droppedCount(),
                deliveryRate
        );
    }
}
