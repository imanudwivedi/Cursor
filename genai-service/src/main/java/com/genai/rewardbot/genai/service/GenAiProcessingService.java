package com.genai.rewardbot.genai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenAiProcessingService {

    private final OpenAiIntegrationService openAiService;
    private final MockDataService mockDataService;
    
    private String lastDetectedIntent = "GENERAL_INFO";

    public String processUserQuery(String mobileNumber, String query) {
        log.info("Processing query for user {}: {}", mobileNumber, query);
        
        // Get user context with mock data
        OpenAiIntegrationService.UserContext userContext = mockDataService.getUserContext(mobileNumber);
        
        // Analyze intent using OpenAI
        OpenAiIntegrationService.QueryIntent intent = openAiService.analyzeUserIntent(query, userContext);
        this.lastDetectedIntent = intent.getIntentType();
        
        log.info("Detected intent: {} (confidence: {})", intent.getIntentType(), intent.getConfidence());
        
        // Generate intelligent response
        String response = openAiService.generateIntelligentResponse(query, intent, userContext);
        
        log.info("Generated response for user {}", mobileNumber);
        return response;
    }

    public String processTestQuery(String query) {
        log.info("Processing test query: {}", query);
        
        // Use default test user context
        OpenAiIntegrationService.UserContext testContext = mockDataService.getTestUserContext();
        
        // Analyze intent
        OpenAiIntegrationService.QueryIntent intent = openAiService.analyzeUserIntent(query, testContext);
        this.lastDetectedIntent = intent.getIntentType();
        
        // Generate response
        return openAiService.generateIntelligentResponse(query, intent, testContext);
    }

    public String getLastDetectedIntent() {
        return lastDetectedIntent;
    }

    public boolean isOpenAiAvailable() {
        return openAiService != null;
    }
} 