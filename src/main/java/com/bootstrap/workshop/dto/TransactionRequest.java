package com.bootstrap.workshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for transaction (transfer) request.
 */
public record TransactionRequest(
        @NotBlank(message = "Recipient wallet address is required") @Size(min = 16, max = 16, message = "Wallet address must be 16 characters") String toWalletAddress,

        @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") BigDecimal amount,

        @NotBlank(message = "Idempotency key is required") String idempotencyKey) {
}
