package com.example.demo.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Stores integration events in the primary database so publish work can happen
 * after the main transaction commits.
 */
@Component
public class OutboxEventStore {

    private final JdbcTemplate primaryJdbcTemplate;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public OutboxEventStore(@Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate,
                            ObjectMapper objectMapper,
                            Clock clock) {
        this.primaryJdbcTemplate = primaryJdbcTemplate;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public void enqueueUserRegistered(UserRegisteredEvent event) {
        primaryJdbcTemplate.update("""
                        insert into outbox_events(
                            aggregate_type,
                            aggregate_id,
                            event_type,
                            payload,
                            status,
                            attempts,
                            available_at,
                            created_at
                        ) values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                "USER",
                event.userId(),
                "USER_REGISTERED",
                serialize(event),
                "PENDING",
                0,
                Timestamp.from(clock.instant()),
                Timestamp.from(clock.instant()));
    }

    public List<OutboxEventRecord> fetchReadyBatch(int batchSize) {
        return primaryJdbcTemplate.query("""
                        select id, aggregate_type, aggregate_id, event_type, payload, status,
                               attempts, available_at, created_at, published_at, last_error
                        from outbox_events
                        where status = 'PENDING' and available_at <= ?
                        order by id
                        limit ?
                        """,
                (rs, rowNum) -> mapRecord(rs),
                Timestamp.from(clock.instant()),
                batchSize);
    }

    public void markPublished(long outboxId) {
        primaryJdbcTemplate.update("""
                        update outbox_events
                        set status = 'PUBLISHED',
                            published_at = ?,
                            last_error = null
                        where id = ?
                        """,
                Timestamp.from(clock.instant()),
                outboxId);
    }

    public void reschedule(long outboxId, String errorMessage, long retryDelayMillis) {
        primaryJdbcTemplate.update("""
                        update outbox_events
                        set attempts = attempts + 1,
                            last_error = ?,
                            available_at = ?
                        where id = ?
                        """,
                truncate(errorMessage),
                Timestamp.from(clock.instant().plusMillis(retryDelayMillis)),
                outboxId);
    }

    public void markDeadLetter(long outboxId, String errorMessage) {
        primaryJdbcTemplate.update("""
                        update outbox_events
                        set attempts = attempts + 1,
                            status = 'DEAD_LETTER',
                            last_error = ?
                        where id = ?
                        """,
                truncate(errorMessage),
                outboxId);
    }

    public int countPending() {
        Integer count = primaryJdbcTemplate.queryForObject(
                "select count(*) from outbox_events where status = 'PENDING'",
                Integer.class);
        return count == null ? 0 : count;
    }

    public int countDeadLetter() {
        Integer count = primaryJdbcTemplate.queryForObject(
                "select count(*) from outbox_events where status = 'DEAD_LETTER'",
                Integer.class);
        return count == null ? 0 : count;
    }

    public int countPublished() {
        Integer count = primaryJdbcTemplate.queryForObject(
                "select count(*) from outbox_events where status = 'PUBLISHED'",
                Integer.class);
        return count == null ? 0 : count;
    }

    public List<OutboxEventRecord> listRecent(int limit) {
        return primaryJdbcTemplate.query("""
                        select id, aggregate_type, aggregate_id, event_type, payload, status,
                               attempts, available_at, created_at, published_at, last_error
                        from outbox_events
                        order by id desc
                        limit ?
                        """,
                (rs, rowNum) -> mapRecord(rs),
                limit);
    }

    public boolean replayDeadLetter(long outboxId) {
        int updated = primaryJdbcTemplate.update("""
                        update outbox_events
                        set status = 'PENDING',
                            attempts = 0,
                            available_at = ?,
                            published_at = null,
                            last_error = null
                        where id = ? and status = 'DEAD_LETTER'
                        """,
                Timestamp.from(clock.instant()),
                outboxId);
        return updated > 0;
    }

    public UserRegisteredEvent deserializeUserRegistered(String payload) {
        try {
            return objectMapper.readValue(payload, UserRegisteredEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("could not read outbox payload", exception);
        }
    }

    private String serialize(UserRegisteredEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("could not serialize outbox payload", exception);
        }
    }

    private OutboxEventRecord mapRecord(ResultSet resultSet) throws SQLException {
        return new OutboxEventRecord(
                resultSet.getLong("id"),
                resultSet.getString("aggregate_type"),
                resultSet.getString("aggregate_id"),
                resultSet.getString("event_type"),
                resultSet.getString("payload"),
                resultSet.getString("status"),
                resultSet.getInt("attempts"),
                toInstant(resultSet.getTimestamp("available_at")),
                toInstant(resultSet.getTimestamp("created_at")),
                toInstant(resultSet.getTimestamp("published_at")),
                resultSet.getString("last_error")
        );
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private String truncate(String value) {
        if (value == null) {
            return "unknown error";
        }
        return value.length() <= 1000 ? value : value.substring(0, 1000);
    }
}
