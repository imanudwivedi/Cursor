package com.genai.rewardbot.auth.service;

import com.genai.rewardbot.auth.repository.UserRepository;
import com.genai.rewardbot.auth.util.JwtUtil;
import com.genai.rewardbot.common.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationResponse authenticateUser(String mobileNumber) {
        log.info("Authenticating user with mobile number: {}", mobileNumber);
        
        Optional<User> userOptional = userRepository.findByMobileNumber(mobileNumber);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getIsActive()) {
                log.warn("User with mobile number {} is inactive", mobileNumber);
                return new AuthenticationResponse(false, "User account is inactive", null, null, null);
            }
            
            String token = jwtUtil.generateToken(mobileNumber, user.getId());
            log.info("User authenticated successfully: {}", mobileNumber);
            
            return new AuthenticationResponse(true, "Authentication successful", token, user.getId(), user);
        } else {
            log.warn("User not found with mobile number: {}", mobileNumber);
            return new AuthenticationResponse(false, "User not found", null, null, null);
        }
    }

    public AuthenticationResponse registerUser(String mobileNumber, String firstName, String lastName, String email) {
        log.info("Registering new user with mobile number: {}", mobileNumber);
        
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            log.warn("User already exists with mobile number: {}", mobileNumber);
            return new AuthenticationResponse(false, "User already exists", null, null, null);
        }
        
        User newUser = new User(mobileNumber, firstName, lastName, email);
        User savedUser = userRepository.save(newUser);
        
        String token = jwtUtil.generateToken(mobileNumber, savedUser.getId());
        log.info("User registered successfully: {}", mobileNumber);
        
        return new AuthenticationResponse(true, "Registration successful", token, savedUser.getId(), savedUser);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractMobileNumber(String token) {
        return jwtUtil.extractMobileNumber(token);
    }

    public Long extractUserId(String token) {
        return jwtUtil.extractUserId(token);
    }

    public Optional<User> getUserByMobileNumber(String mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber);
    }

    public Optional<User> getUserWithCardsAndRewards(String mobileNumber) {
        return userRepository.findByMobileNumberWithCardsAndRewards(mobileNumber);
    }

    // Inner class for authentication response
    public static class AuthenticationResponse {
        private boolean success;
        private String message;
        private String token;
        private Long userId;
        private User user;

        public AuthenticationResponse(boolean success, String message, String token, Long userId, User user) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.userId = userId;
            this.user = user;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getToken() { return token; }
        public Long getUserId() { return userId; }
        public User getUser() { return user; }
    }
} 