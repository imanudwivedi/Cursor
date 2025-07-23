package com.genai.rewards.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for OpenAI service
 */
@Configuration
public class OpenAIConfiguration {

    @Value("${ai.openai.api-key}")
    private String apiKey;

    /**
     * Configure OpenAI service bean
     */
    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(apiKey, Duration.ofSeconds(30));
    }
} 