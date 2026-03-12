package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Database-backed refresh token store used for learning token rotation and revocation.
 */
@Service
public class RefreshTokenStore {

    private final Clock clock;
    private final AuthProperties authProperties;
    private final JdbcTemplate primaryJdbcTemplate;

    public RefreshTokenStore(
            Clock clock,
            AuthProperties authProperties,
            @Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate) {
        this.clock = clock;
        this.authProperties = authProperties;
        this.primaryJdbcTemplate = primaryJdbcTemplate;
    }

    public String issueToken(String username) {
        pruneExpiredTokens();
        String token = UUID.randomUUID().toString();
        Instant now = clock.instant();
        Instant expiresAt = now.plusSeconds(authProperties.refreshExpirationSeconds());
        primaryJdbcTemplate.update(
                "insert into refresh_tokens(token, username, expires_at, created_at) values (?, ?, ?, ?)",
                token,
                username,
                Timestamp.from(expiresAt),
                Timestamp.from(now)
        );
        return token;
    }

    public Optional<String> consumeToken(String refreshToken) {
        pruneExpiredTokens();
        Optional<String> username = primaryJdbcTemplate.query(
                "select username from refresh_tokens where token = ? and expires_at > ?",
                resultSet -> resultSet.next() ? Optional.of(resultSet.getString("username")) : Optional.empty(),
                refreshToken,
                Timestamp.from(clock.instant())
        );
        if (username.isPresent()) {
            primaryJdbcTemplate.update("delete from refresh_tokens where token = ?", refreshToken);
        }
        return username;
    }

    public boolean revokeToken(String refreshToken) {
        pruneExpiredTokens();
        return primaryJdbcTemplate.update("delete from refresh_tokens where token = ?", refreshToken) > 0;
    }

    public int activeTokenCount() {
        pruneExpiredTokens();
        Integer count = primaryJdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens where expires_at > ?",
                Integer.class,
                Timestamp.from(clock.instant())
        );
        return count == null ? 0 : count;
    }

    public int activeTokenCountForUser(String username) {
        pruneExpiredTokens();
        Integer count = primaryJdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens where username = ? and expires_at > ?",
                Integer.class,
                username,
                Timestamp.from(clock.instant())
        );
        return count == null ? 0 : count;
    }

    private void pruneExpiredTokens() {
        primaryJdbcTemplate.update("delete from refresh_tokens where expires_at <= ?", Timestamp.from(clock.instant()));
    }
}
