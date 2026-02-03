package com.bootstrap.workshop.exception;

/**
 * Exception thrown when duplicate transaction is detected (idempotency).
 */
public class DuplicateTransactionException extends RuntimeException {
    private final Long existingTransactionId;

    public DuplicateTransactionException(String idempotencyKey, Long existingTransactionId) {
        super("Transaction with idempotency key '" + idempotencyKey + "' already exists");
        this.existingTransactionId = existingTransactionId;
    }

    public Long getExistingTransactionId() {
        return existingTransactionId;
    }
}
