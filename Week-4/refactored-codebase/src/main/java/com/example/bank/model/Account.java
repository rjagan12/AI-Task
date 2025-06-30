package com.example.bank.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a bank account with proper encapsulation and security measures.
 * 
 * This class follows the Single Responsibility Principle by focusing solely
 * on account data management and basic validation.
 * 
 * @author Refactored Banking System
 * @version 2.0
 */
public class Account {
    private final String accountNumber;
    private BigDecimal balance;
    private final String accountHolderId;
    private final AccountType accountType;
    private final AccountStatus status;
    
    /**
     * Creates a new bank account with the specified parameters.
     * 
     * @param accountNumber unique identifier for the account
     * @param accountHolderId ID of the account holder
     * @param accountType type of account (SAVINGS, CHECKING, etc.)
     * @param initialBalance starting balance for the account
     * @throws IllegalArgumentException if accountNumber or accountHolderId is null/empty
     */
    public Account(String accountNumber, String accountHolderId, 
                   AccountType accountType, BigDecimal initialBalance) {
        this.accountNumber = validateAccountNumber(accountNumber);
        this.accountHolderId = validateAccountHolderId(accountHolderId);
        this.accountType = Objects.requireNonNull(accountType, "Account type cannot be null");
        this.balance = validateInitialBalance(initialBalance);
        this.status = AccountStatus.ACTIVE;
    }
    
    /**
     * Deposits the specified amount into the account.
     * 
     * @param amount the amount to deposit (must be positive)
     * @throws IllegalArgumentException if amount is null or negative
     * @throws IllegalStateException if account is not active
     */
    public void deposit(BigDecimal amount) {
        validateAccountStatus();
        BigDecimal validAmount = validatePositiveAmount(amount, "Deposit amount");
        this.balance = this.balance.add(validAmount);
    }
    
    /**
     * Withdraws the specified amount from the account.
     * 
     * @param amount the amount to withdraw (must be positive and not exceed balance)
     * @throws IllegalArgumentException if amount is null, negative, or exceeds balance
     * @throws IllegalStateException if account is not active
     */
    public void withdraw(BigDecimal amount) {
        validateAccountStatus();
        BigDecimal validAmount = validatePositiveAmount(amount, "Withdrawal amount");
        
        if (validAmount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
        
        this.balance = this.balance.subtract(validAmount);
    }
    
    /**
     * Transfers the specified amount to another account.
     * 
     * @param amount the amount to transfer
     * @param targetAccount the account to transfer to
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if either account is not active
     */
    public void transfer(BigDecimal amount, Account targetAccount) {
        validateAccountStatus();
        Objects.requireNonNull(targetAccount, "Target account cannot be null");
        targetAccount.validateAccountStatus();
        
        BigDecimal validAmount = validatePositiveAmount(amount, "Transfer amount");
        
        if (validAmount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Insufficient funds for transfer");
        }
        
        // Atomic transfer operation
        this.balance = this.balance.subtract(validAmount);
        targetAccount.balance = targetAccount.balance.add(validAmount);
    }
    
    /**
     * Gets the current account balance.
     * 
     * @return the current balance
     */
    public BigDecimal getBalance() {
        return this.balance;
    }
    
    /**
     * Gets the account number.
     * 
     * @return the account number
     */
    public String getAccountNumber() {
        return this.accountNumber;
    }
    
    /**
     * Gets the account holder ID.
     * 
     * @return the account holder ID
     */
    public String getAccountHolderId() {
        return this.accountHolderId;
    }
    
    /**
     * Gets the account type.
     * 
     * @return the account type
     */
    public AccountType getAccountType() {
        return this.accountType;
    }
    
    /**
     * Gets the account status.
     * 
     * @return the account status
     */
    public AccountStatus getStatus() {
        return this.status;
    }
    
    /**
     * Checks if the account has sufficient funds for the specified amount.
     * 
     * @param amount the amount to check
     * @return true if sufficient funds are available
     */
    public boolean hasSufficientFunds(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0 
               && amount.compareTo(this.balance) <= 0;
    }
    
    // Private validation methods
    private String validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (!accountNumber.matches("^[A-Z0-9]{10}$")) {
            throw new IllegalArgumentException("Account number must be 10 alphanumeric characters");
        }
        return accountNumber.trim();
    }
    
    private String validateAccountHolderId(String accountHolderId) {
        if (accountHolderId == null || accountHolderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder ID cannot be null or empty");
        }
        return accountHolderId.trim();
    }
    
    private BigDecimal validateInitialBalance(BigDecimal initialBalance) {
        if (initialBalance == null) {
            return BigDecimal.ZERO;
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        return initialBalance;
    }
    
    private BigDecimal validatePositiveAmount(BigDecimal amount, String operation) {
        if (amount == null) {
            throw new IllegalArgumentException(operation + " cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(operation + " must be positive");
        }
        return amount;
    }
    
    private void validateAccountStatus() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active. Current status: " + this.status);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return Objects.equals(accountNumber, account.accountNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
    
    @Override
    public String toString() {
        return String.format("Account{accountNumber='%s', balance=%s, accountType=%s, status=%s}", 
                           accountNumber, balance, accountType, status);
    }
    
    /**
     * Enum representing different types of bank accounts.
     */
    public enum AccountType {
        SAVINGS, CHECKING, BUSINESS, INVESTMENT
    }
    
    /**
     * Enum representing the status of an account.
     */
    public enum AccountStatus {
        ACTIVE, SUSPENDED, CLOSED, PENDING
    }
} 