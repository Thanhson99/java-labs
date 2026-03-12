package com.example.demo.auth;

/**
 * Result returned when a new refresh token is issued.
 */
public record IssuedRefreshToken(String token, String sessionId) {
}
