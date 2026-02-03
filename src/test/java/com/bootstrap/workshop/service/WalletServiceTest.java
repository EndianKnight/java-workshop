package com.bootstrap.workshop.service;

import com.bootstrap.workshop.dto.WalletOperationRequest;
import com.bootstrap.workshop.dto.WalletResponse;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.entity.Wallet;
import com.bootstrap.workshop.exception.InsufficientBalanceException;
import com.bootstrap.workshop.exception.WalletNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WalletService")
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "Test", "pass", "Bank", "ACC", "Addr");
        testUser.setId(1L);

        testWallet = new Wallet("abc123def4567890", testUser);
        testWallet.setId(1L);
        testWallet.setBalance(BigDecimal.valueOf(1000));
    }

    @Nested
    @DisplayName("getBalance()")
    class GetBalance {

        @Test
        @DisplayName("should return wallet balance")
        void shouldReturnWalletBalance() {
            when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));

            WalletResponse response = walletService.getBalance(1L);

            assertNotNull(response);
            assertEquals("abc123def4567890", response.address());
            assertEquals(BigDecimal.valueOf(1000), response.balance());
        }

        @Test
        @DisplayName("should throw exception when wallet not found")
        void shouldThrowExceptionWhenWalletNotFound() {
            when(walletRepository.findByUserId(99L)).thenReturn(Optional.empty());

            assertThrows(WalletNotFoundException.class, () -> walletService.getBalance(99L));
        }
    }

    @Nested
    @DisplayName("deposit()")
    class Deposit {

        @Test
        @DisplayName("should deposit amount to wallet")
        void shouldDepositAmountToWallet() {
            WalletOperationRequest request = new WalletOperationRequest(BigDecimal.valueOf(500));

            when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
            when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

            WalletResponse response = walletService.deposit(1L, request);

            assertEquals(BigDecimal.valueOf(1500), response.balance());
            verify(walletRepository).save(any(Wallet.class));
        }

        @Test
        @DisplayName("should throw exception when wallet not found")
        void shouldThrowExceptionWhenWalletNotFound() {
            when(walletRepository.findByUserIdForUpdate(99L)).thenReturn(Optional.empty());

            assertThrows(WalletNotFoundException.class,
                    () -> walletService.deposit(99L, new WalletOperationRequest(BigDecimal.TEN)));
        }
    }

    @Nested
    @DisplayName("withdraw()")
    class Withdraw {

        @Test
        @DisplayName("should withdraw amount from wallet")
        void shouldWithdrawAmountFromWallet() {
            WalletOperationRequest request = new WalletOperationRequest(BigDecimal.valueOf(300));

            when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
            when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

            WalletResponse response = walletService.withdraw(1L, request);

            assertEquals(BigDecimal.valueOf(700), response.balance());
            verify(walletRepository).save(any(Wallet.class));
        }

        @Test
        @DisplayName("should throw exception for insufficient balance")
        void shouldThrowExceptionForInsufficientBalance() {
            WalletOperationRequest request = new WalletOperationRequest(BigDecimal.valueOf(5000));

            when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));

            assertThrows(InsufficientBalanceException.class, () -> walletService.withdraw(1L, request));

            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when wallet not found")
        void shouldThrowExceptionWhenWalletNotFound() {
            when(walletRepository.findByUserIdForUpdate(99L)).thenReturn(Optional.empty());

            assertThrows(WalletNotFoundException.class,
                    () -> walletService.withdraw(99L, new WalletOperationRequest(BigDecimal.TEN)));
        }
    }

    @Nested
    @DisplayName("getByAddress()")
    class GetByAddress {

        @Test
        @DisplayName("should return wallet by address")
        void shouldReturnWalletByAddress() {
            when(walletRepository.findByAddress("abc123def4567890")).thenReturn(Optional.of(testWallet));

            WalletResponse response = walletService.getByAddress("abc123def4567890");

            assertNotNull(response);
            assertEquals("abc123def4567890", response.address());
        }

        @Test
        @DisplayName("should throw exception when wallet not found")
        void shouldThrowExceptionWhenWalletNotFound() {
            when(walletRepository.findByAddress("invalid")).thenReturn(Optional.empty());

            assertThrows(WalletNotFoundException.class, () -> walletService.getByAddress("invalid"));
        }
    }
}
