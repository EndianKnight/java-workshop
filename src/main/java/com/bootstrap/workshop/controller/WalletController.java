package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.WalletOperationRequest;
import com.bootstrap.workshop.dto.WalletResponse;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<WalletResponse> getBalance(@AuthenticationPrincipal User user) {
        log.info("Get wallet balance for user: {}", user.getId());
        WalletResponse response = walletService.getBalance(user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Deposit money to wallet.
     * POST /api/v1/wallet/deposit
     */
    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WalletOperationRequest request) {
        log.info("Deposit {} for user: {}", request.amount(), user.getId());
        WalletResponse response = walletService.deposit(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * Withdraw money from wallet.
     * POST /api/v1/wallet/withdraw
     */
    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WalletOperationRequest request) {
        log.info("Withdraw {} for user: {}", request.amount(), user.getId());
        WalletResponse response = walletService.withdraw(user.getId(), request);
        return ResponseEntity.ok(response);
    }
}
