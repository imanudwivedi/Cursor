package com.genai.rewardbot.genai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.genai.rewardbot")
public class GenAiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenAiServiceApplication.class, args);
    }
} 