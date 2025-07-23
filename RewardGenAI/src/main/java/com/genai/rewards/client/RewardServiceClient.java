package com.genai.rewards.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

/**
 * Feign client for Reward Service integration
 */
@FeignClient(
    name = "reward-service",
    url = "${microservices.rewards.base-url}",
    configuration = FeignConfiguration.class
)
public interface RewardServiceClient {

    /**
     * Get reward balance for a customer
     */
    @GetMapping("/rewards/{customerId}/balance")
    RewardBalanceResponse getRewardBalance(@PathVariable String customerId);

    /**
     * Get expiring points for a customer
     */
    @GetMapping("/rewards/{customerId}/expiring")
    List<ExpiringPointsResponse> getExpiringPoints(@PathVariable String customerId);

    /**
     * Response DTOs for external service integration
     */
    record RewardBalanceResponse(
        Long totalPoints,
        Long availablePoints,
        Long expiredPoints,
        LocalDate nextExpiryDate
    ) {}

    record ExpiringPointsResponse(
        Long points,
        LocalDate expiryDate,
        String source
    ) {}
} 