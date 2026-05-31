package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagRegistryTest {

    @Test
    void findsConfiguredRuleAndReturnsDisabledDefaultForMissingRule() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("new-checkout", true, 25)
        ));

        assertEquals(new FeatureFlagRule("new-checkout", true, 25),
                registry.find("new-checkout").orElseThrow());
        assertEquals(new FeatureFlagRule("missing-flag", false, 0),
                registry.findOrDisabled("missing-flag"));
    }

    @Test
    void upsertReturnsWhetherRuleChanged() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        FeatureFlagRule first = new FeatureFlagRule("new-checkout", true, 25);
        FeatureFlagRule updated = new FeatureFlagRule("new-checkout", true, 50);

        assertTrue(registry.upsert(first));
        assertFalse(registry.upsert(first));
        assertTrue(registry.upsert(updated));
        assertEquals(updated, registry.find("new-checkout").orElseThrow());
    }

    @Test
    void snapshotCannotMutateRegistry() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("new-checkout", true, 25)
        ));

        Map<String, FeatureFlagRule> snapshot = registry.snapshot();

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.put("other", new FeatureFlagRule("other", true, 100)));
        assertEquals(1, registry.size());
    }

    @Test
    void evaluatesUsingConfiguredOrDefaultRule() {
        FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("new-checkout", true, 100)
        ));

        assertTrue(registry.evaluate("new-checkout", "u-1", evaluator).enabled());
        assertFalse(registry.evaluate("missing-flag", "u-1", evaluator).enabled());
        assertEquals("flag disabled", registry.evaluate("missing-flag", "u-1", evaluator).reason());
    }

    @Test
    void removesConfiguredRule() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("new-checkout", true, 25)
        ));

        assertTrue(registry.remove("new-checkout"));
        assertFalse(registry.remove("new-checkout"));
        assertTrue(registry.find("new-checkout").isEmpty());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagRegistry(null));

        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        assertThrows(IllegalArgumentException.class, () -> registry.find(" "));
        assertThrows(IllegalArgumentException.class, () -> registry.upsert(null));
        assertThrows(IllegalArgumentException.class,
                () -> registry.evaluate("new-checkout", "u-1", null));
    }
}
