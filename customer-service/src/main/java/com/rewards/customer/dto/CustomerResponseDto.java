package com.rewards.customer.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for customer response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {

    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status;
    private String tierLevel;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;
    private BigDecimal totalSpend;
    private List<CashbackAccountDto> cashbackAccounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CashbackAccountDto {
        private Long id;
        private String accountType;
        private BigDecimal balance;
        private BigDecimal totalEarned;
        private BigDecimal totalRedeemed;
        private String currency;
        private LocalDateTime lastTransactionDate;
    }
} 