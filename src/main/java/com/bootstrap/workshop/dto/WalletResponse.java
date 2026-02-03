package com.bootstrap.workshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for wallet response.
 */
public record WalletResponse(
        Long id,
        String address,
        BigDecimal balance,
        LocalDateTime createdAt) {
}
