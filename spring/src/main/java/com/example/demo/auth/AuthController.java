package com.example.demo.auth;

import com.example.demo.security.JwtProperties;
import com.example.demo.security.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Authentication endpoints for the demo API.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenStore refreshTokenStore;
    private final UserDetailsService userDetailsService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            RefreshTokenStore refreshTokenStore,
            UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenStore = refreshTokenStore;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> createToken(
            @RequestHeader(name = "X-Session-Label", defaultValue = "default-session") String sessionLabel,
            @Valid @RequestBody TokenRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(issueTokenPair(userDetails, sessionLabel));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestHeader(name = "X-Session-Label", defaultValue = "refreshed-session") String sessionLabel,
            @Valid @RequestBody RefreshTokenRequest request) {
        ConsumedRefreshToken consumed = refreshTokenStore.consumeToken(request.refreshToken())
                .orElseThrow(() -> new BadCredentialsException("invalid refresh token"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(consumed.username());
        return ResponseEntity.ok(issueTokenPair(userDetails, sessionLabel));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody LogoutRequest request) {
        boolean revoked = refreshTokenStore.revokeToken(request.refreshToken());
        if (!revoked) {
            throw new BadCredentialsException("invalid refresh token");
        }
        return ResponseEntity.ok(Map.of("message", "refresh token revoked"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", exception.getMessage()));
    }

    private TokenResponse issueTokenPair(UserDetails userDetails, String sessionLabel) {
        String accessToken = jwtTokenService.issueToken(userDetails);
        IssuedRefreshToken refreshToken = refreshTokenStore.issueToken(userDetails.getUsername(), sessionLabel);
        return new TokenResponse(
                accessToken,
                refreshToken.token(),
                refreshToken.sessionId(),
                "Bearer",
                jwtProperties.expirationSeconds());
    }
}
