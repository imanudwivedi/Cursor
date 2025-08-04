package com.genai.rewardbot.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardPointDto {
    
    private Long id;
    private Integer pointsEarned;
    private Integer pointsUsed;
    private Integer pointsAvailable;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime earningDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    
    private String source;
    private String description;
    private String transactionId;
    private Boolean isExpired;
    private Boolean isExpiringSoon; // Within 30 days
    private Double pointValue;
    private Double totalValue; // pointsAvailable * pointValue
    
    public RewardPointDto(Long id, Integer pointsEarned, Integer pointsUsed, Integer pointsAvailable,
                         LocalDateTime earningDate, LocalDateTime expiryDate, String source, 
                         String description, Double pointValue) {
        this.id = id;
        this.pointsEarned = pointsEarned;
        this.pointsUsed = pointsUsed;
        this.pointsAvailable = pointsAvailable;
        this.earningDate = earningDate;
        this.expiryDate = expiryDate;
        this.source = source;
        this.description = description;
        this.pointValue = pointValue;
        this.totalValue = pointsAvailable * (pointValue != null ? pointValue : 0.0);
        
        // Calculate if expired or expiring soon
        if (expiryDate != null) {
            LocalDateTime now = LocalDateTime.now();
            this.isExpired = expiryDate.isBefore(now);
            this.isExpiringSoon = expiryDate.isBefore(now.plusDays(30)) && !this.isExpired;
        } else {
            this.isExpired = false;
            this.isExpiringSoon = false;
        }
    }
} 