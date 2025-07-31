package com.rewards.customer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CashbackAccount entity representing customer cashback balance and details
 */
@Entity
@Table(name = "cashback_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashbackAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "total_earned", precision = 10, scale = 2)
    private BigDecimal totalEarned;

    @Column(name = "total_redeemed", precision = 10, scale = 2)
    private BigDecimal totalRedeemed;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (totalEarned == null) {
            totalEarned = BigDecimal.ZERO;
        }
        if (totalRedeemed == null) {
            totalRedeemed = BigDecimal.ZERO;
        }
        if (currency == null) {
            currency = "USD";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AccountType {
        PRIMARY, BONUS, PROMOTIONAL, REFERRAL
    }
} 