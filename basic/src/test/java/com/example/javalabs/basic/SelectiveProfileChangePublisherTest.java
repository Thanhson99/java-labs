package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectiveProfileChangePublisherTest {

    @Test
    void skipsPublishingWhenProfileDidNotChange() {
        InMemoryUserProfileChangePublisher publisher = new InMemoryUserProfileChangePublisher();
        SelectiveProfileChangePublisher service =
                new SelectiveProfileChangePublisher(new UserProfileDiffService(), publisher);

        boolean published = service.publishIfChanged(
                new UserProfile("u-1", "alice@example.com", Region.US),
                new UserProfile("u-1", "alice@example.com", Region.US)
        );

        assertFalse(published);
        assertEquals(0, publisher.publishCount());
    }

    @Test
    void publishesOnlyChangedFields() {
        InMemoryUserProfileChangePublisher publisher = new InMemoryUserProfileChangePublisher();
        SelectiveProfileChangePublisher service =
                new SelectiveProfileChangePublisher(new UserProfileDiffService(), publisher);

        boolean published = service.publishIfChanged(
                new UserProfile("u-1", "alice@example.com", Region.US),
                new UserProfile("u-1", "alice@new.example.com", Region.APAC)
        );

        assertTrue(published);
        assertEquals(1, publisher.publishCount());
        assertEquals(new UserProfileChangeEvent("u-1", List.of(
                new FieldChange("email", "alice@example.com", "alice@new.example.com"),
                new FieldChange("region", "US", "APAC")
        )), publisher.publishedEvents().get(0));
    }

    @Test
    void rejectsInvalidDependenciesAndEvents() {
        InMemoryUserProfileChangePublisher publisher = new InMemoryUserProfileChangePublisher();

        assertThrows(IllegalArgumentException.class, () -> new SelectiveProfileChangePublisher(null, publisher));
        assertThrows(IllegalArgumentException.class,
                () -> new SelectiveProfileChangePublisher(new UserProfileDiffService(), null));
        assertThrows(IllegalArgumentException.class, () -> publisher.publish(null));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProfileChangeEvent("u-1", List.of()));
    }
}
