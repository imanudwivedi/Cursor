package com.rewards.reward.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for expiring points response - matches RewardQueryBot expectations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringPointsResponse {

    private Long points;
    private LocalDate expiryDate;
    private String source;
} 