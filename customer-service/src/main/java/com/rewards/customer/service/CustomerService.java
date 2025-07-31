package com.rewards.customer.service;

import com.rewards.customer.dto.CashbackBalanceResponse;
import com.rewards.customer.dto.CustomerResponseDto;
import com.rewards.customer.entity.CashbackAccount;
import com.rewards.customer.entity.Customer;
import com.rewards.customer.repository.CashbackAccountRepository;
import com.rewards.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for customer operations and cashback management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CashbackAccountRepository cashbackAccountRepository;

    /**
     * Get customer by ID
     */
    public Optional<CustomerResponseDto> getCustomer(String customerId) {
        log.debug("Getting customer with ID: {}", customerId);
        
        return customerRepository.findByIdWithCashbackAccounts(customerId)
                .map(this::convertToDto);
    }

    /**
     * Get cashback balance for customer - API endpoint for RewardQueryBot
     */
    public CashbackBalanceResponse getCashbackBalance(String customerId) {
        log.debug("Getting cashback balance for customer: {}", customerId);
        
        BigDecimal totalBalance = cashbackAccountRepository.getTotalCashbackBalance(customerId);
        
        return CashbackBalanceResponse.builder()
                .balance(totalBalance)
                .currency("USD")
                .build();
    }

    /**
     * Get customer profile with detailed information
     */
    public Optional<CustomerResponseDto> getCustomerProfile(String customerId) {
        log.debug("Getting customer profile for: {}", customerId);
        
        return customerRepository.findByIdWithCashbackAccounts(customerId)
                .map(this::convertToDto);
    }

    /**
     * Create new customer
     */
    @Transactional
    public CustomerResponseDto createCustomer(Customer customer) {
        log.info("Creating new customer: {}", customer.getEmail());
        
        Customer savedCustomer = customerRepository.save(customer);
        
        // Create primary cashback account
        createPrimaryCashbackAccount(savedCustomer);
        
        return convertToDto(savedCustomer);
    }

    /**
     * Update customer information
     */
    @Transactional
    public Optional<CustomerResponseDto> updateCustomer(String customerId, Customer customerUpdates) {
        log.info("Updating customer: {}", customerId);
        
        return customerRepository.findById(customerId)
                .map(existingCustomer -> {
                    updateCustomerFields(existingCustomer, customerUpdates);
                    Customer savedCustomer = customerRepository.save(existingCustomer);
                    return convertToDto(savedCustomer);
                });
    }

    /**
     * Add cashback to customer account
     */
    @Transactional
    public void addCashback(String customerId, BigDecimal amount, CashbackAccount.AccountType accountType) {
        log.info("Adding cashback {} to customer: {} (account type: {})", amount, customerId, accountType);
        
        CashbackAccount account = getCashbackAccount(customerId, accountType);
        account.setBalance(account.getBalance().add(amount));
        account.setTotalEarned(account.getTotalEarned().add(amount));
        
        cashbackAccountRepository.save(account);
    }

    /**
     * Redeem cashback from customer account
     */
    @Transactional
    public boolean redeemCashback(String customerId, BigDecimal amount) {
        log.info("Redeeming cashback {} for customer: {}", amount, customerId);
        
        Optional<CashbackAccount> primaryAccount = cashbackAccountRepository
                .findPrimaryAccountByCustomerId(customerId);
        
        if (primaryAccount.isPresent() && primaryAccount.get().getBalance().compareTo(amount) >= 0) {
            CashbackAccount account = primaryAccount.get();
            account.setBalance(account.getBalance().subtract(amount));
            account.setTotalRedeemed(account.getTotalRedeemed().add(amount));
            
            cashbackAccountRepository.save(account);
            return true;
        }
        
        return false;
    }

    /**
     * Get all customers
     */
    public List<CustomerResponseDto> getAllCustomers() {
        log.debug("Getting all customers");
        
        return customerRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete customer
     */
    @Transactional
    public boolean deleteCustomer(String customerId) {
        log.info("Deleting customer: {}", customerId);
        
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    // Private helper methods

    private void createPrimaryCashbackAccount(Customer customer) {
        CashbackAccount primaryAccount = CashbackAccount.builder()
                .customer(customer)
                .accountType(CashbackAccount.AccountType.PRIMARY)
                .balance(BigDecimal.ZERO)
                .totalEarned(BigDecimal.ZERO)
                .totalRedeemed(BigDecimal.ZERO)
                .currency("USD")
                .build();
        
        cashbackAccountRepository.save(primaryAccount);
    }

    private CashbackAccount getCashbackAccount(String customerId, CashbackAccount.AccountType accountType) {
        if (accountType == CashbackAccount.AccountType.PRIMARY) {
            return cashbackAccountRepository.findPrimaryAccountByCustomerId(customerId)
                    .orElseThrow(() -> new RuntimeException("Primary cashback account not found for customer: " + customerId));
        }
        
        // For other account types, create if doesn't exist
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        
        return CashbackAccount.builder()
                .customer(customer)
                .accountType(accountType)
                .balance(BigDecimal.ZERO)
                .totalEarned(BigDecimal.ZERO)
                .totalRedeemed(BigDecimal.ZERO)
                .currency("USD")
                .build();
    }

    private void updateCustomerFields(Customer existing, Customer updates) {
        if (updates.getFirstName() != null) {
            existing.setFirstName(updates.getFirstName());
        }
        if (updates.getLastName() != null) {
            existing.setLastName(updates.getLastName());
        }
        if (updates.getEmail() != null) {
            existing.setEmail(updates.getEmail());
        }
        if (updates.getPhoneNumber() != null) {
            existing.setPhoneNumber(updates.getPhoneNumber());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        if (updates.getTierLevel() != null) {
            existing.setTierLevel(updates.getTierLevel());
        }
        if (updates.getTotalSpend() != null) {
            existing.setTotalSpend(updates.getTotalSpend());
        }
    }

    private CustomerResponseDto convertToDto(Customer customer) {
        List<CustomerResponseDto.CashbackAccountDto> cashbackDtos = customer.getCashbackAccounts() != null ?
                customer.getCashbackAccounts().stream()
                        .map(this::convertCashbackAccountToDto)
                        .collect(Collectors.toList()) : List.of();

        return CustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .status(customer.getStatus() != null ? customer.getStatus().name() : null)
                .tierLevel(customer.getTierLevel() != null ? customer.getTierLevel().name() : null)
                .registrationDate(customer.getRegistrationDate())
                .lastLoginDate(customer.getLastLoginDate())
                .totalSpend(customer.getTotalSpend())
                .cashbackAccounts(cashbackDtos)
                .build();
    }

    private CustomerResponseDto.CashbackAccountDto convertCashbackAccountToDto(CashbackAccount account) {
        return CustomerResponseDto.CashbackAccountDto.builder()
                .id(account.getId())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .totalEarned(account.getTotalEarned())
                .totalRedeemed(account.getTotalRedeemed())
                .currency(account.getCurrency())
                .lastTransactionDate(account.getLastTransactionDate())
                .build();
    }
} 