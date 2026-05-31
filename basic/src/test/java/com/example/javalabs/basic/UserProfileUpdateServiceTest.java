package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserProfileUpdateServiceTest {

    @Test
    void skipsSaveWhenEmailIsUnchangedAfterNormalization() {
        InMemoryUserProfileRepository delegate = new InMemoryUserProfileRepository("users");
        delegate.save(new UserProfile("u-1", "alice@example.com", Region.APAC));
        CountingUserProfileRepository repository = new CountingUserProfileRepository(delegate);
        UserProfileUpdateService service = new UserProfileUpdateService(repository);

        ProfileUpdateResult result = service.updateEmailIfChanged("u-1", " Alice@Example.com ");

        assertFalse(result.changed());
        assertEquals("email unchanged", result.message());
        assertEquals(0, repository.saveCount());
        assertEquals("alice@example.com", repository.findById("u-1").orElseThrow().email());
    }

    @Test
    void savesOnlyWhenEmailChanges() {
        InMemoryUserProfileRepository delegate = new InMemoryUserProfileRepository("users");
        delegate.save(new UserProfile("u-1", "alice@example.com", Region.APAC));
        CountingUserProfileRepository repository = new CountingUserProfileRepository(delegate);
        UserProfileUpdateService service = new UserProfileUpdateService(repository);

        ProfileUpdateResult result = service.updateEmailIfChanged("u-1", "alice.new@example.com");

        assertTrue(result.changed());
        assertEquals("email updated", result.message());
        assertEquals(1, repository.saveCount());
        assertEquals("alice.new@example.com", repository.findById("u-1").orElseThrow().email());
    }

    @Test
    void failsWhenUserDoesNotExist() {
        UserProfileUpdateService service =
                new UserProfileUpdateService(new InMemoryUserProfileRepository("users"));

        assertThrows(IllegalStateException.class,
                () -> service.updateEmailIfChanged("missing", "missing@example.com"));
    }
}
