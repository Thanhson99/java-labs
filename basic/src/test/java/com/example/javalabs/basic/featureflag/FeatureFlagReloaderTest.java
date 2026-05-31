package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloaderTest {

    @Test
    void reportsAddedUpdatedRemovedAndUnchangedRules() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10),
                new FeatureFlagRule("search", true, 50),
                new FeatureFlagRule("old-dashboard", true, 100)
        ));
        FeatureFlagReloader reloader = new FeatureFlagReloader(registry);

        FeatureFlagReloadReport report = reloader.reload(List.of(
                new FeatureFlagRule("checkout", true, 10),
                new FeatureFlagRule("search", true, 75),
                new FeatureFlagRule("recommendations", true, 5)
        ));

        assertEquals(List.of("recommendations"), report.addedFlags());
        assertEquals(List.of("search"), report.updatedFlags());
        assertEquals(List.of("old-dashboard"), report.removedFlags());
        assertEquals(List.of("checkout"), report.unchangedFlags());
        assertTrue(report.hasChanges());
        assertEquals(3, report.changeCount());
        assertEquals(3, registry.size());
        assertTrue(registry.find("old-dashboard").isEmpty());
    }

    @Test
    void unchangedReloadDoesNotReportChanges() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        FeatureFlagReloader reloader = new FeatureFlagReloader(registry);

        FeatureFlagReloadReport report = reloader.reload(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));

        assertFalse(report.hasChanges());
        assertEquals(List.of("checkout"), report.unchangedFlags());
        assertEquals(0, report.changeCount());
    }

    @Test
    void duplicateRulesUseLastRuleInSnapshot() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        FeatureFlagReloader reloader = new FeatureFlagReloader(registry);

        FeatureFlagReloadReport report = reloader.reload(List.of(
                new FeatureFlagRule("checkout", true, 10),
                new FeatureFlagRule("checkout", true, 20)
        ));

        assertEquals(List.of("checkout"), report.addedFlags());
        assertEquals(new FeatureFlagRule("checkout", true, 20), registry.find("checkout").orElseThrow());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());

        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloader(null));

        FeatureFlagReloader reloader = new FeatureFlagReloader(registry);
        assertThrows(IllegalArgumentException.class, () -> reloader.reload(null));
        assertThrows(IllegalArgumentException.class, () -> reloader.reload(Arrays.asList(
                new FeatureFlagRule("checkout", true, 10),
                null
        )));
    }
}
