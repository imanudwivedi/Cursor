package com.genai.rewardbot.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.genai.rewardbot.**.repository")
@EntityScan(basePackages = "com.genai.rewardbot.common.entity")
@EnableTransactionManagement
public class DatabaseConfig {
    // Configuration will be handled through application properties
} 