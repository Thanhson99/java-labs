package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationServiceTest {

    @Test
    void registersUserAndSendsNotificationWhenRateLimitAllows() {
        MultiDatabaseUserProfileRepository repository = new MultiDatabaseUserProfileRepository(Map.of(
                Region.APAC, new InMemoryUserProfileRepository("users-apac"),
                Region.EU, new InMemoryUserProfileRepository("users-eu"),
                Region.US, new InMemoryUserProfileRepository("users-us")
        ));
        InMemoryNotificationClient notificationClient = new InMemoryNotificationClient();
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 1_000, new ManualTimeSource(0));
        RegistrationService service = new RegistrationService(repository, notificationClient, limiter);

        RegistrationResult result = service.register(
                "api-key-1",
                new UserProfile("u-1", "alice@example.com", Region.APAC)
        );

        assertTrue(result.accepted());
        assertTrue(repository.findById("u-1").isPresent());
        assertEquals(1, notificationClient.sentMessages().size());
    }

    @Test
    void rejectsUserWhenRateLimitIsExceeded() {
        MultiDatabaseUserProfileRepository repository = new MultiDatabaseUserProfileRepository(Map.of(
                Region.APAC, new InMemoryUserProfileRepository("users-apac"),
                Region.EU, new InMemoryUserProfileRepository("users-eu"),
                Region.US, new InMemoryUserProfileRepository("users-us")
        ));
        InMemoryNotificationClient notificationClient = new InMemoryNotificationClient();
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 1_000, timeSource);
        RegistrationService service = new RegistrationService(repository, notificationClient, limiter);

        assertTrue(service.register("api-key-1", new UserProfile("u-1", "alice@example.com", Region.APAC)).accepted());

        RegistrationResult second = service.register(
                "api-key-1",
                new UserProfile("u-2", "bob@example.com", Region.EU)
        );

        assertFalse(second.accepted());
        assertEquals("rate limit exceeded", second.message());
        assertTrue(repository.findById("u-2").isEmpty());
        assertEquals(1, notificationClient.sentMessages().size());
    }
}
