package com.genai.rewardbot.auth.repository;

import com.genai.rewardbot.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByMobileNumber(String mobileNumber);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards c LEFT JOIN FETCH c.rewardPoints WHERE u.mobileNumber = :mobileNumber AND u.isActive = true")
    Optional<User> findByMobileNumberWithCardsAndRewards(@Param("mobileNumber") String mobileNumber);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
} 