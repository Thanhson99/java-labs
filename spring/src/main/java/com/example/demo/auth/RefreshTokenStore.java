package com.example.demo.auth;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory refresh token store used for learning token rotation.
 */
@Service
public class RefreshTokenStore {

    private final Clock clock;
    private final AuthProperties authProperties;
    private final Map<String, StoredRefreshToken> tokens = new ConcurrentHashMap<>();

    public RefreshTokenStore(Clock clock, AuthProperties authProperties) {
        this.clock = clock;
        this.authProperties = authProperties;
    }

    public String issueToken(String username) {
        pruneExpiredTokens();
        String token = UUID.randomUUID().toString();
        Instant expiresAt = clock.instant().plusSeconds(authProperties.refreshExpirationSeconds());
        tokens.put(token, new StoredRefreshToken(username, expiresAt));
        return token;
    }

    public Optional<String> consumeToken(String refreshToken) {
        pruneExpiredTokens();
        StoredRefreshToken stored = tokens.remove(refreshToken);
        if (stored == null || stored.expiresAt().isBefore(clock.instant())) {
            return Optional.empty();
        }
        return Optional.of(stored.username());
    }

    public int activeTokenCount() {
        pruneExpiredTokens();
        return tokens.size();
    }

    private void pruneExpiredTokens() {
        Instant now = clock.instant();
        tokens.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private record StoredRefreshToken(String username, Instant expiresAt) {
    }
}
