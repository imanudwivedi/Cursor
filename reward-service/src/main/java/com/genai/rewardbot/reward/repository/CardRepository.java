package com.genai.rewardbot.reward.repository;

import com.genai.rewardbot.common.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    List<Card> findByUserMobileNumber(String mobileNumber);
    
    List<Card> findByUserMobileNumberAndIsActive(String mobileNumber, Boolean isActive);
    
    List<Card> findByVendorCode(String vendorCode);
    
    Optional<Card> findByCardNumber(String cardNumber);
    
    @Query("SELECT c FROM Card c LEFT JOIN FETCH c.rewardPoints rp WHERE c.user.mobileNumber = :mobileNumber AND c.isActive = true")
    List<Card> findByUserMobileNumberWithRewardPoints(@Param("mobileNumber") String mobileNumber);
    
    @Query("SELECT COUNT(c) FROM Card c WHERE c.user.mobileNumber = :mobileNumber AND c.isActive = true")
    long countActiveCardsByMobileNumber(@Param("mobileNumber") String mobileNumber);
    
    @Query("SELECT DISTINCT c.vendorName FROM Card c WHERE c.user.mobileNumber = :mobileNumber AND c.isActive = true")
    List<String> findDistinctVendorsByMobileNumber(@Param("mobileNumber") String mobileNumber);
} 