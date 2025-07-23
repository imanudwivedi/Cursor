package com.genai.rewards.client;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for Feign clients
 */
@Configuration
public class FeignConfiguration {

    @Value("${microservices.rewards.timeout:5000}")
    private int timeout;

    /**
     * Configure request timeout
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            timeout,  // connect timeout
            TimeUnit.MILLISECONDS,
            timeout,  // read timeout
            TimeUnit.MILLISECONDS,
            true      // follow redirects
        );
    }

    /**
     * Configure retry mechanism
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
            100,    // initial interval
            1000,   // max interval
            3       // max attempts
        );
    }

    /**
     * Configure logging level
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
} 