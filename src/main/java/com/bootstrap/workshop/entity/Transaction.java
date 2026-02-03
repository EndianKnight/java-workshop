package com.bootstrap.workshop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction entity for recording wallet transfers.
 * Includes idempotencyKey for safe retries.
 */
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_from_wallet", columnList = "from_wallet_address"),
        @Index(name = "idx_transaction_to_wallet", columnList = "to_wallet_address"),
        @Index(name = "idx_transaction_idempotency", columnList = "idempotency_key", unique = true)
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_wallet_address", nullable = false, length = 16)
    private String fromWalletAddress;

    @Column(name = "to_wallet_address", nullable = false, length = 16)
    private String toWalletAddress;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructors
    public Transaction() {
    }

    public Transaction(String fromWalletAddress, String toWalletAddress, BigDecimal amount, String idempotencyKey) {
        this.fromWalletAddress = fromWalletAddress;
        this.toWalletAddress = toWalletAddress;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.status = TransactionStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromWalletAddress() {
        return fromWalletAddress;
    }

    public void setFromWalletAddress(String fromWalletAddress) {
        this.fromWalletAddress = fromWalletAddress;
    }

    public String getToWalletAddress() {
        return toWalletAddress;
    }

    public void setToWalletAddress(String toWalletAddress) {
        this.toWalletAddress = toWalletAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Business methods
    public void markSuccess() {
        this.status = TransactionStatus.SUCCESS;
    }

    public void markFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.errorMessage = reason;
    }
}
