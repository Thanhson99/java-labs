package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserProfileDiffServiceTest {

    private final UserProfileDiffService service = new UserProfileDiffService();

    @Test
    void returnsEmptyDiffWhenProfileIsUnchanged() {
        UserProfile before = new UserProfile("u-1", "alice@example.com", Region.US);
        UserProfile after = new UserProfile("u-1", "alice@example.com", Region.US);

        UserProfileDiff diff = service.diff(before, after);

        assertFalse(diff.hasChanges());
        assertEquals(0, diff.changeCount());
        assertEquals(List.of(), diff.changes());
    }

    @Test
    void detectsEmailAndRegionChanges() {
        UserProfile before = new UserProfile("u-1", "alice@example.com", Region.US);
        UserProfile after = new UserProfile("u-1", "alice@new.example.com", Region.APAC);

        UserProfileDiff diff = service.diff(before, after);

        assertTrue(diff.hasChanges());
        assertEquals(2, diff.changeCount());
        assertEquals(List.of(
                new FieldChange("email", "alice@example.com", "alice@new.example.com"),
                new FieldChange("region", "US", "APAC")
        ), diff.changes());
    }

    @Test
    void rejectsProfilesWithDifferentIds() {
        UserProfile before = new UserProfile("u-1", "alice@example.com", Region.US);
        UserProfile after = new UserProfile("u-2", "alice@example.com", Region.US);

        assertThrows(IllegalArgumentException.class, () -> service.diff(before, after));
    }

    @Test
    void rejectsNullSnapshots() {
        UserProfile profile = new UserProfile("u-1", "alice@example.com", Region.US);

        assertThrows(IllegalArgumentException.class, () -> service.diff(null, profile));
        assertThrows(IllegalArgumentException.class, () -> service.diff(profile, null));
    }
}
