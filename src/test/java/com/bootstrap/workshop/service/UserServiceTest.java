package com.bootstrap.workshop.service;

import com.bootstrap.workshop.dto.UserRegistrationRequest;
import com.bootstrap.workshop.dto.UserResponse;
import com.bootstrap.workshop.dto.UserUpdateRequest;
import com.bootstrap.workshop.entity.Role;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.entity.Wallet;
import com.bootstrap.workshop.exception.UserAlreadyExistsException;
import com.bootstrap.workshop.exception.UserNotFoundException;
import com.bootstrap.workshop.repository.UserRepository;
import com.bootstrap.workshop.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private io.micrometer.core.instrument.MeterRegistry meterRegistry;

    @Mock
    private io.micrometer.core.instrument.Counter counter;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest registrationRequest;
    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        lenient().when(meterRegistry.counter(anyString())).thenReturn(counter);
        lenient().when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(counter);

        registrationRequest = new UserRegistrationRequest(
                "test@example.com",
                "Test User",
                "password123",
                "Test Bank",
                "ACC123",
                "123 Test St");

        testUser = new User(
                "test@example.com",
                "Test User",
                "password123",
                "Test Bank",
                "ACC123",
                "123 Test St");
        testUser.setId(1L);

        testWallet = new Wallet("abc123def4567890", testUser);
        testWallet.setId(1L);
        testWallet.setBalance(BigDecimal.ZERO);
        testUser.setWallet(testWallet);
    }

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("should register new user and create wallet")
        void shouldRegisterNewUserAndCreateWallet() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(walletRepository.existsByAddress(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

            UserResponse response = userService.register(registrationRequest);

            assertNotNull(response);
            assertEquals("test@example.com", response.email());
            assertEquals("Test User", response.name());
            assertEquals(Role.USER, response.role());
            assertNotNull(response.walletAddress());
            assertEquals(16, response.walletAddress().length());

            verify(userRepository).save(any(User.class));
            verify(walletRepository).save(any(Wallet.class));
        }

        @Test
        @DisplayName("should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> userService.register(registrationRequest));

            verify(userRepository, never()).save(any());
            verify(walletRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUserWhenFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserResponse response = userService.findById(1L);

            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("test@example.com", response.email());
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowExceptionWhenNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("should return all users")
        void shouldReturnAllUsers() {
            User user2 = new User("user2@example.com", "User 2", "pass", "Bank", "ACC", "Addr");
            user2.setId(2L);

            when(userRepository.findAll()).thenReturn(List.of(testUser, user2));
            when(walletRepository.findByUserId(2L)).thenReturn(Optional.empty());

            List<UserResponse> responses = userService.findAll();

            assertEquals(2, responses.size());
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("should update user fields")
        void shouldUpdateUserFields() {
            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    "Updated Name",
                    "New Bank",
                    "NEW123",
                    "456 New St");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            UserResponse response = userService.update(1L, updateRequest);

            assertEquals("Updated Name", response.name());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.update(99L, new UserUpdateRequest(null, null, null, null)));
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete existing user")
        void shouldDeleteExistingUser() {
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            assertDoesNotThrow(() -> userService.delete(1L));

            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.existsById(99L)).thenReturn(false);

            assertThrows(UserNotFoundException.class, () -> userService.delete(99L));

            verify(userRepository, never()).deleteById(any());
        }
    }
}
