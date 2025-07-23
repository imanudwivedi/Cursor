package com.genai.rewards.service;

import com.genai.rewards.dto.QueryRequest;
import com.genai.rewards.dto.QueryResponse;
import com.genai.rewards.dto.RewardContextData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Main service for processing reward queries using AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RewardQueryService {

    private final RewardDataService rewardDataService;
    private final AIService aiService;
    private final QueryAnalysisService queryAnalysisService;

    /**
     * Process a natural language query about rewards
     */
    public QueryResponse processQuery(QueryRequest request) {
        log.debug("Processing query: {}", request.getQuery());

        try {
            // Generate session ID if not provided
            String sessionId = request.getSessionId() != null ? 
                    request.getSessionId() : UUID.randomUUID().toString();

            // Analyze the query to understand intent
            String queryIntent = queryAnalysisService.analyzeQueryIntent(request.getQuery());
            log.debug("Identified query intent: {}", queryIntent);

            // Fetch relevant reward data based on query intent
            RewardContextData contextData = rewardDataService.getRewardContext(
                    request.getCustomerId(), queryIntent);

            // Generate AI response using context data
            String aiResponse = aiService.generateResponse(
                    request.getQuery(), contextData, request.getUserType());

            return QueryResponse.builder()
                    .response(aiResponse)
                    .sessionId(sessionId)
                    .success(true)
                    .contextData(contextData)
                    .build();

        } catch (Exception e) {
            log.error("Error processing query: {}", request.getQuery(), e);
            throw new RuntimeException("Failed to process query", e);
        }
    }
} 