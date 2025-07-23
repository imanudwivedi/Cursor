package com.genai.rewards.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Feign client for Redemption Service integration
 */
@FeignClient(
    name = "redemption-service",
    url = "${microservices.redemption.base-url}",
    configuration = FeignConfiguration.class
)
public interface RedemptionServiceClient {

    /**
     * Get available redemption options for a customer
     */
    @GetMapping("/redemptions/{customerId}/options")
    List<RedemptionOptionResponse> getRedemptionOptions(@PathVariable String customerId);

    /**
     * Response DTO for redemption options
     */
    record RedemptionOptionResponse(
        String name,
        String description,
        Long pointsRequired,
        String category,
        BigDecimal cashValue,
        boolean available
    ) {}
} 