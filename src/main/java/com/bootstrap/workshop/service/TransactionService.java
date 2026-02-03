package com.bootstrap.workshop.service;

import com.bootstrap.workshop.dto.TransactionRequest;
import com.bootstrap.workshop.dto.TransactionResponse;
import com.bootstrap.workshop.entity.Transaction;
import com.bootstrap.workshop.entity.Wallet;
import com.bootstrap.workshop.exception.DuplicateTransactionException;
import com.bootstrap.workshop.exception.InsufficientBalanceException;
import com.bootstrap.workshop.exception.WalletNotFoundException;
import com.bootstrap.workshop.repository.TransactionRepository;
import com.bootstrap.workshop.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for transaction operations with strong consistency.
 * Uses SERIALIZABLE isolation and pessimistic locking for transfers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    /**
     * Transfer money between wallets with strong consistency.
     * Uses SERIALIZABLE isolation to prevent double-spending.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse transfer(Long fromUserId, TransactionRequest request) {
        log.info("Processing transfer: fromUserId={}, toWallet={}, amount={}, idempotencyKey={}",
                fromUserId, request.toWalletAddress(), request.amount(), request.idempotencyKey());

        // Step 1: Check idempotency - return existing transaction if duplicate
        Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(request.idempotencyKey());
        if (existing.isPresent()) {
            log.info("Duplicate transaction detected: idempotencyKey={}", request.idempotencyKey());
            return toResponse(existing.get());
        }

        // Step 2: Lock sender wallet (pessimistic locking)
        Wallet fromWallet = walletRepository.findByUserIdForUpdate(fromUserId)
                .orElseThrow(() -> new WalletNotFoundException(fromUserId));

        // Step 3: Validate recipient wallet exists
        Wallet toWallet = walletRepository.findByAddressForUpdate(request.toWalletAddress())
                .orElseThrow(() -> new WalletNotFoundException(request.toWalletAddress()));

        // Prevent self-transfer
        if (fromWallet.getAddress().equals(toWallet.getAddress())) {
            Transaction failed = createFailedTransaction(
                    fromWallet.getAddress(),
                    request.toWalletAddress(),
                    request.amount(),
                    request.idempotencyKey(),
                    "Cannot transfer to same wallet");
            return toResponse(failed);
        }

        // Step 4: Validate balance
        if (fromWallet.getBalance().compareTo(request.amount()) < 0) {
            Transaction failed = createFailedTransaction(
                    fromWallet.getAddress(),
                    request.toWalletAddress(),
                    request.amount(),
                    request.idempotencyKey(),
                    "Insufficient balance");
            log.warn("Transfer failed - insufficient balance: available={}, requested={}",
                    fromWallet.getBalance(), request.amount());
            return toResponse(failed);
        }

        // Step 5: Perform transfer
        BigDecimal senderPrevBalance = fromWallet.getBalance();
        BigDecimal receiverPrevBalance = toWallet.getBalance();

        fromWallet.withdraw(request.amount());
        toWallet.deposit(request.amount());

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // Step 6: Record successful transaction
        Transaction transaction = new Transaction(
                fromWallet.getAddress(),
                request.toWalletAddress(),
                request.amount(),
                request.idempotencyKey());
        transaction.markSuccess();
        transaction = transactionRepository.save(transaction);

        log.info("Transfer successful: txId={}, from={} ({}→{}), to={} ({}→{}), amount={}",
                transaction.getId(),
                fromWallet.getAddress(), senderPrevBalance, fromWallet.getBalance(),
                toWallet.getAddress(), receiverPrevBalance, toWallet.getBalance(),
                request.amount());

        return toResponse(transaction);
    }

    /**
     * Get transaction by ID.
     */
    @Transactional(readOnly = true)
    public TransactionResponse findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
        return toResponse(transaction);
    }

    /**
     * Get all transactions for a wallet.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> findByWalletAddress(String address) {
        return transactionRepository.findByWalletAddress(address).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get paginated transactions for a wallet.
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> findByWalletAddress(String address, Pageable pageable) {
        return transactionRepository.findByWalletAddress(address, pageable)
                .map(this::toResponse);
    }

    /**
     * Get transactions for a user by their user ID.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> findByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));
        return findByWalletAddress(wallet.getAddress());
    }

    /**
     * Create and save a failed transaction record.
     */
    private Transaction createFailedTransaction(
            String fromAddress,
            String toAddress,
            BigDecimal amount,
            String idempotencyKey,
            String errorMessage) {

        Transaction transaction = new Transaction(fromAddress, toAddress, amount, idempotencyKey);
        transaction.markFailed(errorMessage);
        return transactionRepository.save(transaction);
    }

    /**
     * Convert Transaction entity to TransactionResponse DTO.
     */
    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getFromWalletAddress(),
                transaction.getToWalletAddress(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getTimestamp(),
                transaction.getIdempotencyKey(),
                transaction.getErrorMessage());
    }
}
