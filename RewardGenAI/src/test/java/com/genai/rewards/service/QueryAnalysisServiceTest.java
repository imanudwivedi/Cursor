package com.genai.rewards.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for QueryAnalysisService
 */
@ExtendWith(MockitoExtension.class)
class QueryAnalysisServiceTest {

    @InjectMocks
    private QueryAnalysisService queryAnalysisService;

    @Test
    void analyzeQueryIntent_BalanceQuery() {
        // Given
        String query = "How many reward points do I have?";

        // When
        String intent = queryAnalysisService.analyzeQueryIntent(query);

        // Then
        assertTrue(intent.contains("BALANCE"));
    }

    @Test
    void analyzeQueryIntent_RedemptionQuery() {
        // Given
        String query = "What can I redeem with 5000 points?";

        // When
        String intent = queryAnalysisService.analyzeQueryIntent(query);

        // Then
        assertTrue(intent.contains("REDEEM"));
        assertTrue(intent.contains("SPECIFIC_AMOUNT"));
    }

    @Test
    void analyzeQueryIntent_ExpiryQuery() {
        // Given
        String query = "When do my points expire?";

        // When
        String intent = queryAnalysisService.analyzeQueryIntent(query);

        // Then
        assertTrue(intent.contains("EXPIRY"));
    }

    @Test
    void analyzeQueryIntent_CashbackQuery() {
        // Given
        String query = "What's my cashback balance?";

        // When
        String intent = queryAnalysisService.analyzeQueryIntent(query);

        // Then
        assertTrue(intent.contains("CASHBACK"));
        assertTrue(intent.contains("BALANCE"));
    }

    @Test
    void analyzeQueryIntent_GeneralInquiry() {
        // Given
        String query = "Tell me about the rewards program";

        // When
        String intent = queryAnalysisService.analyzeQueryIntent(query);

        // Then
        assertEquals("GENERAL_INQUIRY", intent);
    }
} 