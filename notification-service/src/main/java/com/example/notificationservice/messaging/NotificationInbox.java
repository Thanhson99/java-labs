package com.example.notificationservice.messaging;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory inbox so the consumer service can expose what it received.
 */
@Component
public class NotificationInbox {

    private final Clock clock;
    private final List<NotificationMessage> messages = new ArrayList<>();
    private final ConcurrentHashMap<String, Integer> transportCounts = new ConcurrentHashMap<>();

    public NotificationInbox(Clock clock) {
        this.clock = clock;
    }

    public synchronized void append(String transport, UserRegisteredEvent event) {
        messages.add(new NotificationMessage(
                transport,
                event.userId(),
                event.email(),
                event.region(),
                clock.instant()
        ));
        transportCounts.merge(transport, 1, Integer::sum);
    }

    public synchronized List<NotificationMessage> messages() {
        return List.copyOf(messages);
    }

    public Map<String, Integer> transportCounts() {
        return Map.copyOf(transportCounts);
    }
}
