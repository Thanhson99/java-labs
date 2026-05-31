package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Stores feature flag audit events.
 */
public interface FeatureFlagAuditLog {

    /**
     * Records one feature flag audit event.
     *
     * @param event audit event to record
     * @throws IllegalArgumentException when {@code event} is {@code null}
     */
    void record(FeatureFlagAuditEvent event);

    /**
     * Returns audit events in record order.
     *
     * @return immutable or defensive snapshot of audit events
     */
    List<FeatureFlagAuditEvent> findAll();
}
