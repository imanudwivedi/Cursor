package com.genai.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.rewards.dto.QueryRequest;
import com.genai.rewards.dto.QueryResponse;
import com.genai.rewards.service.RewardQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for RewardQueryController
 */
@WebMvcTest(RewardQueryController.class)
class RewardQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RewardQueryService rewardQueryService;

    @Test
    void processQuery_Success() throws Exception {
        // Given
        QueryRequest request = new QueryRequest(
                "How many points do I have?", 
                "customer123", 
                "session456", 
                "CUSTOMER"
        );

        QueryResponse response = QueryResponse.builder()
                .response("You have 5,000 reward points available!")
                .sessionId("session456")
                .success(true)
                .responseTimeMs(150L)
                .build();

        when(rewardQueryService.processQuery(any(QueryRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/rewards/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("You have 5,000 reward points available!"))
                .andExpect(jsonPath("$.sessionId").value("session456"));
    }

    @Test
    void processQuery_ValidationError() throws Exception {
        // Given - Invalid request with empty query
        QueryRequest request = new QueryRequest("", "customer123", null, "CUSTOMER");

        // When & Then
        mockMvc.perform(post("/api/v1/rewards/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void health_Success() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reward Query Bot is running!"));
    }
} 