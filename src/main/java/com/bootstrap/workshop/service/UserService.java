package com.bootstrap.workshop.service;

import com.bootstrap.workshop.dto.UserRegistrationRequest;
import com.bootstrap.workshop.dto.UserResponse;
import com.bootstrap.workshop.dto.UserUpdateRequest;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.entity.Wallet;
import com.bootstrap.workshop.exception.UserAlreadyExistsException;
import com.bootstrap.workshop.exception.UserNotFoundException;
import com.bootstrap.workshop.repository.UserRepository;
import com.bootstrap.workshop.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;

/**
 * Service for user management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Register a new user and create their wallet.
     */
    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.email());

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        // Create user
        User user = new User(
                request.email(),
                request.name(),
                request.password(), // TODO: Hash with BCrypt in security phase
                request.bank(),
                request.accountId(),
                request.address());
        user = userRepository.save(user);

        // Create wallet with unique address
        String walletAddress = generateUniqueWalletAddress();
        Wallet wallet = new Wallet(walletAddress, user);
        wallet = walletRepository.save(wallet);

        user.setWallet(wallet);

        log.info("User registered successfully: id={}, walletAddress={}", user.getId(), walletAddress);

        return toResponse(user);
    }

    /**
     * Find user by ID.
     */
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    /**
     * Find user by email.
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return toResponse(user);
    }

    /**
     * Get all users (admin only).
     */
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Update user profile.
     */
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        log.info("Updating user: id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.bank() != null) {
            user.setBank(request.bank());
        }
        if (request.accountId() != null) {
            user.setAccountId(request.accountId());
        }
        if (request.address() != null) {
            user.setAddress(request.address());
        }

        user = userRepository.save(user);
        log.info("User updated successfully: id={}", id);

        return toResponse(user);
    }

    /**
     * Delete user and their wallet.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting user: id={}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully: id={}", id);
    }

    /**
     * Generate unique 16-character hex wallet address.
     */
    private String generateUniqueWalletAddress() {
        String address;
        do {
            byte[] bytes = new byte[8];
            secureRandom.nextBytes(bytes);
            address = HexFormat.of().formatHex(bytes);
        } while (walletRepository.existsByAddress(address));
        return address;
    }

    /**
     * Convert User entity to UserResponse DTO.
     */
    private UserResponse toResponse(User user) {
        String walletAddress = null;
        if (user.getWallet() != null) {
            walletAddress = user.getWallet().getAddress();
        } else {
            // Fetch wallet if not loaded
            walletRepository.findByUserId(user.getId())
                    .ifPresent(w -> user.setWallet(w));
            if (user.getWallet() != null) {
                walletAddress = user.getWallet().getAddress();
            }
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getBank(),
                user.getAccountId(),
                user.getAddress(),
                user.getRole(),
                user.getCreatedAt(),
                walletAddress);
    }
}
