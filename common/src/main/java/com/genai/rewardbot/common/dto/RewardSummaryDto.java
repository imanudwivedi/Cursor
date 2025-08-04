package com.genai.rewardbot.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardSummaryDto {
    
    private Integer totalPointsAcrossAllCards;
    private Integer totalPointsUsed;
    private Integer totalPointsAvailable;
    private Integer pointsExpiringSoon; // Within 30 days
    private Integer expiredPoints;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextExpiryDate;
    
    private Double totalPointsValue; // Total monetary value of available points
    private Integer numberOfActiveCards;
    private Map<String, Integer> pointsByVendor; // Vendor name -> total points
    private Map<String, Double> valueByVendor; // Vendor name -> total value
    
    // Top redemption suggestions
    private String topRedemptionSuggestion;
    private Double estimatedCashbackValue;
    
    public RewardSummaryDto(Integer totalPointsAvailable, Integer pointsExpiringSoon, 
                           Double totalPointsValue, Integer numberOfActiveCards) {
        this.totalPointsAvailable = totalPointsAvailable;
        this.pointsExpiringSoon = pointsExpiringSoon;
        this.totalPointsValue = totalPointsValue;
        this.numberOfActiveCards = numberOfActiveCards;
    }
} 