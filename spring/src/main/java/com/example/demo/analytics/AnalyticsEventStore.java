package com.example.demo.analytics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Writes simple operational events to a secondary database.
 */
@Component
public class AnalyticsEventStore {

    private final JdbcTemplate analyticsJdbcTemplate;

    public AnalyticsEventStore(JdbcTemplate analyticsJdbcTemplate) {
        this.analyticsJdbcTemplate = analyticsJdbcTemplate;
    }

    /**
     * Persists an analytics event.
     *
     * @param eventType event classification
     * @param subjectId related entity identifier
     * @param details free-form summary for the demo
     */
    public void record(String eventType, String subjectId, String details) {
        analyticsJdbcTemplate.update(
                "insert into analytics_events(event_type, subject_id, details) values (?, ?, ?)",
                eventType,
                subjectId,
                details
        );
    }

    public int countEvents() {
        Integer count = analyticsJdbcTemplate.queryForObject("select count(*) from analytics_events", Integer.class);
        return count == null ? 0 : count;
    }
}
