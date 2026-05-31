package com.example.javalabs.basic;

/**
 * Registration service that writes an outbox event instead of calling downstream directly.
 *
 * <p>The outbox pattern keeps the domain write and the intent to publish in the same local flow.
 * A separate dispatcher can publish later, which avoids losing events when the downstream system is
 * temporarily unavailable.</p>
 */
public final class OutboxRegistrationService {

    private final UserProfileRepository repository;
    private final OutboxEventStore outboxEventStore;

    /**
     * Creates a registration service that persists users and outbox events.
     *
     * @param repository user profile repository
     * @param outboxEventStore store used to retain publishable events
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public OutboxRegistrationService(UserProfileRepository repository, OutboxEventStore outboxEventStore) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        if (outboxEventStore == null) {
            throw new IllegalArgumentException("outboxEventStore must not be null");
        }
        this.repository = repository;
        this.outboxEventStore = outboxEventStore;
    }

    /**
     * Registers a user and stores a pending outbox event.
     *
     * @param userProfile profile to register
     * @return successful registration result
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    public RegistrationResult register(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        // Save the local domain state before recording the event that describes the change.
        repository.save(userProfile);
        outboxEventStore.save(new OutboxEvent(
                "user-registered-" + userProfile.userId(),
                "UserRegistered",
                userProfile.userId(),
                userProfile.email(),
                OutboxEventStatus.PENDING,
                0
        ));
        return new RegistrationResult(true, "user registered with outbox event");
    }
}
