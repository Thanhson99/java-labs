package com.example.demo.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request used to exchange a valid refresh token for a new access token pair.
 */
public record RefreshTokenRequest(@NotBlank String refreshToken) {
}
