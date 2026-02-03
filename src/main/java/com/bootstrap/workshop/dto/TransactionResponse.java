package com.bootstrap.workshop.dto;

import com.bootstrap.workshop.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for transaction response.
 */
public record TransactionResponse(
        Long id,
        String fromWalletAddress,
        String toWalletAddress,
        BigDecimal amount,
        TransactionStatus status,
        LocalDateTime timestamp,
        String idempotencyKey,
        String errorMessage) {
}
