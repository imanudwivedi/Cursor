package com.genai.rewardbot.genai.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class OpenAiIntegrationService {

    private final OpenAiService openAiService;
    private final boolean openAiEnabled;

    public OpenAiIntegrationService(@Value("${openai.api-key:}") String apiKey,
                                   @Value("${openai.enabled:false}") boolean enabled) {
        this.openAiEnabled = enabled && apiKey != null && !apiKey.trim().isEmpty();
        
        if (this.openAiEnabled) {
            this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
            log.info("OpenAI service initialized successfully");
        } else {
            this.openAiService = null;
            log.info("OpenAI service disabled - using fallback responses");
        }
    }

    public QueryIntent analyzeUserIntent(String userQuery, UserContext userContext) {
        if (!openAiEnabled) {
            return analyzeIntentFallback(userQuery);
        }

        try {
            String systemPrompt = buildSystemPrompt(userContext);
            String analysisPrompt = buildAnalysisPrompt(userQuery);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                            new ChatMessage(ChatMessageRole.USER.value(), analysisPrompt)
                    ))
                    .maxTokens(150)
                    .temperature(0.3)
                    .build();

            var response = openAiService.createChatCompletion(request);
            String aiResponse = response.getChoices().get(0).getMessage().getContent().trim();
            
            return parseAiResponse(aiResponse, userQuery);

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage());
            return analyzeIntentFallback(userQuery);
        }
    }

    public String generateIntelligentResponse(String userQuery, QueryIntent intent, UserContext userContext) {
        if (!openAiEnabled) {
            return generateResponseFallback(userQuery, intent, userContext);
        }

        try {
            String systemPrompt = buildResponseSystemPrompt(userContext);
            String responsePrompt = buildResponsePrompt(userQuery, intent, userContext);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                            new ChatMessage(ChatMessageRole.USER.value(), responsePrompt)
                    ))
                    .maxTokens(300)
                    .temperature(0.7)
                    .build();

            var response = openAiService.createChatCompletion(request);
            String aiResponse = response.getChoices().get(0).getMessage().getContent().trim();
            
            return formatResponse(aiResponse, intent, userContext);

        } catch (Exception e) {
            log.error("Error generating OpenAI response: {}", e.getMessage());
            return generateResponseFallback(userQuery, intent, userContext);
        }
    }

    private String buildSystemPrompt(UserContext userContext) {
        return String.format("""
            You are RewardBot, an AI assistant for reward points and credit card management.
            
            User Context:
            - Name: %s
            - Total Reward Points: %d
            - Active Cards: %d
            - Total Balance: $%.2f
            
            Your job is to analyze user queries and classify them into these intents:
            - POINTS_BALANCE: Questions about total points, points per card
            - POINTS_EXPIRY: Questions about when points expire, expiring points
            - CASHBACK_INFO: Questions about cashback rates, cashback options
            - REDEMPTION_OPTIONS: Questions about what they can buy/redeem with points
            - CARD_BALANCE: Questions about card balances, available money
            - GENERAL_INFO: General questions about the service
            
            Respond with just the intent name and confidence (0-1).
            """, 
            userContext.getUserName(), 
            userContext.getTotalPoints(), 
            userContext.getActiveCards(),
            userContext.getTotalBalance());
    }

    private String buildAnalysisPrompt(String userQuery) {
        return String.format("""
            Analyze this user query and determine the intent:
            "%s"
            
            Respond in this exact format:
            INTENT: [intent_name]
            CONFIDENCE: [0.0-1.0]
            """, userQuery);
    }

    private String buildResponseSystemPrompt(UserContext userContext) {
        return String.format("""
            You are RewardBot, a friendly AI assistant for reward points management.
            
            User Context:
            - Name: %s
            - Total Reward Points: %s
            - Cards: %s
            - Total Balance: $%.2f
            
            Generate helpful, personalized responses using the user's actual data.
            Be conversational but professional. Use emojis appropriately.
            Always include specific numbers from their account.
            """,
            userContext.getUserName(),
            formatPoints(userContext.getTotalPoints()),
            userContext.getCardsInfo(),
            userContext.getTotalBalance());
    }

    private String buildResponsePrompt(String userQuery, QueryIntent intent, UserContext userContext) {
        return String.format("""
            User asked: "%s"
            Detected intent: %s
            
            Generate a helpful response using their actual data:
            %s
            
            Keep response under 200 words and include actionable advice.
            """, 
            userQuery, 
            intent.getIntentType(),
            userContext.getDetailedInfo());
    }

    private QueryIntent parseAiResponse(String aiResponse, String originalQuery) {
        try {
            String[] lines = aiResponse.split("\n");
            String intentType = "GENERAL_INFO";
            double confidence = 0.5;

            for (String line : lines) {
                if (line.startsWith("INTENT:")) {
                    intentType = line.substring(7).trim();
                } else if (line.startsWith("CONFIDENCE:")) {
                    confidence = Double.parseDouble(line.substring(11).trim());
                }
            }

            return new QueryIntent(intentType, confidence, originalQuery);
        } catch (Exception e) {
            log.warn("Failed to parse AI response, using fallback: {}", e.getMessage());
            return analyzeIntentFallback(originalQuery);
        }
    }

    private QueryIntent analyzeIntentFallback(String userQuery) {
        String query = userQuery.toLowerCase();
        
        if (query.contains("points") && (query.contains("how many") || query.contains("total"))) {
            return new QueryIntent("POINTS_BALANCE", 0.9, userQuery);
        } else if (query.contains("expir") || query.contains("when")) {
            return new QueryIntent("POINTS_EXPIRY", 0.8, userQuery);
        } else if (query.contains("cashback") || query.contains("cash back")) {
            return new QueryIntent("CASHBACK_INFO", 0.8, userQuery);
        } else if (query.contains("redeem") || query.contains("buy") || query.contains("purchase")) {
            return new QueryIntent("REDEMPTION_OPTIONS", 0.8, userQuery);
        } else if (query.contains("balance")) {
            return new QueryIntent("CARD_BALANCE", 0.8, userQuery);
        } else {
            return new QueryIntent("GENERAL_INFO", 0.5, userQuery);
        }
    }

    private String generateResponseFallback(String userQuery, QueryIntent intent, UserContext userContext) {
        return switch (intent.getIntentType()) {
            case "POINTS_BALANCE" -> String.format(
                "You have a total of **%s reward points** across all your cards! 🎉\n\n" +
                "Here's the breakdown:\n%s\n\n" +
                "You're doing great with your rewards! 💪",
                formatPoints(userContext.getTotalPoints()),
                userContext.getCardsInfo()
            );
            
            case "POINTS_EXPIRY" -> String.format(
                "Here's your points expiry information: ⏰\n\n" +
                "%s\n\n" +
                "💡 **Pro tip**: Redeem expiring points first to maximize your rewards!",
                userContext.getExpiryInfo()
            );
            
            case "CASHBACK_INFO" -> String.format(
                "Here are your cashback rates by card: 💳\n\n" +
                "%s\n\n" +
                "💰 Your %s points are worth approximately **$%.2f** in cashback value!",
                userContext.getCashbackInfo(),
                formatPoints(userContext.getTotalPoints()),
                userContext.getTotalPoints() * 0.5
            );
            
            case "REDEMPTION_OPTIONS" -> String.format(
                "With your **%s points**, here are your best redemption options: 🎁\n\n" +
                "%s\n\n" +
                "🏆 **Best Value**: Amazon Gift Vouchers give you 20%% bonus!",
                formatPoints(userContext.getTotalPoints()),
                userContext.getRedemptionOptions()
            );
            
            case "CARD_BALANCE" -> String.format(
                "Your current card balances: 💳\n\n" +
                "%s\n\n" +
                "**Total Available**: $%.2f across all cards!",
                userContext.getBalanceInfo(),
                userContext.getTotalBalance()
            );
            
            default -> String.format(
                "Hi there! 👋 I'm here to help you with your rewards.\n\n" +
                "You currently have **%s points** and $%.2f available.\n\n" +
                "Ask me about:\n• Points balance and expiry\n• Cashback rates\n• Redemption options\n• Card balances",
                formatPoints(userContext.getTotalPoints()),
                userContext.getTotalBalance()
            );
        };
    }

    private String formatResponse(String aiResponse, QueryIntent intent, UserContext userContext) {
        // Enhanced formatting with user data
        return aiResponse + "\n\n" + getQuickActions(intent);
    }

    private String getQuickActions(QueryIntent intent) {
        return switch (intent.getIntentType()) {
            case "POINTS_BALANCE" -> "💡 **Quick Actions**: View expiry dates • Redeem points • Check cashback rates";
            case "POINTS_EXPIRY" -> "💡 **Quick Actions**: Redeem expiring points • View redemption options • Set reminders";
            case "CASHBACK_INFO" -> "💡 **Quick Actions**: Optimize spending • Compare cards • View earning history";
            case "REDEMPTION_OPTIONS" -> "💡 **Quick Actions**: Start redemption • Compare values • Check requirements";
            case "CARD_BALANCE" -> "💡 **Quick Actions**: View transactions • Make payment • Check limits";
            default -> "💡 **Quick Actions**: Check points • View cards • Redeem rewards • Get support";
        };
    }

    private String formatPoints(int points) {
        return String.format("%,d", points);
    }

    // Data classes
    public static class QueryIntent {
        private final String intentType;
        private final double confidence;
        private final String originalQuery;

        public QueryIntent(String intentType, double confidence, String originalQuery) {
            this.intentType = intentType;
            this.confidence = confidence;
            this.originalQuery = originalQuery;
        }

        // Getters
        public String getIntentType() { return intentType; }
        public double getConfidence() { return confidence; }
        public String getOriginalQuery() { return originalQuery; }
    }

    public static class UserContext {
        private String userName;
        private int totalPoints;
        private int activeCards;
        private double totalBalance;
        private String cardsInfo;
        private String expiryInfo;
        private String cashbackInfo;
        private String redemptionOptions;
        private String balanceInfo;
        private String detailedInfo;

        // Constructor and getters
        public UserContext(String userName, int totalPoints, int activeCards, double totalBalance) {
            this.userName = userName;
            this.totalPoints = totalPoints;
            this.activeCards = activeCards;
            this.totalBalance = totalBalance;
        }

        // Getters
        public String getUserName() { return userName; }
        public int getTotalPoints() { return totalPoints; }
        public int getActiveCards() { return activeCards; }
        public double getTotalBalance() { return totalBalance; }
        public String getCardsInfo() { return cardsInfo != null ? cardsInfo : "Card details loading..."; }
        public String getExpiryInfo() { return expiryInfo != null ? expiryInfo : "Expiry details loading..."; }
        public String getCashbackInfo() { return cashbackInfo != null ? cashbackInfo : "Cashback details loading..."; }
        public String getRedemptionOptions() { return redemptionOptions != null ? redemptionOptions : "Redemption options loading..."; }
        public String getBalanceInfo() { return balanceInfo != null ? balanceInfo : "Balance details loading..."; }
        public String getDetailedInfo() { return detailedInfo != null ? detailedInfo : "User details loading..."; }

        // Setters for detailed info
        public void setCardsInfo(String cardsInfo) { this.cardsInfo = cardsInfo; }
        public void setExpiryInfo(String expiryInfo) { this.expiryInfo = expiryInfo; }
        public void setCashbackInfo(String cashbackInfo) { this.cashbackInfo = cashbackInfo; }
        public void setRedemptionOptions(String redemptionOptions) { this.redemptionOptions = redemptionOptions; }
        public void setBalanceInfo(String balanceInfo) { this.balanceInfo = balanceInfo; }
        public void setDetailedInfo(String detailedInfo) { this.detailedInfo = detailedInfo; }
    }
} 