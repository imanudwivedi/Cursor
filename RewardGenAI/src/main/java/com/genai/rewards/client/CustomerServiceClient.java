package com.genai.rewards.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/**
 * Feign client for Customer Service integration
 */
@FeignClient(
    name = "customer-service",
    url = "${microservices.customer.base-url}",
    configuration = FeignConfiguration.class
)
public interface CustomerServiceClient {

    /**
     * Get cashback balance for a customer
     */
    @GetMapping("/customers/{customerId}/cashback")
    CashbackBalanceResponse getCashbackBalance(@PathVariable String customerId);

    /**
     * Response DTO for cashback balance
     */
    record CashbackBalanceResponse(
        BigDecimal balance,
        String currency
    ) {}
} 