package com.bootstrap.workshop.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when wallet has insufficient balance.
 */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(BigDecimal available, BigDecimal requested) {
        super("Insufficient balance. Available: " + available + ", Requested: " + requested);
    }
}
