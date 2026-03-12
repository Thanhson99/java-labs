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

/**
 * Admin API for inspecting and replaying outbox rows during local learning.
 */
@RestController
@RequestMapping("/api/system/outbox")
public class OutboxAdminController {

    private static final int DEFAULT_PAGE_SIZE = 12;

    private final OutboxEventStore outboxEventStore;

    public OutboxAdminController(OutboxEventStore outboxEventStore) {
        this.outboxEventStore = outboxEventStore;
    }

    /**
     * Returns a compact outbox view for the admin UI.
     *
     * @return recent rows plus aggregate status counts
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public OutboxAdminResponse recentOutboxRows() {
        List<OutboxEventRecord> rows = outboxEventStore.listRecent(DEFAULT_PAGE_SIZE);
        return new OutboxAdminResponse(
                rows,
                new OutboxSummaryResponse(
                        outboxEventStore.countPending(),
                        outboxEventStore.countPublished(),
                        outboxEventStore.countDeadLetter()
                )
        );
    }

    /**
     * Moves a dead-letter row back to the pending state so the dispatcher can retry it.
     *
     * @param outboxId unique outbox row identifier
     * @return replay result payload
     */
    @PostMapping(value = "/{outboxId}/replay", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OutboxReplayResponse replayDeadLetter(@PathVariable long outboxId) {
        boolean replayed = outboxEventStore.replayDeadLetter(outboxId);
        if (!replayed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "dead-letter outbox row not found");
        }
        return new OutboxReplayResponse("requeued", outboxId);
    }

    /**
     * Summary counts displayed in the admin outbox panel.
     *
     * @param pending    rows waiting to be dispatched
     * @param published  rows already dispatched successfully
     * @param deadLetter rows that require manual intervention
     */
    record OutboxSummaryResponse(int pending, int published, int deadLetter) {
    }

    /**
     * API payload returned by the admin outbox inspection endpoint.
     *
     * @param rows    recent outbox rows ordered by newest first
     * @param summary aggregate row counts grouped by status
     */
    record OutboxAdminResponse(List<OutboxEventRecord> rows, OutboxSummaryResponse summary) {
    }

    /**
     * Result payload returned after replaying a dead-letter row.
     *
     * @param status   replay action outcome
     * @param outboxId replayed outbox row identifier
     */
    record OutboxReplayResponse(String status, long outboxId) {
    }
}
