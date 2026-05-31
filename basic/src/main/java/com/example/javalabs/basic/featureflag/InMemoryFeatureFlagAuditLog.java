package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory audit log used by examples and tests.
 */
public final class InMemoryFeatureFlagAuditLog implements FeatureFlagAuditLog {

    private final List<FeatureFlagAuditEvent> events = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void record(FeatureFlagAuditEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        events.add(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FeatureFlagAuditEvent> findAll() {
        return List.copyOf(events);
    }

    /**
     * @return number of audit events stored
     */
    public int size() {
        return events.size();
    }
}
