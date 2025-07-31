package com.rewards.customer.controller;

import com.rewards.customer.dto.CashbackBalanceResponse;
import com.rewards.customer.dto.CustomerResponseDto;
import com.rewards.customer.entity.CashbackAccount;
import com.rewards.customer.entity.Customer;
import com.rewards.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Customer Service operations
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Service", description = "Customer management and cashback operations")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Get cashback balance for customer - Main endpoint for RewardQueryBot integration
     */
    @GetMapping("/{customerId}/cashback")
    @Operation(summary = "Get customer cashback balance", description = "Returns the total cashback balance for a customer")
    public ResponseEntity<CashbackBalanceResponse> getCashbackBalance(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {
        
        log.info("Getting cashback balance for customer: {}", customerId);
        
        CashbackBalanceResponse response = customerService.getCashbackBalance(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer profile
     */
    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer profile", description = "Returns complete customer profile with cashback accounts")
    public ResponseEntity<CustomerResponseDto> getCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {
        
        log.info("Getting customer profile for: {}", customerId);
        
        return customerService.getCustomer(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all customers
     */
    @GetMapping
    @Operation(summary = "Get all customers", description = "Returns list of all customers")
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        log.info("Getting all customers");
        
        List<CustomerResponseDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Create new customer
     */
    @PostMapping
    @Operation(summary = "Create customer", description = "Creates a new customer with primary cashback account")
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody Customer customer) {
        log.info("Creating new customer: {}", customer.getEmail());
        
        CustomerResponseDto createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.ok(createdCustomer);
    }

    /**
     * Update customer
     */
    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Updates customer information")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId,
            @Valid @RequestBody Customer customer) {
        
        log.info("Updating customer: {}", customerId);
        
        return customerService.updateCustomer(customerId, customer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer", description = "Deletes customer and associated data")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {
        
        log.info("Deleting customer: {}", customerId);
        
        boolean deleted = customerService.deleteCustomer(customerId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Add cashback to customer account
     */
    @PostMapping("/{customerId}/cashback/add")
    @Operation(summary = "Add cashback", description = "Adds cashback amount to customer account")
    public ResponseEntity<Void> addCashback(
            @Parameter(description = "Customer ID") @PathVariable String customerId,
            @Parameter(description = "Cashback amount") @RequestParam BigDecimal amount,
            @Parameter(description = "Account type") @RequestParam(defaultValue = "PRIMARY") CashbackAccount.AccountType accountType) {
        
        log.info("Adding cashback {} to customer: {}", amount, customerId);
        
        customerService.addCashback(customerId, amount, accountType);
        return ResponseEntity.ok().build();
    }

    /**
     * Redeem cashback from customer account
     */
    @PostMapping("/{customerId}/cashback/redeem")
    @Operation(summary = "Redeem cashback", description = "Redeems cashback amount from customer account")
    public ResponseEntity<Void> redeemCashback(
            @Parameter(description = "Customer ID") @PathVariable String customerId,
            @Parameter(description = "Redemption amount") @RequestParam BigDecimal amount) {
        
        log.info("Redeeming cashback {} for customer: {}", amount, customerId);
        
        boolean redeemed = customerService.redeemCashback(customerId, amount);
        return redeemed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns service health status")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer Service is healthy");
    }
} 