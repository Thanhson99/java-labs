package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadValidatorTest {

    @Test
    void acceptsSmallRolloutIncreaseAndSafeNewFlag() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 20)
        ));
        FeatureFlagReloadValidator validator = new FeatureFlagReloadValidator(20, 10);

        FeatureFlagReloadValidationReport report = validator.validate(registry, List.of(
                new FeatureFlagRule("checkout", true, 35),
                new FeatureFlagRule("recommendations", true, 10)
        ));

        assertTrue(report.accepted());
        assertFalse(report.rejected());
        assertEquals(List.of(), report.violations());
    }

    @Test
    void rejectsDuplicateFlagsLargeRolloutJumpAndLargeNewFlag() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        FeatureFlagReloadValidator validator = new FeatureFlagReloadValidator(25, 15);

        FeatureFlagReloadValidationReport report = validator.validate(registry, List.of(
                new FeatureFlagRule("checkout", true, 60),
                new FeatureFlagRule("search", true, 50),
                new FeatureFlagRule("search", true, 10)
        ));

        assertTrue(report.rejected());
        assertEquals(List.of(
                "flag checkout rollout increase 50 exceeds max 25",
                "new flag search rollout 50 exceeds max 15",
                "duplicate flag: search"
        ), report.violations());
    }

    @Test
    void disabledFlagsDoNotTriggerRolloutSafetyViolations() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        FeatureFlagReloadValidator validator = new FeatureFlagReloadValidator(20, 5);

        FeatureFlagReloadValidationReport report = validator.validate(registry, List.of(
                new FeatureFlagRule("checkout", false, 100),
                new FeatureFlagRule("new-disabled", false, 100)
        ));

        assertTrue(report.accepted());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadValidator(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadValidator(10, 101));

        FeatureFlagReloadValidator validator = new FeatureFlagReloadValidator(10, 10);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());

        assertThrows(IllegalArgumentException.class, () -> validator.validate((Map<String, FeatureFlagRule>) null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> validator.validate((FeatureFlagRegistry) null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(registry, null));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(registry, Arrays.asList(
                new FeatureFlagRule("checkout", true, 10),
                null
        )));
    }
}
