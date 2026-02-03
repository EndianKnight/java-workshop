package com.bootstrap.workshop.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Wallet entity business logic.
 */
class WalletTest {

    @Nested
    @DisplayName("deposit()")
    class DepositTests {

        @Test
        @DisplayName("should add amount to balance")
        void shouldAddAmountToBalance() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(100));

            wallet.deposit(BigDecimal.valueOf(50));

            assertEquals(BigDecimal.valueOf(150), wallet.getBalance());
        }

        @Test
        @DisplayName("should handle decimal amounts")
        void shouldHandleDecimalAmounts() {
            Wallet wallet = new Wallet();
            wallet.setBalance(new BigDecimal("100.5000"));

            wallet.deposit(new BigDecimal("25.2500"));

            assertEquals(new BigDecimal("125.7500"), wallet.getBalance());
        }

        @Test
        @DisplayName("should throw exception for zero amount")
        void shouldThrowExceptionForZeroAmount() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(100));

            assertThrows(IllegalArgumentException.class, () -> wallet.deposit(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("should throw exception for negative amount")
        void shouldThrowExceptionForNegativeAmount() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(100));

            assertThrows(IllegalArgumentException.class, () -> wallet.deposit(BigDecimal.valueOf(-50)));
        }
    }

    @Nested
    @DisplayName("withdraw()")
    class WithdrawTests {

        @Test
        @DisplayName("should subtract amount from balance")
        void shouldSubtractAmountFromBalance() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(100));

            wallet.withdraw(BigDecimal.valueOf(30));

            assertEquals(BigDecimal.valueOf(70), wallet.getBalance());
        }

        @Test
        @DisplayName("should allow withdrawing entire balance")
        void shouldAllowWithdrawingEntireBalance() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(100));

            wallet.withdraw(BigDecimal.valueOf(100));

            assertEquals(BigDecimal.ZERO, wallet.getBalance());
        }

        @Test
        @DisplayName("should throw exception for insufficient balance")
        void shouldThrowExceptionForInsufficientBalance() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(50));

            assertThrows(IllegalStateException.class, () -> wallet.withdraw(BigDecimal.valueOf(100)));
        }

        @Test
        @DisplayName("should throw exception for zero amount")
        void shouldThrowExceptionForZeroAmount() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(100));

            assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("should throw exception for negative amount")
        void shouldThrowExceptionForNegativeAmount() {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.valueOf(100));

            assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(BigDecimal.valueOf(-50)));
        }
    }
}
