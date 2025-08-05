package com.genai.rewardbot.genai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MockDataService {

    private final Map<String, OpenAiIntegrationService.UserContext> userContextCache = new HashMap<>();

    public OpenAiIntegrationService.UserContext getUserContext(String mobileNumber) {
        return userContextCache.computeIfAbsent(mobileNumber, this::createUserContext);
    }

    public OpenAiIntegrationService.UserContext getTestUserContext() {
        return getUserContext("+1 5551234567");
    }

    private OpenAiIntegrationService.UserContext createUserContext(String mobileNumber) {
        log.info("Creating user context for: {}", mobileNumber);
        
        // Create mock user context based on mobile number
        OpenAiIntegrationService.UserContext context = new OpenAiIntegrationService.UserContext(
            "Alex Johnson", // Default name
            47910,          // Total points
            3,              // Active cards
            7401.50         // Total balance
        );

        // Set detailed information
        context.setCardsInfo(buildCardsInfo());
        context.setExpiryInfo(buildExpiryInfo());
        context.setCashbackInfo(buildCashbackInfo());
        context.setRedemptionOptions(buildRedemptionOptions());
        context.setBalanceInfo(buildBalanceInfo());
        context.setDetailedInfo(buildDetailedInfo());

        return context;
    }

    private String buildCardsInfo() {
        return """
            • **HDFC Bank Credit Card**: 15,420 points
            • **Amazon Pay Loyalty Card**: 18,750 points
            • **Flipkart Axis Bank Card**: 13,740 points
            """;
    }

    private String buildExpiryInfo() {
        return """
            ⚠️ **Expiring Soon (within 30 days):**
            • HDFC Bank: 500 points (expires in 15 days)
            • Flipkart Axis Bank: 1,200 points (expires in 28 days)
            
            📅 **Future Expiries:**
            • Amazon Pay: 18,750 points (expires in 8 months)
            • HDFC Bank: 14,920 points (expires in 11 months)
            """;
    }

    private String buildCashbackInfo() {
        return """
            • **Amazon Pay**: 5.0% (highest rate!)
            • **Flipkart Axis Bank**: 4.0%
            • **HDFC Bank**: 2.5%
            """;
    }

    private String buildRedemptionOptions() {
        return """
            🏆 **Best Value Options:**
            1. **Amazon Gift Voucher**: $28,746 value (20% bonus)
            2. **Flight Booking Credit**: $26,000 value
            3. **Direct Bank Transfer**: $23,955
            4. **Shopping Vouchers**: $25,500-29,000 range
            5. **Dining Vouchers**: $24,000-27,000 range
            """;
    }

    private String buildBalanceInfo() {
        return """
            💳 **Available Balances:**
            • **HDFC Bank**: $25,000.50
            • **Amazon Pay**: $1,250.75
            • **Flipkart Axis Bank**: $8,750.25
            """;
    }

    private String buildDetailedInfo() {
        return """
            User has 47,910 total reward points across 3 active cards.
            Total available balance: $7,401.50
            Cards: HDFC Bank (Credit, 2.5% cashback), Amazon Pay (Loyalty, 5.0% cashback), Flipkart Axis Bank (Credit, 4.0% cashback)
            Expiring points: 1,700 points expiring within 30 days
            Best redemption: Amazon Gift Vouchers with 20% bonus
            """;
    }

    // Update user context for specific scenarios
    public void updateUserContext(String mobileNumber, String contextType, Object data) {
        OpenAiIntegrationService.UserContext context = userContextCache.get(mobileNumber);
        if (context != null) {
            switch (contextType) {
                case "POINTS_UPDATE" -> {
                    // Update points if needed
                    log.info("Updated points context for user: {}", mobileNumber);
                }
                case "CARDS_UPDATE" -> {
                    // Update cards if needed
                    log.info("Updated cards context for user: {}", mobileNumber);
                }
                default -> log.warn("Unknown context type: {}", contextType);
            }
        }
    }
} 