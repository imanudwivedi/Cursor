package com.genai.rewards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request DTO for reward queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    
    @NotBlank(message = "Query cannot be empty")
    @Size(max = 500, message = "Query must not exceed 500 characters")
    private String query;
    
    @NotNull(message = "Customer ID is required")
    private String customerId;
    
    private String sessionId;
    
    private String userType = "CUSTOMER"; // CUSTOMER or AGENT
} 