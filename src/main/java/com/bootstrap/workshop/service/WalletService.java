package com.bootstrap.workshop.service;

import com.bootstrap.workshop.dto.WalletOperationRequest;
import com.bootstrap.workshop.dto.WalletResponse;
import com.bootstrap.workshop.entity.Wallet;
import com.bootstrap.workshop.exception.InsufficientBalanceException;
import com.bootstrap.workshop.exception.WalletNotFoundException;
import com.bootstrap.workshop.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for wallet operations with locking support.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * Get wallet balance for user.
     */
    @Transactional(readOnly = true)
    public WalletResponse getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));
        return toResponse(wallet);
    }

    /**
     * Get wallet by address.
     */
    @Transactional(readOnly = true)
    public WalletResponse getByAddress(String address) {
        Wallet wallet = walletRepository.findByAddress(address)
                .orElseThrow(() -> new WalletNotFoundException(address));
        return toResponse(wallet);
    }

    /**
     * Deposit money to wallet (with pessimistic locking).
     */
    @Transactional
    public WalletResponse deposit(Long userId, WalletOperationRequest request) {
        log.info("Processing deposit: userId={}, amount={}", userId, request.amount());

        // Acquire lock on wallet
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        BigDecimal previousBalance = wallet.getBalance();
        wallet.deposit(request.amount());
        wallet = walletRepository.save(wallet);

        log.info("Deposit successful: walletAddress={}, previousBalance={}, newBalance={}",
                wallet.getAddress(), previousBalance, wallet.getBalance());

        return toResponse(wallet);
    }

    /**
     * Withdraw money from wallet (with pessimistic locking).
     */
    @Transactional
    public WalletResponse withdraw(Long userId, WalletOperationRequest request) {
        log.info("Processing withdrawal: userId={}, amount={}", userId, request.amount());

        // Acquire lock on wallet
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        // Check balance
        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException(wallet.getBalance(), request.amount());
        }

        BigDecimal previousBalance = wallet.getBalance();
        wallet.withdraw(request.amount());
        wallet = walletRepository.save(wallet);

        log.info("Withdrawal successful: walletAddress={}, previousBalance={}, newBalance={}",
                wallet.getAddress(), previousBalance, wallet.getBalance());

        return toResponse(wallet);
    }

    /**
     * Internal method to get wallet with lock for transfer operations.
     */
    @Transactional
    public Wallet getWalletForUpdate(String address) {
        return walletRepository.findByAddressForUpdate(address)
                .orElseThrow(() -> new WalletNotFoundException(address));
    }

    /**
     * Convert Wallet entity to WalletResponse DTO.
     */
    private WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getAddress(),
                wallet.getBalance(),
                wallet.getCreatedAt());
    }
}
