package com.example.demo.auth;

/**
 * Result returned when a refresh token is consumed.
 */
public record ConsumedRefreshToken(String username, String sessionId, String sessionLabel) {
}
