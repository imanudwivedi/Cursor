package com.genai.rewardbot.genai.controller;

import com.genai.rewardbot.common.dto.GenAiQueryDto;
import com.genai.rewardbot.genai.service.GenAiProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/genai")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GenAiController {

    private final GenAiProcessingService genAiProcessingService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> processQuery(@Valid @RequestBody QueryRequest request) {
        log.info("Processing GenAI query for user: {}", request.getMobileNumber());
        
        try {
            String response = genAiProcessingService.processUserQuery(
                request.getMobileNumber(), 
                request.getQuery()
            );
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("response", response);
            responseMap.put("intent", genAiProcessingService.getLastDetectedIntent());
            responseMap.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(responseMap);
            
        } catch (Exception e) {
            log.error("Error processing GenAI query: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "I apologize, but I'm experiencing some technical difficulties. Please try again later.");
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "genai-service");
        response.put("openai_status", genAiProcessingService.isOpenAiAvailable());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testQuery(@RequestBody TestQueryRequest request) {
        log.info("Testing GenAI query: {}", request.getQuery());
        
        try {
            String response = genAiProcessingService.processTestQuery(request.getQuery());
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("response", response);
            responseMap.put("query", request.getQuery());
            
            return ResponseEntity.ok(responseMap);
            
        } catch (Exception e) {
            log.error("Error processing test query: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    // Request DTOs
    public static class QueryRequest {
        private String mobileNumber;
        private String query;

        // Getters and setters
        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
        
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
    }

    public static class TestQueryRequest {
        private String query;

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
    }
} 