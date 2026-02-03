package com.bootstrap.workshop.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Transaction entity business logic.
 */
class TransactionTest {

    @Test
    @DisplayName("should create transaction with PENDING status")
    void shouldCreateTransactionWithPendingStatus() {
        Transaction transaction = new Transaction(
                "abc123def456789a",
                "xyz789ghi012345b",
                BigDecimal.valueOf(100),
                "idempotency-key-123");

        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    @DisplayName("markSuccess() should set status to SUCCESS")
    void markSuccessShouldSetStatusToSuccess() {
        Transaction transaction = new Transaction(
                "abc123def456789a",
                "xyz789ghi012345b",
                BigDecimal.valueOf(100),
                "idempotency-key-123");

        transaction.markSuccess();

        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    @DisplayName("markFailed() should set status to FAILED and error message")
    void markFailedShouldSetStatusAndErrorMessage() {
        Transaction transaction = new Transaction(
                "abc123def456789a",
                "xyz789ghi012345b",
                BigDecimal.valueOf(100),
                "idempotency-key-123");

        transaction.markFailed("Insufficient balance");

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertEquals("Insufficient balance", transaction.getErrorMessage());
    }

    @Test
    @DisplayName("should store correct wallet addresses")
    void shouldStoreCorrectWalletAddresses() {
        String fromAddress = "abc123def456789a";
        String toAddress = "xyz789ghi012345b";

        Transaction transaction = new Transaction(
                fromAddress,
                toAddress,
                BigDecimal.valueOf(50),
                "key-123");

        assertEquals(fromAddress, transaction.getFromWalletAddress());
        assertEquals(toAddress, transaction.getToWalletAddress());
    }

    @Test
    @DisplayName("should store correct amount")
    void shouldStoreCorrectAmount() {
        BigDecimal amount = new BigDecimal("123.4567");

        Transaction transaction = new Transaction(
                "abc123def456789a",
                "xyz789ghi012345b",
                amount,
                "key-123");

        assertEquals(amount, transaction.getAmount());
    }
}
