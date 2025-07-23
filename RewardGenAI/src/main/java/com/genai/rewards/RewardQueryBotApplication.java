package com.genai.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for GenAI Reward Query Bot
 * 
 * This application provides AI-powered natural language processing
 * for reward-related queries, integrating with microservices to
 * fetch reward data and generate contextual responses.
 */
@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class RewardQueryBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewardQueryBotApplication.class, args);
    }
} 