package com.genai.rewardbot.reward.service;

import com.genai.rewardbot.common.entity.User;
import com.genai.rewardbot.common.entity.Card;
import com.genai.rewardbot.common.entity.RewardPoint;
import com.genai.rewardbot.reward.repository.CardRepository;
import com.genai.rewardbot.reward.repository.RewardPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MockDataService {

    private final CardRepository cardRepository;
    private final RewardPointRepository rewardPointRepository;
    private final Random random = new Random();

    public void generateMockDataForUser(User user) {
        log.info("Generating mock data for user: {}", user.getMobileNumber());
        
        // Create mock cards from different vendors
        List<Card> mockCards = createMockCards(user);
        
        // Save cards
        List<Card> savedCards = cardRepository.saveAll(mockCards);
        
        // Create mock reward points for each card
        for (Card card : savedCards) {
            List<RewardPoint> mockRewardPoints = createMockRewardPoints(card);
            rewardPointRepository.saveAll(mockRewardPoints);
        }
        
        log.info("Generated mock data: {} cards with reward points for user {}", 
                savedCards.size(), user.getMobileNumber());
    }

    private List<Card> createMockCards(User user) {
        List<Card> cards = new ArrayList<>();
        
        // HDFC Credit Card
        Card hdfcCard = new Card();
        hdfcCard.setCardNumber("4532" + generateRandomDigits(12));
        hdfcCard.setCardType("CREDIT");
        hdfcCard.setVendorName("HDFC Bank");
        hdfcCard.setVendorCode("HDFC");
        hdfcCard.setCardBalance(new BigDecimal("25000.50"));
        hdfcCard.setCurrency("INR");
        hdfcCard.setCashbackRate(new BigDecimal("2.5"));
        hdfcCard.setExpiryDate(LocalDateTime.now().plusYears(3));
        hdfcCard.setUser(user);
        cards.add(hdfcCard);
        
        // Amazon Pay Card
        Card amazonCard = new Card();
        amazonCard.setCardNumber("5555" + generateRandomDigits(12));
        amazonCard.setCardType("LOYALTY");
        amazonCard.setVendorName("Amazon Pay");
        amazonCard.setVendorCode("AMZN");
        amazonCard.setCardBalance(new BigDecimal("1250.75"));
        amazonCard.setCurrency("INR");
        amazonCard.setCashbackRate(new BigDecimal("5.0"));
        amazonCard.setExpiryDate(LocalDateTime.now().plusYears(2));
        amazonCard.setUser(user);
        cards.add(amazonCard);
        
        // Flipkart Axis Bank Card
        Card flipkartCard = new Card();
        flipkartCard.setCardNumber("6011" + generateRandomDigits(12));
        flipkartCard.setCardType("CREDIT");
        flipkartCard.setVendorName("Flipkart Axis Bank");
        flipkartCard.setVendorCode("FLIP");
        flipkartCard.setCardBalance(new BigDecimal("8750.25"));
        flipkartCard.setCurrency("INR");
        flipkartCard.setCashbackRate(new BigDecimal("4.0"));
        flipkartCard.setExpiryDate(LocalDateTime.now().plusYears(4));
        flipkartCard.setUser(user);
        cards.add(flipkartCard);
        
        // SBI Debit Card
        Card sbiCard = new Card();
        sbiCard.setCardNumber("4000" + generateRandomDigits(12));
        sbiCard.setCardType("DEBIT");
        sbiCard.setVendorName("State Bank of India");
        sbiCard.setVendorCode("SBI");
        sbiCard.setCardBalance(new BigDecimal("15430.80"));
        sbiCard.setCurrency("INR");
        sbiCard.setCashbackRate(new BigDecimal("1.0"));
        sbiCard.setExpiryDate(LocalDateTime.now().plusYears(5));
        sbiCard.setUser(user);
        cards.add(sbiCard);
        
        return cards;
    }

    private List<RewardPoint> createMockRewardPoints(Card card) {
        List<RewardPoint> rewardPoints = new ArrayList<>();
        
        // Create 3-5 reward point entries per card
        int numberOfEntries = 3 + random.nextInt(3);
        
        for (int i = 0; i < numberOfEntries; i++) {
            RewardPoint rewardPoint = new RewardPoint();
            
            int pointsEarned = 500 + random.nextInt(9500); // 500-10000 points
            rewardPoint.setPointsEarned(pointsEarned);
            rewardPoint.setPointsUsed(random.nextInt(pointsEarned / 4)); // Use up to 25% of points
            rewardPoint.setPointsAvailable(pointsEarned - rewardPoint.getPointsUsed());
            
            // Earning date between 1-12 months ago
            rewardPoint.setEarningDate(LocalDateTime.now().minusMonths(1 + random.nextInt(12)));
            
            // Expiry date between 3-24 months from now
            rewardPoint.setExpiryDate(LocalDateTime.now().plusMonths(3 + random.nextInt(21)));
            
            rewardPoint.setSource(getRandomSource());
            rewardPoint.setDescription(generateDescription(card.getVendorName(), rewardPoint.getSource()));
            rewardPoint.setTransactionId("TXN" + System.currentTimeMillis() + i);
            rewardPoint.setPointValue(0.25 + (random.nextDouble() * 0.75)); // Value between 0.25-1.0 INR per point
            rewardPoint.setCard(card);
            
            rewardPoints.add(rewardPoint);
        }
        
        return rewardPoints;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String getRandomSource() {
        String[] sources = {"PURCHASE", "CASHBACK", "BONUS", "REFERRAL", "BIRTHDAY_BONUS", "ANNIVERSARY_BONUS"};
        return sources[random.nextInt(sources.length)];
    }

    private String generateDescription(String vendorName, String source) {
        switch (source) {
            case "PURCHASE":
                return "Points earned from " + vendorName + " purchase";
            case "CASHBACK":
                return "Cashback points from " + vendorName + " transaction";
            case "BONUS":
                return "Bonus points from " + vendorName + " promotion";
            case "REFERRAL":
                return "Referral bonus points from " + vendorName;
            case "BIRTHDAY_BONUS":
                return "Birthday bonus points from " + vendorName;
            case "ANNIVERSARY_BONUS":
                return "Anniversary bonus points from " + vendorName;
            default:
                return "Points earned from " + vendorName;
        }
    }
} 