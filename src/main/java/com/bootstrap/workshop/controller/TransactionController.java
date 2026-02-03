package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.TransactionRequest;
import com.bootstrap.workshop.dto.TransactionResponse;
import com.bootstrap.workshop.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody TransactionRequest request) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L;
        }
        log.info("Transfer {} to {} for user: {}", request.amount(), request.toWalletAddress(), userId);
        TransactionResponse response = transactionService.transfer(userId, request);

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
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: Get userId from JWT token in Phase 6
        if (userId == null) {
            userId = 1L;
        }
        log.info("Get transaction history for user: {}", userId);
        List<TransactionResponse> transactions = transactionService.findByUserId(userId);
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
