package com.genai.rewardbot.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
    @SequenceGenerator(name = "card_seq", sequenceName = "card_sequence", allocationSize = 1)
    private Long id;
    
    @Column(name = "card_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Card number is required")
    private String cardNumber;
    
    @Column(name = "card_type", length = 50)
    private String cardType; // CREDIT, DEBIT, LOYALTY, etc.
    
    @Column(name = "vendor_name", nullable = false, length = 100)
    @NotBlank(message = "Vendor name is required")
    private String vendorName; // Bank name or vendor like "HDFC Bank", "Amazon", "Flipkart"
    
    @Column(name = "vendor_code", length = 10)
    private String vendorCode; // Short code for vendor like "HDFC", "AMZN", "FLIP"
    
    @Column(name = "card_balance", precision = 15, scale = 2)
    @PositiveOrZero(message = "Card balance must be positive or zero")
    private BigDecimal cardBalance = BigDecimal.ZERO;
    
    @Column(name = "currency", length = 3)
    private String currency = "INR";
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "cashback_rate", precision = 5, scale = 2)
    private BigDecimal cashbackRate; // Percentage cashback rate
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RewardPoint> rewardPoints;
    
    public Card(String cardNumber, String cardType, String vendorName, String vendorCode, 
                BigDecimal cardBalance, User user) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.vendorName = vendorName;
        this.vendorCode = vendorCode;
        this.cardBalance = cardBalance;
        this.user = user;
    }
} 