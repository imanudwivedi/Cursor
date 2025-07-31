package com.rewards.reward.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PointExpiry entity for tracking points with expiry dates
 */
@Entity
@Table(name = "point_expiries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointExpiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private RewardAccount rewardAccount;

    @Column(name = "points", nullable = false)
    private Long points;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ExpiryStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ExpiryStatus.ACTIVE;
        }
    }

    public enum ExpiryStatus {
        ACTIVE, EXPIRED, REDEEMED
    }
} 