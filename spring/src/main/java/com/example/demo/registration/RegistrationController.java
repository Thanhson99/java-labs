package com.example.demo.registration;

import com.example.demo.profile.UserProfileRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST API for working with user registration and profile lookup.
 */
@RestController
@RequestMapping("/api/users")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserProfileRepository userProfileRepository;

    public RegistrationController(RegistrationService registrationService, UserProfileRepository userProfileRepository) {
        this.registrationService = registrationService;
        this.userProfileRepository = userProfileRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResult> register(
            @RequestHeader(name = "X-Caller-Key", defaultValue = "demo-caller") String callerKey,
            @Valid @RequestBody RegisterUserRequest request) {
        RegistrationResult result = registrationService.register(callerKey, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/register-demo")
    public ResponseEntity<RegistrationResult> registerWithRollbackDemo(
            @RequestHeader(name = "X-Caller-Key", defaultValue = "demo-caller") String callerKey,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "false") boolean failAfterAudit,
            @Valid @RequestBody RegisterUserRequest request) {
        RegistrationResult result = registrationService.registerWithRollbackDemo(callerKey, request, failAfterAudit);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUser(@PathVariable String userId) {
        return userProfileRepository.findById(userId)
                .map(UserProfileResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleRateLimit(RateLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", exception.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", exception.getMessage()));
    }
}
