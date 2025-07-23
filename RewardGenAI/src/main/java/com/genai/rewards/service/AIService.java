package com.genai.rewards.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.rewards.dto.RewardContextData;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service for AI-powered response generation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    @Value("${ai.openai.model}")
    private String model;

    @Value("${ai.openai.max-tokens}")
    private Integer maxTokens;

    @Value("${ai.openai.temperature}")
    private Double temperature;

    @Value("${ai.openai.system-prompt}")
    private String systemPrompt;

    /**
     * Generate AI response for a reward query
     */
    @Cacheable(value = "aiResponses", key = "#query + #contextData.totalPoints + #userType")
    public String generateResponse(String query, RewardContextData contextData, String userType) {
        log.debug("Generating AI response for query: {}", query);

        try {
            String contextString = buildContextString(contextData);
            String userPrompt = buildUserPrompt(query, contextString, userType);

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                            new ChatMessage(ChatMessageRole.USER.value(), userPrompt)
                    ))
                    .maxTokens(maxTokens)
                    .temperature(temperature)
                    .build();

            String response = openAiService.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.debug("Generated AI response: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Error generating AI response", e);
            return generateFallbackResponse(contextData);
        }
    }

    /**
     * Build context string from reward data
     */
    private String buildContextString(RewardContextData contextData) {
        StringBuilder context = new StringBuilder();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        context.append("REWARD INFORMATION:\n");

        if (contextData.getTotalPoints() != null) {
            context.append("Total Points: ").append(numberFormat.format(contextData.getTotalPoints())).append("\n");
        }

        if (contextData.getAvailablePoints() != null) {
            context.append("Available Points: ").append(numberFormat.format(contextData.getAvailablePoints())).append("\n");
        }

        if (contextData.getCashbackBalance() != null) {
            context.append("Cashback Balance: $").append(contextData.getCashbackBalance()).append("\n");
        }

        if (contextData.getNextExpiryDate() != null) {
            context.append("Next Expiry Date: ").append(
                    contextData.getNextExpiryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            ).append("\n");
        }

        if (contextData.getRedemptionOptions() != null && !contextData.getRedemptionOptions().isEmpty()) {
            context.append("\nREDEMPTION OPTIONS:\n");
            contextData.getRedemptionOptions().forEach(option -> {
                context.append("- ").append(option.getName())
                        .append(": ").append(numberFormat.format(option.getPointsRequired()))
                        .append(" points");
                if (option.getCashValue() != null) {
                    context.append(" (Value: $").append(option.getCashValue()).append(")");
                }
                context.append("\n");
            });
        }

        if (contextData.getExpiringPoints() != null && !contextData.getExpiringPoints().isEmpty()) {
            context.append("\nEXPIRING POINTS:\n");
            contextData.getExpiringPoints().forEach(expiry -> {
                context.append("- ").append(numberFormat.format(expiry.getPoints()))
                        .append(" points expiring on ")
                        .append(expiry.getExpiryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                        .append("\n");
            });
        }

        return context.toString();
    }

    /**
     * Build user prompt for AI
     */
    private String buildUserPrompt(String query, String contextString, String userType) {
        return String.format(
                "User Type: %s\n\n%s\n\nUser Query: %s\n\nPlease provide a helpful, friendly response based on the reward information above.",
                userType, contextString, query
        );
    }

    /**
     * Generate fallback response when AI service fails
     */
    private String generateFallbackResponse(RewardContextData contextData) {
        if (contextData.getAvailablePoints() != null) {
            return String.format(
                    "You currently have %s reward points available. For specific redemption options and detailed information, please contact customer support.",
                    NumberFormat.getNumberInstance(Locale.US).format(contextData.getAvailablePoints())
            );
        }
        return "I'm sorry, I'm having trouble accessing your reward information right now. Please contact customer support for assistance.";
    }
} 