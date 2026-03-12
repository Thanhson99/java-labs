package com.example.demo.audit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Writes audit-style records to the primary database inside the same transaction as registration.
 */
@Component
public class RegistrationAuditStore {

    private final JdbcTemplate primaryJdbcTemplate;

    public RegistrationAuditStore(@Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate) {
        this.primaryJdbcTemplate = primaryJdbcTemplate;
    }

    /**
     * Stores an audit row in the primary database.
     *
     * @param userId related user identifier
     * @param action action description
     */
    public void record(String userId, String action) {
        primaryJdbcTemplate.update(
                "insert into registration_audit(user_id, action) values (?, ?)",
                userId,
                action
        );
    }

    public int countByUserId(String userId) {
        Integer count = primaryJdbcTemplate.queryForObject(
                "select count(*) from registration_audit where user_id = ?",
                Integer.class,
                userId
        );
        return count == null ? 0 : count;
    }
}
