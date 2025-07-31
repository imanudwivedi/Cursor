package com.rewards.customer.repository;

import com.rewards.customer.entity.CashbackAccount;
import com.rewards.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CashbackAccount entity operations
 */
@Repository
public interface CashbackAccountRepository extends JpaRepository<CashbackAccount, Long> {

    /**
     * Find cashback accounts by customer
     */
    List<CashbackAccount> findByCustomer(Customer customer);

    /**
     * Find cashback accounts by customer ID
     */
    List<CashbackAccount> findByCustomerCustomerId(String customerId);

    /**
     * Find primary cashback account for customer
     */
    @Query("SELECT ca FROM CashbackAccount ca WHERE ca.customer.customerId = :customerId AND ca.accountType = 'PRIMARY'")
    Optional<CashbackAccount> findPrimaryAccountByCustomerId(@Param("customerId") String customerId);

    /**
     * Calculate total cashback balance for customer
     */
    @Query("SELECT COALESCE(SUM(ca.balance), 0) FROM CashbackAccount ca WHERE ca.customer.customerId = :customerId")
    BigDecimal getTotalCashbackBalance(@Param("customerId") String customerId);

    /**
     * Find accounts by account type
     */
    List<CashbackAccount> findByAccountType(CashbackAccount.AccountType accountType);

    /**
     * Find accounts with balance greater than specified amount
     */
    @Query("SELECT ca FROM CashbackAccount ca WHERE ca.balance > :minBalance")
    List<CashbackAccount> findAccountsWithBalanceGreaterThan(@Param("minBalance") BigDecimal minBalance);
} 