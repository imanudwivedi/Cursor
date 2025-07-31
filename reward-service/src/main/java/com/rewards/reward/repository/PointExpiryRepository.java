package com.rewards.reward.repository;

import com.rewards.reward.entity.PointExpiry;
import com.rewards.reward.entity.RewardAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for PointExpiry entity operations
 */
@Repository
public interface PointExpiryRepository extends JpaRepository<PointExpiry, Long> {

    /**
     * Find expiring points by customer ID
     */
    List<PointExpiry> findByRewardAccountCustomerIdAndStatus(String customerId, PointExpiry.ExpiryStatus status);

    /**
     * Find points expiring within date range
     */
    @Query("SELECT pe FROM PointExpiry pe WHERE pe.rewardAccount.customerId = :customerId " +
           "AND pe.expiryDate BETWEEN :startDate AND :endDate AND pe.status = 'ACTIVE'")
    List<PointExpiry> findPointsExpiringBetween(@Param("customerId") String customerId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * Find points expiring soon (within 30 days)
     */
    @Query("SELECT pe FROM PointExpiry pe WHERE pe.rewardAccount.customerId = :customerId " +
           "AND pe.expiryDate <= :date AND pe.status = 'ACTIVE' ORDER BY pe.expiryDate")
    List<PointExpiry> findPointsExpiringSoon(@Param("customerId") String customerId, @Param("date") LocalDate date);

    /**
     * Get next expiry date for customer
     */
    @Query("SELECT MIN(pe.expiryDate) FROM PointExpiry pe WHERE pe.rewardAccount.customerId = :customerId " +
           "AND pe.status = 'ACTIVE'")
    LocalDate getNextExpiryDate(@Param("customerId") String customerId);

    /**
     * Sum points by expiry status
     */
    @Query("SELECT COALESCE(SUM(pe.points), 0) FROM PointExpiry pe WHERE pe.rewardAccount.customerId = :customerId " +
           "AND pe.status = :status")
    Long sumPointsByStatus(@Param("customerId") String customerId, @Param("status") PointExpiry.ExpiryStatus status);
} 