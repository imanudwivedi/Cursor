package com.rewards.customer.config;

import com.rewards.customer.entity.CashbackAccount;
import com.rewards.customer.entity.Customer;
import com.rewards.customer.repository.CashbackAccountRepository;
import com.rewards.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Initializes sample data for development and testing
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final CashbackAccountRepository cashbackAccountRepository;

    @Value("${app.data.init-sample-data:true}")
    private boolean initSampleData;

    @Override
    public void run(String... args) {
        if (initSampleData && customerRepository.count() == 0) {
            log.info("Initializing sample customer data...");
            initializeCustomerData();
        }
    }

    private void initializeCustomerData() {
        // Create sample customers
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId("customer123")
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .phoneNumber("+1-555-0101")
                        .status(Customer.CustomerStatus.ACTIVE)
                        .tierLevel(Customer.TierLevel.GOLD)
                        .registrationDate(LocalDateTime.now().minusMonths(6))
                        .lastLoginDate(LocalDateTime.now().minusDays(1))
                        .totalSpend(new BigDecimal("2500.00"))
                        .build(),

                Customer.builder()
                        .customerId("customer456")
                        .firstName("Jane")
                        .lastName("Smith")
                        .email("jane.smith@example.com")
                        .phoneNumber("+1-555-0102")
                        .status(Customer.CustomerStatus.ACTIVE)
                        .tierLevel(Customer.TierLevel.SILVER)
                        .registrationDate(LocalDateTime.now().minusMonths(3))
                        .lastLoginDate(LocalDateTime.now().minusHours(2))
                        .totalSpend(new BigDecimal("1200.00"))
                        .build(),

                Customer.builder()
                        .customerId("customer789")
                        .firstName("Michael")
                        .lastName("Johnson")
                        .email("michael.johnson@example.com")
                        .phoneNumber("+1-555-0103")
                        .status(Customer.CustomerStatus.ACTIVE)
                        .tierLevel(Customer.TierLevel.PLATINUM)
                        .registrationDate(LocalDateTime.now().minusYears(1))
                        .lastLoginDate(LocalDateTime.now().minusDays(3))
                        .totalSpend(new BigDecimal("5000.00"))
                        .build(),

                Customer.builder()
                        .customerId("customer999")
                        .firstName("Sarah")
                        .lastName("Wilson")
                        .email("sarah.wilson@example.com")
                        .phoneNumber("+1-555-0104")
                        .status(Customer.CustomerStatus.ACTIVE)
                        .tierLevel(Customer.TierLevel.BRONZE)
                        .registrationDate(LocalDateTime.now().minusWeeks(2))
                        .lastLoginDate(LocalDateTime.now().minusHours(6))
                        .totalSpend(new BigDecimal("350.00"))
                        .build()
        );

        List<Customer> savedCustomers = customerRepository.saveAll(customers);
        log.info("Created {} sample customers", savedCustomers.size());

        // Create cashback accounts for each customer
        savedCustomers.forEach(this::createCashbackAccounts);
        
        log.info("Sample data initialization completed");
    }

    private void createCashbackAccounts(Customer customer) {
        // Primary cashback account
        CashbackAccount primaryAccount = CashbackAccount.builder()
                .customer(customer)
                .accountType(CashbackAccount.AccountType.PRIMARY)
                .balance(calculateCashbackBalance(customer.getTotalSpend()))
                .totalEarned(calculateTotalEarned(customer.getTotalSpend()))
                .totalRedeemed(BigDecimal.ZERO)
                .currency("USD")
                .lastTransactionDate(LocalDateTime.now().minusDays(1))
                .build();

        // Bonus cashback account for higher tier customers
        CashbackAccount bonusAccount = null;
        if (customer.getTierLevel() == Customer.TierLevel.GOLD || 
            customer.getTierLevel() == Customer.TierLevel.PLATINUM ||
            customer.getTierLevel() == Customer.TierLevel.DIAMOND) {
            
            bonusAccount = CashbackAccount.builder()
                    .customer(customer)
                    .accountType(CashbackAccount.AccountType.BONUS)
                    .balance(new BigDecimal("15.50"))
                    .totalEarned(new BigDecimal("15.50"))
                    .totalRedeemed(BigDecimal.ZERO)
                    .currency("USD")
                    .lastTransactionDate(LocalDateTime.now().minusDays(2))
                    .build();
        }

        cashbackAccountRepository.save(primaryAccount);
        if (bonusAccount != null) {
            cashbackAccountRepository.save(bonusAccount);
        }

        log.debug("Created cashback accounts for customer: {}", customer.getCustomerId());
    }

    private BigDecimal calculateCashbackBalance(BigDecimal totalSpend) {
        // 1% cashback rate
        return totalSpend.multiply(new BigDecimal("0.01"));
    }

    private BigDecimal calculateTotalEarned(BigDecimal totalSpend) {
        // Slightly higher than current balance to show some redemption history
        return totalSpend.multiply(new BigDecimal("0.012"));
    }
} 