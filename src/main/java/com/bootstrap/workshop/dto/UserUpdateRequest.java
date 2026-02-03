package com.bootstrap.workshop.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user profile update request.
 */
public record UserUpdateRequest(
        String name,
        String bank,
        String accountId,
        String address) {
}
