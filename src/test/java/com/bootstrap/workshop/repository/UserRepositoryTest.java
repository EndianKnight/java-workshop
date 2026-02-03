package com.bootstrap.workshop.repository;

import com.bootstrap.workshop.entity.Role;
import com.bootstrap.workshop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User(
                "test@example.com",
                "Test User",
                "password123",
                "Test Bank",
                "ACC123",
                "123 Test St");
    }

    @Test
    @DisplayName("should save and retrieve user by ID")
    void shouldSaveAndRetrieveUserById() {
        User saved = userRepository.save(testUser);

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("should find user by email")
    void shouldFindUserByEmail() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    @DisplayName("should return empty for non-existent email")
    void shouldReturnEmptyForNonExistentEmail() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("existsByEmail should return true for existing email")
    void existsByEmailShouldReturnTrueForExistingEmail() {
        userRepository.save(testUser);

        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    @DisplayName("existsByEmail should return false for non-existent email")
    void existsByEmailShouldReturnFalseForNonExistentEmail() {
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("should set createdAt on persist")
    void shouldSetCreatedAtOnPersist() {
        User saved = userRepository.save(testUser);

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("should persist user role")
    void shouldPersistUserRole() {
        testUser.setRole(Role.ADMIN);
        User saved = userRepository.save(testUser);

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(Role.ADMIN, found.get().getRole());
    }
}
