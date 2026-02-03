package com.bootstrap.workshop.repository;

import com.bootstrap.workshop.entity.Transaction;
import com.bootstrap.workshop.entity.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find transaction by idempotency key for duplicate detection.
     */
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    /**
     * Find all transactions for a wallet (sent or received).
     */
    @Query("SELECT t FROM Transaction t WHERE t.fromWalletAddress = :address OR t.toWalletAddress = :address ORDER BY t.timestamp DESC")
    List<Transaction> findByWalletAddress(@Param("address") String address);

    /**
     * Find all transactions for a wallet with pagination.
     */
    @Query("SELECT t FROM Transaction t WHERE t.fromWalletAddress = :address OR t.toWalletAddress = :address")
    Page<Transaction> findByWalletAddress(@Param("address") String address, Pageable pageable);

    /**
     * Find sent transactions.
     */
    List<Transaction> findByFromWalletAddressOrderByTimestampDesc(String fromWalletAddress);

    /**
     * Find received transactions.
     */
    List<Transaction> findByToWalletAddressOrderByTimestampDesc(String toWalletAddress);

    /**
     * Find transactions by status.
     */
    List<Transaction> findByStatus(TransactionStatus status);
}
