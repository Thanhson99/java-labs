package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProfileChangeEventCoalescerTest {

    private final ProfileChangeEventCoalescer coalescer = new ProfileChangeEventCoalescer();

    @Test
    void mergesMultipleEventsForSameUser() {
        List<UserProfileChangeEvent> result = coalescer.coalesce(List.of(
                event("u-1", new FieldChange("email", "old@example.com", "mid@example.com")),
                event("u-1", new FieldChange("email", "mid@example.com", "new@example.com")),
                event("u-1", new FieldChange("region", "US", "APAC"))
        ));

        assertEquals(List.of(event("u-1",
                new FieldChange("email", "old@example.com", "new@example.com"),
                new FieldChange("region", "US", "APAC")
        )), result);
    }

    @Test
    void removesFieldWhenThereIsNoNetChange() {
        List<UserProfileChangeEvent> result = coalescer.coalesce(List.of(
                event("u-1", new FieldChange("email", "old@example.com", "new@example.com")),
                event("u-1", new FieldChange("email", "new@example.com", "old@example.com"))
        ));

        assertEquals(List.of(), result);
    }

    @Test
    void preservesFirstUserOrderAcrossTheBatch() {
        List<UserProfileChangeEvent> result = coalescer.coalesce(List.of(
                event("u-2", new FieldChange("region", "US", "EU")),
                event("u-1", new FieldChange("email", "a@example.com", "b@example.com")),
                event("u-2", new FieldChange("email", "c@example.com", "d@example.com"))
        ));

        assertEquals(List.of(
                event("u-2",
                        new FieldChange("region", "US", "EU"),
                        new FieldChange("email", "c@example.com", "d@example.com")),
                event("u-1", new FieldChange("email", "a@example.com", "b@example.com"))
        ), result);
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> coalescer.coalesce(null));
        assertThrows(IllegalArgumentException.class, () -> coalescer.coalesce(Arrays.asList(
                event("u-1", new FieldChange("email", "a@example.com", "b@example.com")),
                null
        )));
    }

    private static UserProfileChangeEvent event(String userId, FieldChange... changes) {
        return new UserProfileChangeEvent(userId, List.of(changes));
    }
}
