package com.genai.rewards.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for analyzing natural language queries to understand user intent
 */
@Service
@Slf4j
public class QueryAnalysisService {

    private static final List<String> BALANCE_KEYWORDS = Arrays.asList(
            "balance", "points", "have", "total", "many", "much", "current"
    );

    private static final List<String> REDEMPTION_KEYWORDS = Arrays.asList(
            "redeem", "spend", "use", "get", "buy", "purchase", "exchange", "options", "available"
    );

    private static final List<String> EXPIRY_KEYWORDS = Arrays.asList(
            "expire", "expiry", "expiring", "when", "deadline", "valid", "until"
    );

    private static final List<String> CASHBACK_KEYWORDS = Arrays.asList(
            "cashback", "cash", "money", "dollars", "refund"
    );

    /**
     * Analyze query intent from natural language
     */
    public String analyzeQueryIntent(String query) {
        log.debug("Analyzing query intent for: {}", query);

        String normalizedQuery = query.toLowerCase().trim();
        
        // Check for specific patterns and keywords
        StringBuilder intent = new StringBuilder();

        if (containsKeywords(normalizedQuery, BALANCE_KEYWORDS)) {
            intent.append("BALANCE ");
        }

        if (containsKeywords(normalizedQuery, REDEMPTION_KEYWORDS)) {
            intent.append("REDEEM ");
        }

        if (containsKeywords(normalizedQuery, EXPIRY_KEYWORDS)) {
            intent.append("EXPIRY ");
        }

        if (containsKeywords(normalizedQuery, CASHBACK_KEYWORDS)) {
            intent.append("CASHBACK ");
        }

        // Check for specific patterns
        if (containsNumberPattern(normalizedQuery)) {
            intent.append("SPECIFIC_AMOUNT ");
        }

        String finalIntent = intent.toString().trim();
        if (finalIntent.isEmpty()) {
            finalIntent = "GENERAL_INQUIRY";
        }

        log.debug("Identified intent: {}", finalIntent);
        return finalIntent;
    }

    /**
     * Check if query contains specific keywords
     */
    private boolean containsKeywords(String query, List<String> keywords) {
        return keywords.stream().anyMatch(query::contains);
    }

    /**
     * Check if query contains number patterns (e.g., "5000 points", "$50")
     */
    private boolean containsNumberPattern(String query) {
        Pattern numberPattern = Pattern.compile("\\d+");
        return numberPattern.matcher(query).find();
    }
} 