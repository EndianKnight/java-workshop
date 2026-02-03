package com.bootstrap.workshop.repository;

import com.bootstrap.workshop.entity.Transaction;
import com.bootstrap.workshop.entity.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TransactionRepository.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        testTransaction = new Transaction(
                "abc123def456789a",
                "xyz789ghi012345b",
                new BigDecimal("50.0000"),
                "idempotency-key-123");
    }

    @Test
    @DisplayName("should save and retrieve transaction by ID")
    void shouldSaveAndRetrieveTransactionById() {
        Transaction saved = transactionRepository.save(testTransaction);

        Optional<Transaction> found = transactionRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("abc123def456789a", found.get().getFromWalletAddress());
    }

    @Test
    @DisplayName("should find transaction by idempotency key")
    void shouldFindTransactionByIdempotencyKey() {
        transactionRepository.save(testTransaction);

        Optional<Transaction> found = transactionRepository.findByIdempotencyKey("idempotency-key-123");

        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("50.0000"), found.get().getAmount());
    }

    @Test
    @DisplayName("existsByIdempotencyKey should return true for existing key")
    void existsByIdempotencyKeyShouldReturnTrueForExistingKey() {
        transactionRepository.save(testTransaction);

        assertTrue(transactionRepository.existsByIdempotencyKey("idempotency-key-123"));
    }

    @Test
    @DisplayName("existsByIdempotencyKey should return false for non-existent key")
    void existsByIdempotencyKeyShouldReturnFalseForNonExistentKey() {
        assertFalse(transactionRepository.existsByIdempotencyKey("nonexistent-key"));
    }

    @Test
    @DisplayName("should find transactions by wallet address (sender)")
    void shouldFindTransactionsByWalletAddressSender() {
        transactionRepository.save(testTransaction);

        List<Transaction> found = transactionRepository.findByWalletAddress("abc123def456789a");

        assertEquals(1, found.size());
    }

    @Test
    @DisplayName("should find transactions by wallet address (receiver)")
    void shouldFindTransactionsByWalletAddressReceiver() {
        transactionRepository.save(testTransaction);

        List<Transaction> found = transactionRepository.findByWalletAddress("xyz789ghi012345b");

        assertEquals(1, found.size());
    }

    @Test
    @DisplayName("should paginate transactions by wallet address")
    void shouldPaginateTransactionsByWalletAddress() {
        for (int i = 0; i < 5; i++) {
            Transaction tx = new Transaction(
                    "abc123def456789a",
                    "xyz789ghi012345b",
                    BigDecimal.valueOf(10 + i),
                    "key-" + i);
            transactionRepository.save(tx);
        }

        Page<Transaction> page = transactionRepository.findByWalletAddress(
                "abc123def456789a",
                PageRequest.of(0, 3));

        assertEquals(3, page.getContent().size());
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    @DisplayName("should find transactions by status")
    void shouldFindTransactionsByStatus() {
        testTransaction.markSuccess();
        transactionRepository.save(testTransaction);

        Transaction pending = new Transaction(
                "def456ghi789012c",
                "xyz789ghi012345b",
                BigDecimal.valueOf(25),
                "key-pending");
        transactionRepository.save(pending);

        List<Transaction> successTx = transactionRepository.findByStatus(TransactionStatus.SUCCESS);
        List<Transaction> pendingTx = transactionRepository.findByStatus(TransactionStatus.PENDING);

        assertEquals(1, successTx.size());
        assertEquals(1, pendingTx.size());
    }

    @Test
    @DisplayName("should set timestamp on persist")
    void shouldSetTimestampOnPersist() {
        Transaction saved = transactionRepository.save(testTransaction);

        assertNotNull(saved.getTimestamp());
    }

    @Test
    @DisplayName("should find sent transactions ordered by timestamp")
    void shouldFindSentTransactionsOrderedByTimestamp() {
        for (int i = 0; i < 3; i++) {
            Transaction tx = new Transaction(
                    "abc123def456789a",
                    "xyz789ghi012345b",
                    BigDecimal.valueOf(10 + i),
                    "sent-key-" + i);
            transactionRepository.save(tx);
        }

        List<Transaction> found = transactionRepository.findByFromWalletAddressOrderByTimestampDesc("abc123def456789a");

        assertEquals(3, found.size());
    }
}
