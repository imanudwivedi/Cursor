package com.rewards.reward.repository;

import com.rewards.reward.entity.RewardAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RewardAccount entity operations
 */
@Repository
public interface RewardAccountRepository extends JpaRepository<RewardAccount, String> {

    /**
     * Find reward account with expiring points
     */
    @Query("SELECT ra FROM RewardAccount ra LEFT JOIN FETCH ra.expiringPoints WHERE ra.customerId = :customerId")
    Optional<RewardAccount> findByIdWithExpiringPoints(@Param("customerId") String customerId);

    /**
     * Find reward account with transactions
     */
    @Query("SELECT ra FROM RewardAccount ra LEFT JOIN FETCH ra.transactions WHERE ra.customerId = :customerId")
    Optional<RewardAccount> findByIdWithTransactions(@Param("customerId") String customerId);

    /**
     * Find accounts by tier level
     */
    List<RewardAccount> findByTierLevel(RewardAccount.TierLevel tierLevel);

    /**
     * Find accounts with points expiring before date
     */
    @Query("SELECT ra FROM RewardAccount ra WHERE ra.nextExpiryDate <= :date")
    List<RewardAccount> findAccountsWithExpiringPointsBefore(@Param("date") LocalDate date);

    /**
     * Count total active accounts
     */
    @Query("SELECT COUNT(ra) FROM RewardAccount ra WHERE ra.availablePoints > 0")
    long countActiveAccounts();

    /**
     * Sum total points across all accounts
     */
    @Query("SELECT COALESCE(SUM(ra.availablePoints), 0) FROM RewardAccount ra")
    Long getTotalPointsAcrossAllAccounts();
} 