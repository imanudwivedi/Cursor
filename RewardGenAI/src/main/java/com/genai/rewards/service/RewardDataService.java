package com.genai.rewards.service;

import com.genai.rewards.client.CustomerServiceClient;
import com.genai.rewards.client.RedemptionServiceClient;
import com.genai.rewards.client.RewardServiceClient;
import com.genai.rewards.dto.RewardContextData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for fetching reward data from microservices
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RewardDataService {

    private final RewardServiceClient rewardServiceClient;
    private final RedemptionServiceClient redemptionServiceClient;
    private final CustomerServiceClient customerServiceClient;

    /**
     * Get comprehensive reward context for a customer
     */
    @Cacheable(value = "rewardContext", key = "#customerId + #queryIntent")
    @CircuitBreaker(name = "rewardService", fallbackMethod = "getFallbackRewardContext")
    @Retry(name = "rewardService")
    public RewardContextData getRewardContext(String customerId, String queryIntent) {
        log.debug("Fetching reward context for customer: {}, intent: {}", customerId, queryIntent);

        try {
            RewardContextData.RewardContextDataBuilder builder = RewardContextData.builder();

            // Fetch basic reward information
            var rewardInfo = rewardServiceClient.getRewardBalance(customerId);
            builder.totalPoints(rewardInfo.getTotalPoints())
                   .availablePoints(rewardInfo.getAvailablePoints())
                   .expiredPoints(rewardInfo.getExpiredPoints())
                   .nextExpiryDate(rewardInfo.getNextExpiryDate());

            // Fetch cashback information if relevant
            if (queryIntent.contains("CASHBACK") || queryIntent.contains("BALANCE")) {
                var cashbackInfo = customerServiceClient.getCashbackBalance(customerId);
                builder.cashbackBalance(cashbackInfo.getBalance());
            }

            // Fetch redemption options if relevant
            if (queryIntent.contains("REDEEM") || queryIntent.contains("OPTIONS") || 
                queryIntent.contains("SPEND") || queryIntent.contains("USE")) {
                var redemptionOptions = redemptionServiceClient.getRedemptionOptions(customerId);
                builder.redemptionOptions(mapRedemptionOptions(redemptionOptions));
            }

            // Fetch expiry details if relevant
            if (queryIntent.contains("EXPIRY") || queryIntent.contains("EXPIRE") || 
                queryIntent.contains("EXPIRING")) {
                var expiryDetails = rewardServiceClient.getExpiringPoints(customerId);
                builder.expiringPoints(mapExpiryDetails(expiryDetails));
            }

            return builder.build();

        } catch (Exception e) {
            log.error("Error fetching reward context for customer: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Fallback method for circuit breaker
     */
    public RewardContextData getFallbackRewardContext(String customerId, String queryIntent, Exception ex) {
        log.warn("Using fallback reward context for customer: {}", customerId);
        
        return RewardContextData.builder()
                .totalPoints(0L)
                .availablePoints(0L)
                .cashbackBalance(BigDecimal.ZERO)
                .redemptionOptions(new ArrayList<>())
                .expiringPoints(new ArrayList<>())
                .build();
    }

    /**
     * Map external redemption options to internal DTOs
     */
    private List<RewardContextData.RedemptionOption> mapRedemptionOptions(List<?> externalOptions) {
        // This would map from your actual microservice response DTOs
        // For now, returning sample data
        return List.of(
                RewardContextData.RedemptionOption.builder()
                        .name("Gift Card - Amazon")
                        .description("Amazon gift card")
                        .pointsRequired(5000L)
                        .category("GIFT_CARDS")
                        .cashValue(BigDecimal.valueOf(50.00))
                        .available(true)
                        .build(),
                RewardContextData.RedemptionOption.builder()
                        .name("Travel Miles")
                        .description("Convert to airline miles")
                        .pointsRequired(10000L)
                        .category("TRAVEL")
                        .cashValue(BigDecimal.valueOf(100.00))
                        .available(true)
                        .build()
        );
    }

    /**
     * Map external expiry details to internal DTOs
     */
    private List<RewardContextData.ExpiryDetail> mapExpiryDetails(List<?> externalExpiries) {
        // This would map from your actual microservice response DTOs
        // For now, returning sample data
        return List.of(
                RewardContextData.ExpiryDetail.builder()
                        .points(2500L)
                        .expiryDate(LocalDate.now().plusDays(30))
                        .source("Welcome Bonus")
                        .build()
        );
    }
} 