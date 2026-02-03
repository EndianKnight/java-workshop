package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.UserRegistrationRequest;
import com.bootstrap.workshop.dto.UserResponse;
import com.bootstrap.workshop.dto.UserUpdateRequest;
import com.bootstrap.workshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for admin user management.
 * Full CRUD on all users (admin only).
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    /**
     * List all users.
     * GET /api/v1/admin/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        log.info("Admin: listing all users");
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID.
     * GET /api/v1/admin/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        log.info("Admin: getting user {}", id);
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Create a new user.
     * POST /api/v1/admin/users
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Admin: creating user with email {}", request.email());
        UserResponse user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Update user by ID.
     * PUT /api/v1/admin/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("Admin: updating user {}", id);
        UserResponse user = userService.update(id, request);
        return ResponseEntity.ok(user);
    }

    /**
     * Delete user by ID.
     * DELETE /api/v1/admin/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Admin: deleting user {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
