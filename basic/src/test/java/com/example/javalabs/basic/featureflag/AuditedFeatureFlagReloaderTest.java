package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TimeSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditedFeatureFlagReloaderTest {

    @Test
    void writesAuditEventWhenReloadChangesConfig() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        InMemoryFeatureFlagAuditLog auditLog = new InMemoryFeatureFlagAuditLog();
        AuditedFeatureFlagReloader reloader = new AuditedFeatureFlagReloader(
                new FeatureFlagReloader(registry),
                auditLog,
                timeSource
        );

        FeatureFlagReloadReport report = reloader.reload(List.of(
                new FeatureFlagRule("checkout", true, 20),
                new FeatureFlagRule("search", true, 5)
        ));

        assertTrue(report.hasChanges());
        assertEquals(1, auditLog.size());
        assertEquals(new FeatureFlagAuditEvent(
                1_000,
                List.of("search"),
                List.of("checkout"),
                List.of()
        ), auditLog.findAll().get(0));
    }

    @Test
    void skipsAuditEventWhenReloadHasNoChanges() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        InMemoryFeatureFlagAuditLog auditLog = new InMemoryFeatureFlagAuditLog();
        AuditedFeatureFlagReloader reloader = new AuditedFeatureFlagReloader(
                new FeatureFlagReloader(registry),
                auditLog,
                new ManualTimeSource(1_000)
        );

        FeatureFlagReloadReport report = reloader.reload(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));

        assertEquals(0, report.changeCount());
        assertEquals(0, auditLog.size());
    }

    @Test
    void auditEventCountsAllChangedFlags() {
        FeatureFlagAuditEvent event = new FeatureFlagAuditEvent(
                1_000,
                List.of("added"),
                List.of("updated"),
                List.of("removed")
        );

        assertEquals(3, event.changeCount());
    }

    @Test
    void rejectsInvalidDependenciesAndEvents() {
        FeatureFlagReloader reloader = new FeatureFlagReloader(new FeatureFlagRegistry(List.of()));
        InMemoryFeatureFlagAuditLog auditLog = new InMemoryFeatureFlagAuditLog();
        ManualTimeSource timeSource = new ManualTimeSource(0);

        assertThrows(IllegalArgumentException.class, () -> new AuditedFeatureFlagReloader(null, auditLog, timeSource));
        assertThrows(IllegalArgumentException.class, () -> new AuditedFeatureFlagReloader(reloader, null, timeSource));
        assertThrows(IllegalArgumentException.class, () -> new AuditedFeatureFlagReloader(reloader, auditLog, null));
        assertThrows(IllegalArgumentException.class, () -> auditLog.record(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagAuditEvent(-1, List.of(), List.of(), List.of()));
    }
}

