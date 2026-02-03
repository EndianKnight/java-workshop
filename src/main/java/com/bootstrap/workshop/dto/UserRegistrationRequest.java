package com.bootstrap.workshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration request.
 */
public record UserRegistrationRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @NotBlank(message = "Name is required") String name,

        @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password,

        @NotBlank(message = "Bank is required") String bank,

        @NotBlank(message = "Account ID is required") String accountId,

        @NotBlank(message = "Address is required") String address) {
}
