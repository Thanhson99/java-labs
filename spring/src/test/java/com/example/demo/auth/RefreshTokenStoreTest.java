package com.example.demo.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenStoreTest {

    private MutableClock clock;
    private JdbcTemplate jdbcTemplate;
    private RefreshTokenStore refreshTokenStore;

    @BeforeEach
    void setUp() {
        org.h2.jdbcx.JdbcDataSource dataSource = new org.h2.jdbcx.JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:refresh_tokens_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("drop table if exists refresh_tokens");
        jdbcTemplate.execute("""
                create table refresh_tokens (
                    token varchar(128) primary key,
                    username varchar(64) not null,
                    expires_at timestamp not null,
                    created_at timestamp not null
                )
                """);
        clock = new MutableClock(Instant.parse("2026-03-12T00:00:00Z"));
        refreshTokenStore = new RefreshTokenStore(clock, new AuthProperties(3600), jdbcTemplate);
    }

    @Test
    void refreshTokenCanBeConsumedOnlyOnce() {
        String token = refreshTokenStore.issueToken("student");

        assertThat(refreshTokenStore.consumeToken(token)).contains("student");
        assertThat(refreshTokenStore.consumeToken(token)).isEmpty();
    }

    @Test
    void expiredRefreshTokenIsRejected() {
        RefreshTokenStore shortLivedStore = new RefreshTokenStore(clock, new AuthProperties(1), jdbcTemplate);
        String token = shortLivedStore.issueToken("student");

        clock.setInstant(clock.instant().plusSeconds(5));

        assertThat(shortLivedStore.consumeToken(token)).isEmpty();
        assertThat(shortLivedStore.activeTokenCount()).isZero();
    }

    @Test
    void revokeTokenDeletesItFromTheDatabase() {
        String token = refreshTokenStore.issueToken("student");

        assertThat(refreshTokenStore.revokeToken(token)).isTrue();
        assertThat(refreshTokenStore.activeTokenCountForUser("student")).isZero();
    }

    @Test
    void activeCountTracksPersistedTokens() {
        refreshTokenStore.issueToken("student");
        refreshTokenStore.issueToken("student");
        jdbcTemplate.update(
                "insert into refresh_tokens(token, username, expires_at, created_at) values (?, ?, ?, ?)",
                "legacy-token",
                "admin",
                Timestamp.from(clock.instant().plusSeconds(60)),
                Timestamp.from(clock.instant())
        );

        assertThat(refreshTokenStore.activeTokenCount()).isEqualTo(3);
        assertThat(refreshTokenStore.activeTokenCountForUser("student")).isEqualTo(2);
    }

    private static final class MutableClock extends Clock {
        private final AtomicReference<Instant> current;

        private MutableClock(Instant initialInstant) {
            this.current = new AtomicReference<>(initialInstant);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return current.get();
        }

        void setInstant(Instant instant) {
            current.set(instant);
        }
    }
}
