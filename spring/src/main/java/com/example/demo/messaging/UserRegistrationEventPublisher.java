package com.example.demo.messaging;

import com.example.demo.observability.ApplicationMetrics;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Fan-out publisher that sends one domain event to every enabled transport.
 */
@Service
public class UserRegistrationEventPublisher {

    private final List<UserRegistrationEventSink> sinks;
    private final ApplicationMetrics applicationMetrics;

    public UserRegistrationEventPublisher(List<UserRegistrationEventSink> sinks,
                                          ApplicationMetrics applicationMetrics) {
        this.sinks = List.copyOf(sinks);
        this.applicationMetrics = applicationMetrics;
    }

    /**
     * Publishes the event to each enabled sink.
     *
     * @param event immutable event payload
     */
    public void publish(UserRegisteredEvent event) {
        for (UserRegistrationEventSink sink : sinks) {
            sink.publish(event);
            applicationMetrics.recordPublishedEvent(sink.transportName());
        }
    }

    /**
     * Exposes enabled transport names for diagnostics and tests.
     *
     * @return enabled sink names in registration order
     */
    public List<String> enabledTransports() {
        return sinks.stream()
                .map(UserRegistrationEventSink::transportName)
                .toList();
    }
}
