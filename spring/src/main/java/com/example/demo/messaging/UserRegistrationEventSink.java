package com.example.demo.messaging;

/**
 * Transport-specific sink for user registration events.
 */
public interface UserRegistrationEventSink {

    /**
     * Human-readable transport name such as kafka or rabbitmq.
     *
     * @return transport identifier
     */
    String transportName();

    /**
     * Publishes the registration event using a specific transport.
     *
     * @param event immutable event payload
     */
    void publish(UserRegisteredEvent event);
}
