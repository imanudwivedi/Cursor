package com.genai.rewardbot.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.genai.rewardbot")
@EntityScan(basePackages = "com.genai.rewardbot.common.entity")
@EnableJpaRepositories(basePackages = "com.genai.rewardbot.auth.repository")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
} 