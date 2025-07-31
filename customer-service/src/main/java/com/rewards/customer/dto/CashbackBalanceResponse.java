package com.rewards.customer.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for cashback balance response - matches RewardQueryBot expectations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashbackBalanceResponse {

    private BigDecimal balance;
    private String currency;
} 