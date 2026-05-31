package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeadLetteringNotificationClientTest {

    private final UserProfile userProfile = new UserProfile("u-1", "alice@example.com", Region.APAC);

    @Test
    void capturesFailedNotificationInDeadLetterStore() {
        FlakyNotificationClient failingClient = new FlakyNotificationClient(1);
        InMemoryDeadLetterStore deadLetterStore = new InMemoryDeadLetterStore();
        DeadLetteringNotificationClient client =
                new DeadLetteringNotificationClient(failingClient, deadLetterStore);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));

        assertEquals(1, deadLetterStore.size());
        DeadLetterMessage message = deadLetterStore.findAll().get(0);
        assertEquals("WELCOME:u-1", message.key());
        assertEquals("alice@example.com", message.payload());
        assertEquals("temporary notification failure", message.reason());
    }

    @Test
    void successfulNotificationDoesNotCreateDeadLetter() {
        InMemoryNotificationClient realClient = new InMemoryNotificationClient();
        InMemoryDeadLetterStore deadLetterStore = new InMemoryDeadLetterStore();
        DeadLetteringNotificationClient client =
                new DeadLetteringNotificationClient(realClient, deadLetterStore);

        client.sendWelcomeMessage(userProfile);

        assertEquals(1, realClient.sentMessages().size());
        assertEquals(0, deadLetterStore.size());
    }
}
