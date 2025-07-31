package com.rewards.reward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Reward Service Application
 * 
 * Microservice responsible for managing reward points, 
 * balances, expiry tracking, and reward transactions.
 */
@SpringBootApplication
public class RewardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewardServiceApplication.class, args);
    }
} 