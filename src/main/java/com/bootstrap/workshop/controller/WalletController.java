package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.WalletOperationRequest;
import com.bootstrap.workshop.dto.WalletResponse;
import com.bootstrap.workshop.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for wallet operations.
 * Handles balance, deposit, and withdraw.
 */
@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    /**
     * Get current user's wallet balance.
     * GET /api/v1/wallet
     */
    @GetMapping
    public ResponseEntity<WalletResponse> getBalance(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L;
        }
        log.info("Get wallet balance for user: {}", userId);
        WalletResponse response = walletService.getBalance(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deposit money to wallet.
     * POST /api/v1/wallet/deposit
     */
    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody WalletOperationRequest request) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L;
        }
        log.info("Deposit {} for user: {}", request.amount(), userId);
        WalletResponse response = walletService.deposit(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Withdraw money from wallet.
     * POST /api/v1/wallet/withdraw
     */
    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody WalletOperationRequest request) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L;
        }
        log.info("Withdraw {} for user: {}", request.amount(), userId);
        WalletResponse response = walletService.withdraw(userId, request);
        return ResponseEntity.ok(response);
    }
}
