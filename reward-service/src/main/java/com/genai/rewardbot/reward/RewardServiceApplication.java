package com.genai.rewardbot.reward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.genai.rewardbot")
@EntityScan(basePackages = "com.genai.rewardbot.common.entity")
@EnableJpaRepositories(basePackages = "com.genai.rewardbot.reward.repository")
public class RewardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewardServiceApplication.class, args);
    }
} 