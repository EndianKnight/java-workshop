package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.UserResponse;
import com.bootstrap.workshop.dto.UserUpdateRequest;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User user) {
        log.info("Get profile for user: {}", user.getId());
        UserResponse response = userService.findById(user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Update current user's profile.
     * PUT /api/v1/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("Update profile for user: {}", user.getId());
        UserResponse response = userService.update(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete current user's account.
     * DELETE /api/v1/users/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal User user) {
        log.info("Delete account for user: {}", user.getId());
        userService.delete(user.getId());
        return ResponseEntity.noContent().build();
    }
}
