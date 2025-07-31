package com.rewards.reward.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RewardAccount entity representing customer reward points and balance
 */
@Entity
@Table(name = "reward_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardAccount {

    @Id
    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints;

    @Column(name = "available_points", nullable = false)
    private Long availablePoints;

    @Column(name = "expired_points")
    private Long expiredPoints;

    @Column(name = "pending_points")
    private Long pendingPoints;

    @Column(name = "lifetime_earned")
    private Long lifetimeEarned;

    @Column(name = "lifetime_redeemed")
    private Long lifetimeRedeemed;

    @Column(name = "tier_level")
    @Enumerated(EnumType.STRING)
    private TierLevel tierLevel;

    @Column(name = "tier_points")
    private Long tierPoints;

    @Column(name = "next_expiry_date")
    private LocalDate nextExpiryDate;

    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "rewardAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PointTransaction> transactions;

    @OneToMany(mappedBy = "rewardAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PointExpiry> expiringPoints;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (totalPoints == null) totalPoints = 0L;
        if (availablePoints == null) availablePoints = 0L;
        if (expiredPoints == null) expiredPoints = 0L;
        if (pendingPoints == null) pendingPoints = 0L;
        if (lifetimeEarned == null) lifetimeEarned = 0L;
        if (lifetimeRedeemed == null) lifetimeRedeemed = 0L;
        if (tierPoints == null) tierPoints = 0L;
        if (tierLevel == null) tierLevel = TierLevel.BRONZE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TierLevel {
        BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }
} 