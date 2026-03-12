package com.example.demo.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitUserRegistrationEventListenerTest {

    @Test
    void onMessageDelegatesToSharedProcessor() {
        UserRegistrationEventProcessor processor = mock(UserRegistrationEventProcessor.class);
        RabbitUserRegistrationEventListener listener = new RabbitUserRegistrationEventListener(processor);
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-70",
                "u70@example.com",
                "APAC",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        listener.onMessage(event);

        verify(processor).process("rabbitmq", event);
    }
}
