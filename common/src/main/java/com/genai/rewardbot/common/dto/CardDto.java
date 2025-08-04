package com.genai.rewardbot.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    
    private Long id;
    private String cardNumber;
    private String maskedCardNumber; // Last 4 digits for security
    private String cardType;
    private String vendorName;
    private String vendorCode;
    private BigDecimal cardBalance;
    private String currency;
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    
    private BigDecimal cashbackRate;
    private Integer totalRewardPoints;
    private Integer expiringSoonPoints; // Points expiring within 30 days
    private LocalDateTime nextExpiryDate;
    private List<RewardPointDto> rewardPoints;
    
    public CardDto(Long id, String cardNumber, String cardType, String vendorName, 
                   String vendorCode, BigDecimal cardBalance, String currency) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.maskedCardNumber = maskCardNumber(cardNumber);
        this.cardType = cardType;
        this.vendorName = vendorName;
        this.vendorCode = vendorCode;
        this.cardBalance = cardBalance;
        this.currency = currency;
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
} 