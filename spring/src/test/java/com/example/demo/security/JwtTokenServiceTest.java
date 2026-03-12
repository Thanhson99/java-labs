package com.example.demo.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenServiceTest {

    @Test
    void issuedTokenContainsSubjectAndRoles() {
        JwtTokenService service = new JwtTokenService(
                Clock.fixed(Instant.parse("2026-03-12T00:00:00Z"), ZoneOffset.UTC),
                new JwtProperties("01234567890123456789012345678901", 3600));
        UserDetails user = User.withUsername("student")
                .password("ignored")
                .roles("USER")
                .build();

        String token = service.issueToken(user);

        assertThat(service.parseToken(token).getSubject()).isEqualTo("student");
        assertThat(service.parseToken(token).get("roles", java.util.List.class)).contains("ROLE_USER");
    }

    @Test
    void expiredTokenIsRejected() {
        Instant issuedAt = Instant.parse("2026-03-12T00:00:00Z");
        JwtTokenService issuer = new JwtTokenService(
                Clock.fixed(issuedAt, ZoneOffset.UTC),
                new JwtProperties("01234567890123456789012345678901", 1));
        JwtTokenService validator = new JwtTokenService(
                Clock.fixed(issuedAt.plusSeconds(5), ZoneOffset.UTC),
                new JwtProperties("01234567890123456789012345678901", 1));
        UserDetails user = User.withUsername("student")
                .password("ignored")
                .roles("USER")
                .build();

        String token = issuer.issueToken(user);

        assertThatThrownBy(() -> validator.parseToken(token)).isInstanceOf(JwtException.class);
    }
}
