package com.rewards.customer.repository;

import com.rewards.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository interface for Customer entity operations
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * Find customer by email
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find customers by status
     */
    List<Customer> findByStatus(Customer.CustomerStatus status);

    /**
     * Find customers by tier level
     */
    List<Customer> findByTierLevel(Customer.TierLevel tierLevel);

    /**
     * Find customer with cashback accounts
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.cashbackAccounts WHERE c.customerId = :customerId")
    Optional<Customer> findByIdWithCashbackAccounts(@Param("customerId") String customerId);

    /**
     * Find customers by phone number
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Check if customer exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Count active customers
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = 'ACTIVE'")
    long countActiveCustomers();
} 