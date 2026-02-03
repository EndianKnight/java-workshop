package com.bootstrap.workshop.dto;

import com.bootstrap.workshop.entity.Role;

import java.time.LocalDateTime;

/**
 * DTO for user response (without password).
 */
public record UserResponse(
        Long id,
        String email,
        String name,
        String bank,
        String accountId,
        String address,
        Role role,
        LocalDateTime createdAt,
        String walletAddress) {
}
