package com.bootstrap.workshop.exception;

/**
 * Exception thrown when wallet is not found.
 */
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String address) {
        super("Wallet not found with address: " + address);
    }

    public WalletNotFoundException(Long userId) {
        super("Wallet not found for user: " + userId);
    }
}
