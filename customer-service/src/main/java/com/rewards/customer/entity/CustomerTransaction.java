package com.rewards.customer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CustomerTransaction entity representing customer transaction history
 */
@Entity
@Table(name = "customer_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "description")
    private String description;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "category")
    private String category;

    @Column(name = "cashback_earned", precision = 10, scale = 2)
    private BigDecimal cashbackEarned;

    @Column(name = "cashback_rate", precision = 5, scale = 4)
    private BigDecimal cashbackRate;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TransactionStatus.PENDING;
        }
        if (currency == null) {
            currency = "USD";
        }
    }

    public enum TransactionType {
        PURCHASE, REFUND, CASHBACK_EARNED, CASHBACK_REDEEMED, ADJUSTMENT
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED, REVERSED
    }
} 