package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdempotentNotificationClientTest {

    @Test
    void duplicateWelcomeMessageForSameUserIsSkipped() {
        InMemoryNotificationClient delegate = new InMemoryNotificationClient();
        InMemoryIdempotencyStore store = new InMemoryIdempotencyStore();
        IdempotentNotificationClient client = new IdempotentNotificationClient(delegate, store);
        UserProfile user = new UserProfile("u-1", "alice@example.com", Region.APAC);

        client.sendWelcomeMessage(user);
        client.sendWelcomeMessage(user);

        assertEquals(1, delegate.sentMessages().size());
        assertEquals(1, store.processedCount());
        assertTrue(store.wasProcessed("WELCOME:u-1"));
    }

    @Test
    void differentUsersProduceDifferentIdempotencyKeys() {
        InMemoryNotificationClient delegate = new InMemoryNotificationClient();
        InMemoryIdempotencyStore store = new InMemoryIdempotencyStore();
        IdempotentNotificationClient client = new IdempotentNotificationClient(delegate, store);

        client.sendWelcomeMessage(new UserProfile("u-1", "alice@example.com", Region.APAC));
        client.sendWelcomeMessage(new UserProfile("u-2", "bob@example.com", Region.EU));

        assertEquals(2, delegate.sentMessages().size());
        assertEquals(2, store.processedCount());
    }
}
