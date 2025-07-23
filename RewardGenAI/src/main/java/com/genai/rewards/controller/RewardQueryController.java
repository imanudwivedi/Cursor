package com.genai.rewards.controller;

import com.genai.rewards.dto.QueryRequest;
import com.genai.rewards.dto.QueryResponse;
import com.genai.rewards.service.RewardQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling reward queries
 */
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RewardQueryController {

    private final RewardQueryService rewardQueryService;

    /**
     * Process a natural language reward query
     */
    @PostMapping("/query")
    public ResponseEntity<QueryResponse> processQuery(@Valid @RequestBody QueryRequest request) {
        log.info("Processing reward query for customer: {}, query: {}", 
                request.getCustomerId(), request.getQuery());
        
        long startTime = System.currentTimeMillis();
        
        try {
            QueryResponse response = rewardQueryService.processQuery(request);
            response.setResponseTimeMs(System.currentTimeMillis() - startTime);
            
            log.info("Successfully processed query for customer: {} in {}ms", 
                    request.getCustomerId(), response.getResponseTimeMs());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing query for customer: {}", request.getCustomerId(), e);
            
            QueryResponse errorResponse = QueryResponse.builder()
                    .success(false)
                    .errorMessage("Sorry, I'm having trouble processing your request right now. Please try again later.")
                    .sessionId(request.getSessionId())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();
                    
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Health check endpoint for the reward query service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Reward Query Bot is running!");
    }
} 