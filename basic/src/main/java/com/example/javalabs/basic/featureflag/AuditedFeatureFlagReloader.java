package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.TimeSource;
import java.util.List;

/**
 * Wraps feature flag reload and writes an audit event only when config changed.
 *
 * <p>Audit logs should record meaningful changes, not every reload attempt. This wrapper keeps the
 * raw reload report available while avoiding noisy audit entries for unchanged snapshots.</p>
 */
public final class AuditedFeatureFlagReloader {

    private final FeatureFlagReloader reloader;
    private final FeatureFlagAuditLog auditLog;
    private final TimeSource timeSource;

    /**
     * Creates an audited reloader.
     *
     * @param reloader reload implementation that mutates the registry
     * @param auditLog audit log receiving change events
     * @param timeSource clock used for audit timestamps
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public AuditedFeatureFlagReloader(
            FeatureFlagReloader reloader,
            FeatureFlagAuditLog auditLog,
            TimeSource timeSource) {
        if (reloader == null) {
            throw new IllegalArgumentException("reloader must not be null");
        }
        if (auditLog == null) {
            throw new IllegalArgumentException("auditLog must not be null");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.reloader = reloader;
        this.auditLog = auditLog;
        this.timeSource = timeSource;
    }

    /**
     * Reloads feature flag rules and records an audit event only when rules changed.
     *
     * @param newRules desired complete rule set
     * @return reload report from the wrapped reloader
     * @throws IllegalArgumentException when {@code newRules} is invalid
     */
    public FeatureFlagReloadReport reload(List<FeatureFlagRule> newRules) {
        FeatureFlagReloadReport report = reloader.reload(newRules);
        if (report.hasChanges()) {
            // Store only changed flag names; unchanged names add noise without helping rollback.
            auditLog.record(new FeatureFlagAuditEvent(
                    timeSource.currentTimeMillis(),
                    report.addedFlags(),
                    report.updatedFlags(),
                    report.removedFlags()
            ));
        }
        return report;
    }
}

