package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.UserRegistrationRequest;
import com.bootstrap.workshop.dto.UserResponse;
import com.bootstrap.workshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints.
 * Handles user registration and login.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * Register a new user and create their wallet.
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration request for email: {}", request.email());
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login and obtain JWT token.
     * POST /api/v1/auth/login
     * Note: Full implementation with JWT in Phase 6 (Security)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.email());
        // TODO: Implement JWT authentication in Phase 6
        // For now, return a placeholder
        UserResponse user = userService.findByEmail(request.email());
        return ResponseEntity.ok(new LoginResponse("placeholder-token", user));
    }

    /**
     * Login request DTO.
     */
    public record LoginRequest(
            String email,
            String password) {
    }

    /**
     * Login response with JWT token.
     */
    public record LoginResponse(
            String token,
            UserResponse user) {
    }
}
