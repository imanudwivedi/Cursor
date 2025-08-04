package com.genai.rewardbot.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenAiQueryDto {
    
    private String mobileNumber;
    private String query;
    private String intent; // POINTS_BALANCE, EXPIRY_CHECK, CASHBACK_OPTIONS, REDEMPTION_SUGGESTIONS
    private String response;
    private Boolean isSuccessful;
    private String errorMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime queryTime;
    
    // Additional context for the response
    private UserResponseDto userContext;
    private String suggestedActions;
    
    public GenAiQueryDto(String mobileNumber, String query) {
        this.mobileNumber = mobileNumber;
        this.query = query;
        this.queryTime = LocalDateTime.now();
    }
} 