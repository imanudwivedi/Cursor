package com.rewards.reward.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for reward balance response - matches RewardQueryBot expectations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardBalanceResponse {

    private Long totalPoints;
    private Long availablePoints;
    private Long expiredPoints;
    private LocalDate nextExpiryDate;
} 