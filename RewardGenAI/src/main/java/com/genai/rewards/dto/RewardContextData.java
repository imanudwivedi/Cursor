package com.genai.rewards.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Context data for reward information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardContextData {
    
    private Long totalPoints;
    private Long availablePoints;
    private Long expiredPoints;
    private LocalDate nextExpiryDate;
    private BigDecimal cashbackBalance;
    private List<RedemptionOption> redemptionOptions;
    private List<ExpiryDetail> expiringPoints;
    
    @Data
    @Builder
    public static class RedemptionOption {
        private String name;
        private String description;
        private Long pointsRequired;
        private String category;
        private BigDecimal cashValue;
        private boolean available;
    }
    
    @Data
    @Builder
    public static class ExpiryDetail {
        private Long points;
        private LocalDate expiryDate;
        private String source;
    }
} 