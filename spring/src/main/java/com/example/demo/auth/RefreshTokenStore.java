package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public IssuedRefreshToken issueToken(String username, String sessionLabel) {
        pruneExpiredTokens();
        String token = UUID.randomUUID().toString();
        String sessionId = UUID.randomUUID().toString();
        String tokenHash = hashToken(token);
        Instant now = clock.instant();
        Instant expiresAt = now.plusSeconds(authProperties.refreshExpirationSeconds());
        primaryJdbcTemplate.update(
                "insert into refresh_tokens(token_hash, username, expires_at, created_at, session_id, session_label) values (?, ?, ?, ?, ?, ?)",
                tokenHash,
                username,
                Timestamp.from(expiresAt),
                Timestamp.from(now),
                sessionId,
                sessionLabel
        );
        return new IssuedRefreshToken(token, sessionId);
    }

    public Optional<ConsumedRefreshToken> consumeToken(String refreshToken) {
        pruneExpiredTokens();
        Optional<ConsumedRefreshToken> stored = primaryJdbcTemplate.query(
                "select username, session_id, session_label from refresh_tokens where token_hash = ? and expires_at > ?",
                resultSet -> resultSet.next()
                        ? Optional.of(new ConsumedRefreshToken(
                        resultSet.getString("username"),
                        resultSet.getString("session_id"),
                        resultSet.getString("session_label")))
                        : Optional.empty(),
                hashToken(refreshToken),
                Timestamp.from(clock.instant())
        );
        if (stored.isPresent()) {
            primaryJdbcTemplate.update("delete from refresh_tokens where token_hash = ?", hashToken(refreshToken));
        }
        return stored;
    }

    public boolean revokeToken(String refreshToken) {
        pruneExpiredTokens();
        return primaryJdbcTemplate.update("delete from refresh_tokens where token_hash = ?", hashToken(refreshToken)) > 0;
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

    public Optional<String> findStoredTokenHash(String refreshToken) {
        return primaryJdbcTemplate.query(
                "select token_hash from refresh_tokens where token_hash = ?",
                resultSet -> resultSet.next() ? Optional.of(resultSet.getString("token_hash")) : Optional.empty(),
                hashToken(refreshToken)
        );
    }

    private void pruneExpiredTokens() {
        primaryJdbcTemplate.update("delete from refresh_tokens where expires_at <= ?", Timestamp.from(clock.instant()));
    }

    private String hashToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
