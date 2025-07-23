package com.genai.rewards.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Response DTO for reward queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryResponse {
    
    private String response;
    private String sessionId;
    private long responseTimeMs;
    private boolean success;
    private String errorMessage;
    private RewardContextData contextData;
} 