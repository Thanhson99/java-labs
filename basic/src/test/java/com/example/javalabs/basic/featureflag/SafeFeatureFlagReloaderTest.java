package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafeFeatureFlagReloaderTest {

    @Test
    void appliesReloadWhenValidationPasses() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        SafeFeatureFlagReloader safeReloader = new SafeFeatureFlagReloader(
                registry,
                new FeatureFlagReloadValidator(25, 10)
        );

        SafeFeatureFlagReloadResult result = safeReloader.reload(List.of(
                new FeatureFlagRule("checkout", true, 25),
                new FeatureFlagRule("recommendations", true, 5)
        ));

        assertTrue(result.applied());
        assertFalse(result.rejected());
        assertTrue(result.validationReport().accepted());
        assertEquals(Optional.of(new FeatureFlagReloadReport(
                List.of("recommendations"),
                List.of("checkout"),
                List.of(),
                List.of()
        )), result.reloadReport());
        assertEquals(new FeatureFlagRule("checkout", true, 25), registry.find("checkout").orElseThrow());
        assertEquals(new FeatureFlagRule("recommendations", true, 5), registry.find("recommendations").orElseThrow());
    }

    @Test
    void rejectsReloadAndLeavesRegistryUnchangedWhenValidationFails() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        SafeFeatureFlagReloader safeReloader = new SafeFeatureFlagReloader(
                registry,
                new FeatureFlagReloadValidator(20, 10)
        );

        SafeFeatureFlagReloadResult result = safeReloader.reload(List.of(
                new FeatureFlagRule("checkout", true, 80),
                new FeatureFlagRule("experimental-search", true, 40)
        ));

        assertTrue(result.rejected());
        assertTrue(result.reloadReport().isEmpty());
        assertEquals(List.of(
                "flag checkout rollout increase 70 exceeds max 20",
                "new flag experimental-search rollout 40 exceeds max 10"
        ), result.validationReport().violations());
        assertEquals(new FeatureFlagRule("checkout", true, 10), registry.find("checkout").orElseThrow());
        assertTrue(registry.find("experimental-search").isEmpty());
    }

    @Test
    void rejectsInvalidConstructorArguments() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        FeatureFlagReloadValidator validator = new FeatureFlagReloadValidator(10, 10);

        assertThrows(IllegalArgumentException.class, () -> new SafeFeatureFlagReloader(null, validator));
        assertThrows(IllegalArgumentException.class, () -> new SafeFeatureFlagReloader(registry, null));
    }

    @Test
    void rejectsInvalidResultArguments() {
        FeatureFlagReloadValidationReport validationReport =
                new FeatureFlagReloadValidationReport(List.of());

        assertThrows(IllegalArgumentException.class, () -> new SafeFeatureFlagReloadResult(null, Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> new SafeFeatureFlagReloadResult(validationReport, null));
        assertThrows(IllegalArgumentException.class, () -> SafeFeatureFlagReloadResult.applied(validationReport, null));
    }
}
