package com.bootstrap.workshop.repository;

import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for WalletRepository.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User(
                "wallet@example.com",
                "Wallet User",
                "password123",
                "Test Bank",
                "ACC123",
                "123 Test St");
        testUser = userRepository.save(testUser);

        testWallet = new Wallet("abc123def456789a", testUser);
        testWallet.setBalance(new BigDecimal("100.0000"));
    }

    @Test
    @DisplayName("should save and retrieve wallet by ID")
    void shouldSaveAndRetrieveWalletById() {
        Wallet saved = walletRepository.save(testWallet);

        Optional<Wallet> found = walletRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("abc123def456789a", found.get().getAddress());
    }

    @Test
    @DisplayName("should find wallet by address")
    void shouldFindWalletByAddress() {
        walletRepository.save(testWallet);

        Optional<Wallet> found = walletRepository.findByAddress("abc123def456789a");

        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("100.0000"), found.get().getBalance());
    }

    @Test
    @DisplayName("should find wallet by user ID")
    void shouldFindWalletByUserId() {
        walletRepository.save(testWallet);

        Optional<Wallet> found = walletRepository.findByUserId(testUser.getId());

        assertTrue(found.isPresent());
        assertEquals("abc123def456789a", found.get().getAddress());
    }

    @Test
    @DisplayName("existsByAddress should return true for existing address")
    void existsByAddressShouldReturnTrueForExistingAddress() {
        walletRepository.save(testWallet);

        assertTrue(walletRepository.existsByAddress("abc123def456789a"));
    }

    @Test
    @DisplayName("existsByAddress should return false for non-existent address")
    void existsByAddressShouldReturnFalseForNonExistentAddress() {
        assertFalse(walletRepository.existsByAddress("nonexistent12345"));
    }

    @Test
    @DisplayName("should set version to 0 on initial save")
    void shouldSetVersionOnInitialSave() {
        Wallet saved = walletRepository.save(testWallet);

        assertEquals(0L, saved.getVersion());
    }

    @Test
    @DisplayName("should set createdAt on persist")
    void shouldSetCreatedAtOnPersist() {
        Wallet saved = walletRepository.save(testWallet);

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("findByAddressForUpdate should return wallet with lock")
    @org.junit.jupiter.api.Disabled("H2 doesn't support PostgreSQL FOR NO KEY UPDATE - test with PostgreSQL")
    void findByAddressForUpdateShouldReturnWalletWithLock() {
        walletRepository.save(testWallet);

        Optional<Wallet> found = walletRepository.findByAddressForUpdate("abc123def456789a");

        assertTrue(found.isPresent());
    }
}
