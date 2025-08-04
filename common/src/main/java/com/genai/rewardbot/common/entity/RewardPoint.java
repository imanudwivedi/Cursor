package com.genai.rewardbot.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reward_seq")
    @SequenceGenerator(name = "reward_seq", sequenceName = "reward_sequence", allocationSize = 1)
    private Long id;
    
    @Column(name = "points_earned", nullable = false)
    @PositiveOrZero(message = "Points earned must be positive or zero")
    private Integer pointsEarned;
    
    @Column(name = "points_used")
    @PositiveOrZero(message = "Points used must be positive or zero")
    private Integer pointsUsed = 0;
    
    @Column(name = "points_available", nullable = false)
    @PositiveOrZero(message = "Available points must be positive or zero")
    private Integer pointsAvailable;
    
    @Column(name = "earning_date", nullable = false)
    private LocalDateTime earningDate;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "source", length = 100)
    private String source; // Transaction source: PURCHASE, CASHBACK, BONUS, etc.
    
    @Column(name = "description", length = 255)
    private String description; // Description of how points were earned
    
    @Column(name = "transaction_id", length = 50)
    private String transactionId; // Reference to original transaction
    
    @Column(name = "is_expired")
    private Boolean isExpired = false;
    
    @Column(name = "point_value", precision = 10, scale = 2)
    private Double pointValue; // Value of each point in currency
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;
    
    public RewardPoint(Integer pointsEarned, LocalDateTime earningDate, LocalDateTime expiryDate, 
                      String source, String description, Card card) {
        this.pointsEarned = pointsEarned;
        this.pointsAvailable = pointsEarned;
        this.earningDate = earningDate;
        this.expiryDate = expiryDate;
        this.source = source;
        this.description = description;
        this.card = card;
    }
    
    // Helper method to check if points are expiring soon (within 30 days)
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDateTime.now().plusDays(30));
    }
    
    // Helper method to check if points are expired
    public boolean isPointsExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDateTime.now());
    }
} 