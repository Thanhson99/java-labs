package com.example.demo.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaUserRegistrationEventListenerTest {

    @Test
    void onMessageDelegatesToSharedProcessor() {
        UserRegistrationEventProcessor processor = mock(UserRegistrationEventProcessor.class);
        KafkaUserRegistrationEventListener listener = new KafkaUserRegistrationEventListener(processor);
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-60",
                "u60@example.com",
                "US",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        listener.onMessage(event);

        verify(processor).process("kafka", event);
    }
}
