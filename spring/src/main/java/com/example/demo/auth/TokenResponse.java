package com.example.demo.auth;

/**
 * JWT token response for API clients.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String sessionId,
        String tokenType,
        long expiresInSeconds) {
}
