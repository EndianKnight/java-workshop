package com.bootstrap.workshop.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity.
 */
class UserTest {

    @Test
    @DisplayName("should create user with default USER role")
    void shouldCreateUserWithDefaultRole() {
        User user = new User(
                "test@example.com",
                "Test User",
                "password123",
                "Test Bank",
                "ACC123",
                "123 Test St");

        assertEquals(Role.USER, user.getRole());
    }

    @Test
    @DisplayName("should set all constructor fields correctly")
    void shouldSetAllConstructorFieldsCorrectly() {
        User user = new User(
                "john@example.com",
                "John Doe",
                "securePassword",
                "Bank of Test",
                "ACC456",
                "456 Main St");

        assertEquals("john@example.com", user.getEmail());
        assertEquals("John Doe", user.getName());
        assertEquals("securePassword", user.getPassword());
        assertEquals("Bank of Test", user.getBank());
        assertEquals("ACC456", user.getAccountId());
        assertEquals("456 Main St", user.getAddress());
    }

    @Test
    @DisplayName("should allow changing role to ADMIN")
    void shouldAllowChangingRoleToAdmin() {
        User user = new User();
        user.setRole(Role.ADMIN);

        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("should set wallet relationship")
    void shouldSetWalletRelationship() {
        User user = new User();
        Wallet wallet = new Wallet("abc123def456789a", user);

        user.setWallet(wallet);

        assertNotNull(user.getWallet());
        assertEquals(wallet, user.getWallet());
    }
}
