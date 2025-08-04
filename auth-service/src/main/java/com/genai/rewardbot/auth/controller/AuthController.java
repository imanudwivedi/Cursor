package com.genai.rewardbot.auth.controller;

import com.genai.rewardbot.auth.service.AuthService;
import com.genai.rewardbot.common.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for mobile number: {}", request.getMobileNumber());
        
        AuthService.AuthenticationResponse response = authService.authenticateUser(request.getMobileNumber());
        
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", response.isSuccess());
        responseMap.put("message", response.getMessage());
        
        if (response.isSuccess()) {
            responseMap.put("token", response.getToken());
            responseMap.put("userId", response.getUserId());
            responseMap.put("user", response.getUser());
        }
        
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for mobile number: {}", request.getMobileNumber());
        
        AuthService.AuthenticationResponse response = authService.registerUser(
            request.getMobileNumber(),
            request.getFirstName(),
            request.getLastName(),
            request.getEmail()
        );
        
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", response.isSuccess());
        responseMap.put("message", response.getMessage());
        
        if (response.isSuccess()) {
            responseMap.put("token", response.getToken());
            responseMap.put("userId", response.getUserId());
            responseMap.put("user", response.getUser());
        }
        
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = authHeader.replace("Bearer ", "");
            boolean isValid = authService.validateToken(token);
            
            response.put("valid", isValid);
            
            if (isValid) {
                String mobileNumber = authService.extractMobileNumber(token);
                Long userId = authService.extractUserId(token);
                response.put("mobileNumber", mobileNumber);
                response.put("userId", userId);
            }
            
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            response.put("valid", false);
            response.put("error", "Invalid token format");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{mobileNumber}")
    public ResponseEntity<Map<String, Object>> getUserByMobileNumber(@PathVariable String mobileNumber) {
        log.info("Get user request for mobile number: {}", mobileNumber);
        
        Optional<User> userOptional = authService.getUserByMobileNumber(mobileNumber);
        Map<String, Object> response = new HashMap<>();
        
        if (userOptional.isPresent()) {
            response.put("success", true);
            response.put("user", userOptional.get());
        } else {
            response.put("success", false);
            response.put("message", "User not found");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "auth-service");
        return ResponseEntity.ok(response);
    }

    // Request DTOs
    public static class LoginRequest {
        private String mobileNumber;

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    }

    public static class RegisterRequest {
        private String mobileNumber;
        private String firstName;
        private String lastName;
        private String email;

        // Getters and setters
        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 