package com.bootstrap.workshop.service;

import com.bootstrap.workshop.dto.TransactionRequest;
import com.bootstrap.workshop.dto.TransactionResponse;
import com.bootstrap.workshop.entity.Transaction;
import com.bootstrap.workshop.entity.TransactionStatus;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.entity.Wallet;
import com.bootstrap.workshop.exception.WalletNotFoundException;
import com.bootstrap.workshop.repository.TransactionRepository;
import com.bootstrap.workshop.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService")
class TransactionServiceTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private WalletRepository walletRepository;

        @Mock
        private io.micrometer.core.instrument.MeterRegistry meterRegistry;

        @Mock
        private io.micrometer.core.instrument.MeterRegistry.Config registryConfig;

        @Mock
        private io.micrometer.core.instrument.Clock clock;

        @Mock
        private io.micrometer.core.instrument.Timer timer;

        @Mock
        private io.micrometer.core.instrument.Counter counter;

        @InjectMocks
        private TransactionService transactionService;

        private User senderUser;
        private User receiverUser;
        private Wallet senderWallet;
        private Wallet receiverWallet;
        private TransactionRequest transferRequest;

        @BeforeEach
        void setUp() {
                // Mock MeterRegistry for Timer.start()
                lenient().when(meterRegistry.config()).thenReturn(registryConfig);
                lenient().when(registryConfig.clock()).thenReturn(clock);
                lenient().when(clock.monotonicTime()).thenReturn(System.nanoTime());

                // Mock Timer and Counter
                lenient().when(meterRegistry.timer(anyString())).thenReturn(timer);
                lenient().when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(counter);

                senderUser = new User("sender@example.com", "Sender", "pass", "Bank", "ACC1", "Addr");
                senderUser.setId(1L);

                receiverUser = new User("receiver@example.com", "Receiver", "pass", "Bank", "ACC2", "Addr");
                receiverUser.setId(2L);

                senderWallet = new Wallet("sender1234567890", senderUser);
                senderWallet.setId(1L);
                senderWallet.setBalance(BigDecimal.valueOf(1000));

                receiverWallet = new Wallet("receiver12345678", receiverUser);
                receiverWallet.setId(2L);
                receiverWallet.setBalance(BigDecimal.valueOf(500));

                transferRequest = new TransactionRequest(
                                "receiver12345678",
                                BigDecimal.valueOf(200),
                                "idempotency-key-123");
        }

        @Nested
        @DisplayName("transfer()")
        class Transfer {

                @Test
                @DisplayName("should successfully transfer money between wallets")
                void shouldSuccessfullyTransferMoney() {
                        when(transactionRepository.findByIdempotencyKey("idempotency-key-123"))
                                        .thenReturn(Optional.empty());
                        when(walletRepository.findByUserIdForUpdate(1L))
                                        .thenReturn(Optional.of(senderWallet));
                        when(walletRepository.findByAddressForUpdate("receiver12345678"))
                                        .thenReturn(Optional.of(receiverWallet));
                        when(walletRepository.save(any(Wallet.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));
                        when(transactionRepository.save(any(Transaction.class)))
                                        .thenAnswer(inv -> {
                                                Transaction t = inv.getArgument(0);
                                                t.setId(1L);
                                                return t;
                                        });

                        TransactionResponse response = transactionService.transfer(1L, transferRequest);

                        assertNotNull(response);
                        assertEquals(TransactionStatus.SUCCESS, response.status());
                        assertEquals(BigDecimal.valueOf(200), response.amount());

                        // Verify balances updated
                        assertEquals(BigDecimal.valueOf(800), senderWallet.getBalance());
                        assertEquals(BigDecimal.valueOf(700), receiverWallet.getBalance());

                        verify(walletRepository, times(2)).save(any(Wallet.class));
                        verify(transactionRepository).save(any(Transaction.class));
                }

                @Test
                @DisplayName("should return existing transaction for duplicate idempotency key")
                void shouldReturnExistingTransactionForDuplicateKey() {
                        Transaction existingTransaction = new Transaction(
                                        "sender1234567890",
                                        "receiver12345678",
                                        BigDecimal.valueOf(200),
                                        "idempotency-key-123");
                        existingTransaction.setId(99L);
                        existingTransaction.markSuccess();

                        when(transactionRepository.findByIdempotencyKey("idempotency-key-123"))
                                        .thenReturn(Optional.of(existingTransaction));

                        TransactionResponse response = transactionService.transfer(1L, transferRequest);

                        assertEquals(99L, response.id());
                        assertEquals(TransactionStatus.SUCCESS, response.status());

                        // Verify no new transaction created
                        verify(walletRepository, never()).findByUserIdForUpdate(any());
                        verify(transactionRepository, never()).save(any());
                }

                @Test
                @DisplayName("should fail transfer for insufficient balance")
                void shouldFailTransferForInsufficientBalance() {
                        TransactionRequest largeRequest = new TransactionRequest(
                                        "receiver12345678",
                                        BigDecimal.valueOf(5000), // More than available
                                        "idempotency-key-456");

                        when(transactionRepository.findByIdempotencyKey("idempotency-key-456"))
                                        .thenReturn(Optional.empty());
                        when(walletRepository.findByUserIdForUpdate(1L))
                                        .thenReturn(Optional.of(senderWallet));
                        when(walletRepository.findByAddressForUpdate("receiver12345678"))
                                        .thenReturn(Optional.of(receiverWallet));
                        when(transactionRepository.save(any(Transaction.class)))
                                        .thenAnswer(inv -> {
                                                Transaction t = inv.getArgument(0);
                                                t.setId(1L);
                                                return t;
                                        });

                        TransactionResponse response = transactionService.transfer(1L, largeRequest);

                        assertEquals(TransactionStatus.FAILED, response.status());
                        assertEquals("Insufficient balance", response.errorMessage());

                        // Verify balances NOT updated
                        assertEquals(BigDecimal.valueOf(1000), senderWallet.getBalance());
                        verify(walletRepository, never()).save(any());
                }

                @Test
                @DisplayName("should fail transfer to same wallet")
                void shouldFailTransferToSameWallet() {
                        TransactionRequest selfTransfer = new TransactionRequest(
                                        "sender1234567890", // Same as sender
                                        BigDecimal.valueOf(100),
                                        "idempotency-key-789");

                        when(transactionRepository.findByIdempotencyKey("idempotency-key-789"))
                                        .thenReturn(Optional.empty());
                        when(walletRepository.findByUserIdForUpdate(1L))
                                        .thenReturn(Optional.of(senderWallet));
                        when(walletRepository.findByAddressForUpdate("sender1234567890"))
                                        .thenReturn(Optional.of(senderWallet));
                        when(transactionRepository.save(any(Transaction.class)))
                                        .thenAnswer(inv -> {
                                                Transaction t = inv.getArgument(0);
                                                t.setId(1L);
                                                return t;
                                        });

                        TransactionResponse response = transactionService.transfer(1L, selfTransfer);

                        assertEquals(TransactionStatus.FAILED, response.status());
                        assertEquals("Cannot transfer to same wallet", response.errorMessage());
                }

                @Test
                @DisplayName("should throw exception when sender wallet not found")
                void shouldThrowExceptionWhenSenderWalletNotFound() {
                        when(transactionRepository.findByIdempotencyKey(any()))
                                        .thenReturn(Optional.empty());
                        when(walletRepository.findByUserIdForUpdate(99L))
                                        .thenReturn(Optional.empty());

                        assertThrows(WalletNotFoundException.class,
                                        () -> transactionService.transfer(99L, transferRequest));
                }

                @Test
                @DisplayName("should throw exception when receiver wallet not found")
                void shouldThrowExceptionWhenReceiverWalletNotFound() {
                        when(transactionRepository.findByIdempotencyKey(any()))
                                        .thenReturn(Optional.empty());
                        when(walletRepository.findByUserIdForUpdate(1L))
                                        .thenReturn(Optional.of(senderWallet));
                        when(walletRepository.findByAddressForUpdate("receiver12345678"))
                                        .thenReturn(Optional.empty());

                        assertThrows(WalletNotFoundException.class,
                                        () -> transactionService.transfer(1L, transferRequest));
                }
        }

        @Nested
        @DisplayName("findByWalletAddress()")
        class FindByWalletAddress {

                @Test
                @DisplayName("should return transactions for wallet")
                void shouldReturnTransactionsForWallet() {
                        Transaction tx1 = new Transaction("sender1234567890", "receiver12345678",
                                        BigDecimal.valueOf(100), "key1");
                        Transaction tx2 = new Transaction("other12345678901", "sender1234567890",
                                        BigDecimal.valueOf(50), "key2");

                        when(transactionRepository.findByWalletAddress("sender1234567890"))
                                        .thenReturn(List.of(tx1, tx2));

                        List<TransactionResponse> responses = transactionService
                                        .findByWalletAddress("sender1234567890");

                        assertEquals(2, responses.size());
                }
        }

        @Nested
        @DisplayName("findByUserId()")
        class FindByUserId {

                @Test
                @DisplayName("should return transactions for user")
                void shouldReturnTransactionsForUser() {
                        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
                        when(transactionRepository.findByWalletAddress("sender1234567890"))
                                        .thenReturn(List.of());

                        List<TransactionResponse> responses = transactionService.findByUserId(1L);

                        assertNotNull(responses);
                        verify(walletRepository).findByUserId(1L);
                }

                @Test
                @DisplayName("should throw exception when wallet not found")
                void shouldThrowExceptionWhenWalletNotFound() {
                        when(walletRepository.findByUserId(99L)).thenReturn(Optional.empty());

                        assertThrows(WalletNotFoundException.class, () -> transactionService.findByUserId(99L));
                }
        }
}
