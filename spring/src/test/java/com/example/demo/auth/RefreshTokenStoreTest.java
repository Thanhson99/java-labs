package com.example.demo.auth;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenStoreTest {

    @Test
    void refreshTokenCanBeConsumedOnlyOnce() {
        RefreshTokenStore store = new RefreshTokenStore(
                Clock.fixed(Instant.parse("2026-03-12T00:00:00Z"), ZoneOffset.UTC),
                new AuthProperties(3600));

        String token = store.issueToken("student");

        assertThat(store.consumeToken(token)).contains("student");
        assertThat(store.consumeToken(token)).isEmpty();
    }

    @Test
    void expiredRefreshTokenIsRejected() {
        MutableClock clock = new MutableClock(Instant.parse("2026-03-12T00:00:00Z"));
        RefreshTokenStore store = new RefreshTokenStore(
                clock,
                new AuthProperties(1));

        String token = store.issueToken("student");
        clock.setInstant(clock.instant().plusSeconds(5));

        assertThat(store.consumeToken(token)).isEmpty();
        assertThat(store.activeTokenCount()).isZero();
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
