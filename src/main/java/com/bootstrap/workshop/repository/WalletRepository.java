package com.bootstrap.workshop.repository;

import com.bootstrap.workshop.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Wallet entity operations.
 * Includes pessimistic locking for balance updates.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByAddress(String address);

    Optional<Wallet> findByUserId(Long userId);

    boolean existsByAddress(String address);

    /**
     * Find wallet by address with pessimistic write lock.
     * Use this for deposit, withdraw, and transfer operations.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.address = :address")
    Optional<Wallet> findByAddressForUpdate(@Param("address") String address);

    /**
     * Find wallet by user ID with pessimistic write lock.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdForUpdate(@Param("userId") Long userId);
}
