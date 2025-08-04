package com.genai.rewardbot.reward.repository;

import com.genai.rewardbot.common.entity.RewardPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardPointRepository extends JpaRepository<RewardPoint, Long> {
    
    List<RewardPoint> findByCardUserMobileNumber(String mobileNumber);
    
    List<RewardPoint> findByCardUserMobileNumberAndIsExpired(String mobileNumber, Boolean isExpired);
    
    @Query("SELECT rp FROM RewardPoint rp WHERE rp.card.user.mobileNumber = :mobileNumber AND rp.pointsAvailable > 0 ORDER BY rp.expiryDate ASC")
    List<RewardPoint> findAvailablePointsByMobileNumber(@Param("mobileNumber") String mobileNumber);
    
    @Query("SELECT rp FROM RewardPoint rp WHERE rp.card.user.mobileNumber = :mobileNumber AND rp.expiryDate BETWEEN :startDate AND :endDate")
    List<RewardPoint> findExpiringPointsByMobileNumber(@Param("mobileNumber") String mobileNumber, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(rp.pointsAvailable) FROM RewardPoint rp WHERE rp.card.user.mobileNumber = :mobileNumber AND rp.isExpired = false")
    Integer getTotalAvailablePointsByMobileNumber(@Param("mobileNumber") String mobileNumber);
    
    @Query("SELECT SUM(rp.pointsAvailable) FROM RewardPoint rp WHERE rp.card.id = :cardId AND rp.isExpired = false")
    Integer getTotalAvailablePointsByCard(@Param("cardId") Long cardId);
    
    @Query("SELECT rp FROM RewardPoint rp WHERE rp.card.vendorCode = :vendorCode AND rp.card.user.mobileNumber = :mobileNumber AND rp.pointsAvailable > 0")
    List<RewardPoint> findAvailablePointsByVendorAndMobileNumber(@Param("vendorCode") String vendorCode, 
                                                                 @Param("mobileNumber") String mobileNumber);
} 