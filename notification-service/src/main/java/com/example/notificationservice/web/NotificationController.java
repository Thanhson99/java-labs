package com.example.notificationservice.web;

import com.example.notificationservice.messaging.NotificationInbox;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationInbox notificationInbox;

    public NotificationController(NotificationInbox notificationInbox) {
        this.notificationInbox = notificationInbox;
    }

    @GetMapping("/inbox")
    public Map<String, Object> inbox() {
        return Map.of(
                "count", notificationInbox.messages().size(),
                "transportCounts", notificationInbox.transportCounts(),
                "messages", notificationInbox.messages()
        );
    }

    @GetMapping("/healthz")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
