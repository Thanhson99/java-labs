package com.example.demo.messaging;

import com.example.demo.observability.ApplicationMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationEventPublisherTest {

    @Test
    void publishFanOutsToEveryEnabledSink() {
        List<String> calls = new ArrayList<>();
        UserRegistrationEventSink kafkaSink = new RecordingSink("kafka", calls);
        UserRegistrationEventSink rabbitSink = new RecordingSink("rabbitmq", calls);
        ApplicationMetrics applicationMetrics = new ApplicationMetrics(new SimpleMeterRegistry());
        UserRegistrationEventPublisher publisher = new UserRegistrationEventPublisher(
                List.of(kafkaSink, rabbitSink),
                applicationMetrics
        );
        UserRegisteredEvent event = new UserRegisteredEvent(
                "u-30",
                "u30@example.com",
                "US",
                Instant.parse("2026-03-12T00:00:00Z"),
                "registration-service"
        );

        publisher.publish(event);

        assertThat(calls).containsExactly("kafka:u-30", "rabbitmq:u-30");
        assertThat(publisher.enabledTransports()).containsExactly("kafka", "rabbitmq");
        assertThat(applicationMetrics.snapshot())
                .extractingByKey("messaging")
                .isNotNull();
    }

    private record RecordingSink(String transportName, List<String> calls) implements UserRegistrationEventSink {

        @Override
        public void publish(UserRegisteredEvent event) {
            calls.add(transportName + ":" + event.userId());
        }
    }
}
