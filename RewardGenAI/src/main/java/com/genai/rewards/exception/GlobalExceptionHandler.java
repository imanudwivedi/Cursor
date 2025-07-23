package com.genai.rewards.exception;

import com.genai.rewards.dto.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<QueryResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = "Invalid request: " + errors.toString();
        log.warn("Validation error: {}", errorMessage);
        
        QueryResponse response = QueryResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
                
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle microservice communication errors
     */
    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<QueryResponse> handleFeignException(feign.FeignException ex) {
        log.error("Microservice communication error: {}", ex.getMessage());
        
        QueryResponse response = QueryResponse.builder()
                .success(false)
                .errorMessage("I'm having trouble accessing your reward information. Please try again in a moment.")
                .build();
                
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Handle circuit breaker open state
     */
    @ExceptionHandler(io.github.resilience4j.circuitbreaker.CallNotPermittedException.class)
    public ResponseEntity<QueryResponse> handleCircuitBreakerException(
            io.github.resilience4j.circuitbreaker.CallNotPermittedException ex) {
        
        log.warn("Circuit breaker is open: {}", ex.getMessage());
        
        QueryResponse response = QueryResponse.builder()
                .success(false)
                .errorMessage("Our systems are temporarily experiencing high load. Please try again in a few minutes.")
                .build();
                
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Handle general runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<QueryResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        QueryResponse response = QueryResponse.builder()
                .success(false)
                .errorMessage("I'm sorry, something went wrong while processing your request. Please try again.")
                .build();
                
        return ResponseEntity.internalServerError().body(response);
    }
} 