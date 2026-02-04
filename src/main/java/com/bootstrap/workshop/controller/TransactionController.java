package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.TransactionRequest;
import com.bootstrap.workshop.dto.TransactionResponse;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for transaction operations.
 * Handles transfers and transaction history.
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Transfer money to another wallet.
     * POST /api/v1/transactions
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransactionRequest request) {
        log.info("Transfer {} to {} for user: {}", request.amount(), request.toWalletAddress(), user.getId());
        TransactionResponse response = transactionService.transfer(user.getId(), request);

        if (response.status().name().equals("FAILED")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get transaction history for current user.
     * GET /api/v1/transactions
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getHistory(@AuthenticationPrincipal User user) {
        log.info("Get transaction history for user: {}", user.getId());
        List<TransactionResponse> transactions = transactionService.findByUserId(user.getId());
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transaction details by ID.
     * GET /api/v1/transactions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        log.info("Get transaction: {}", id);
        TransactionResponse response = transactionService.findById(id);
        return ResponseEntity.ok(response);
    }
}
