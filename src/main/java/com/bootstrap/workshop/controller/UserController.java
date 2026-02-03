package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.UserResponse;
import com.bootstrap.workshop.dto.UserUpdateRequest;
import com.bootstrap.workshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user self-service endpoints.
 * Allows users to manage their own profile.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get current user's profile.
     * GET /api/v1/users/me
     * Note: User ID will come from JWT in Phase 6
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L; // Placeholder for testing
        }
        log.info("Get profile for user: {}", userId);
        UserResponse response = userService.findById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update current user's profile.
     * PUT /api/v1/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L;
        }
        log.info("Update profile for user: {}", userId);
        UserResponse response = userService.update(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete current user's account.
     * DELETE /api/v1/users/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L;
        }
        log.info("Delete account for user: {}", userId);
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
