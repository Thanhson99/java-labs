package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutboxRegistrationServiceTest {

    @Test
    void registrationStoresUserAndPendingOutboxEvent() {
        InMemoryUserProfileRepository repository = new InMemoryUserProfileRepository("users");
        InMemoryOutboxEventStore outboxStore = new InMemoryOutboxEventStore();
        OutboxRegistrationService service = new OutboxRegistrationService(repository, outboxStore);

        RegistrationResult result = service.register(new UserProfile("u-1", "alice@example.com", Region.APAC));

        assertTrue(result.accepted());
        assertTrue(repository.findById("u-1").isPresent());
        OutboxEvent event = outboxStore.findAll().get(0);
        assertEquals("UserRegistered", event.type());
        assertEquals(OutboxEventStatus.PENDING, event.status());
        assertEquals(0, event.attemptCount());
    }

    @Test
    void dispatcherPublishesPendingEventAndMarksItPublished() {
        InMemoryOutboxEventStore outboxStore = new InMemoryOutboxEventStore();
        outboxStore.save(userRegisteredEvent("u-1"));
        InMemoryOutboxEventPublisher publisher = new InMemoryOutboxEventPublisher();
        OutboxDispatcher dispatcher = new OutboxDispatcher(outboxStore, publisher);

        int dispatched = dispatcher.dispatchPending(10);

        assertEquals(1, dispatched);
        assertEquals(1, publisher.publishedEvents().size());
        OutboxEvent event = outboxStore.findAll().get(0);
        assertEquals(OutboxEventStatus.PUBLISHED, event.status());
        assertEquals(1, event.attemptCount());
    }

    @Test
    void failedPublishIsKeptForRetry() {
        InMemoryOutboxEventStore outboxStore = new InMemoryOutboxEventStore();
        outboxStore.save(userRegisteredEvent("u-1"));
        FlakyOutboxEventPublisher publisher = new FlakyOutboxEventPublisher(1);
        OutboxDispatcher dispatcher = new OutboxDispatcher(outboxStore, publisher);

        assertEquals(0, dispatcher.dispatchPending(10));
        assertEquals(OutboxEventStatus.FAILED, outboxStore.findAll().get(0).status());
        assertEquals(1, outboxStore.findAll().get(0).attemptCount());

        assertEquals(1, dispatcher.dispatchPending(10));
        assertEquals(OutboxEventStatus.PUBLISHED, outboxStore.findAll().get(0).status());
        assertEquals(2, outboxStore.findAll().get(0).attemptCount());
        assertEquals(2, publisher.attemptCount());
        assertEquals(1, publisher.publishedCount());
    }

    private static OutboxEvent userRegisteredEvent(String userId) {
        return new OutboxEvent(
                "user-registered-" + userId,
                "UserRegistered",
                userId,
                userId + "@example.com",
                OutboxEventStatus.PENDING,
                0
        );
    }
}
