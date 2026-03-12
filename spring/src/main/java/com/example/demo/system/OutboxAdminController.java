package com.example.demo.system;

import com.example.demo.messaging.OutboxEventRecord;
import com.example.demo.messaging.OutboxEventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Admin API for inspecting and replaying outbox rows during local learning.
 */
@RestController
@RequestMapping("/api/system/outbox")
public class OutboxAdminController {

    private final OutboxEventStore outboxEventStore;

    public OutboxAdminController(OutboxEventStore outboxEventStore) {
        this.outboxEventStore = outboxEventStore;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> recentOutboxRows() {
        List<OutboxEventRecord> rows = outboxEventStore.listRecent(12);
        return Map.of(
                "rows", rows,
                "summary", Map.of(
                        "pending", outboxEventStore.countPending(),
                        "published", outboxEventStore.countPublished(),
                        "deadLetter", outboxEventStore.countDeadLetter()
                )
        );
    }

    @PostMapping(value = "/{outboxId}/replay", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> replayDeadLetter(@PathVariable long outboxId) {
        boolean replayed = outboxEventStore.replayDeadLetter(outboxId);
        if (!replayed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "dead-letter outbox row not found");
        }
        return Map.of(
                "status", "requeued",
                "outboxId", outboxId
        );
    }
}
