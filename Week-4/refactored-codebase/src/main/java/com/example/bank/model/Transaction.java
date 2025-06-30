package com.example.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a financial transaction with proper validation and immutability.
 * 
 * This class follows the Single Responsibility Principle by focusing solely
 * on transaction data management and validation.
 * 
 * @author Refactored Banking System
 * @version 2.0
 */
public class Transaction {
    private final String transactionId;
    private final String fromAccountNumber;
    private final String toAccountNumber;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String description;
    private final LocalDateTime timestamp;
    private final TransactionStatus status;
    private final String currency;
    
    /**
     * Creates a new transaction with the specified parameters.
     * 
     * @param fromAccountNumber source account number
     * @param toAccountNumber destination account number (can be null for deposits/withdrawals)
     * @param amount transaction amount
     * @param type type of transaction
     * @param description transaction description
     * @param currency currency code
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Transaction(String fromAccountNumber, String toAccountNumber, 
                      BigDecimal amount, TransactionType type, 
                      String description, String currency) {
        this.transactionId = UUID.randomUUID().toString();
        this.fromAccountNumber = validateAccountNumber(fromAccountNumber, "From account");
        this.toAccountNumber = validateOptionalAccountNumber(toAccountNumber, "To account");
        this.amount = validateAmount(amount);
        this.type = Objects.requireNonNull(type, "Transaction type cannot be null");
        this.description = validateDescription(description);
        this.currency = validateCurrency(currency);
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }
    
    /**
     * Creates a new transaction with an existing transaction ID (for database operations).
     * 
     * @param transactionId existing transaction ID
     * @param fromAccountNumber source account number
     * @param toAccountNumber destination account number
     * @param amount transaction amount
     * @param type type of transaction
     * @param description transaction description
     * @param currency currency code
     * @param timestamp transaction timestamp
     * @param status transaction status
     */
    public Transaction(String transactionId, String fromAccountNumber, String toAccountNumber,
                      BigDecimal amount, TransactionType type, String description,
                      String currency, LocalDateTime timestamp, TransactionStatus status) {
        this.transactionId = Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
        this.fromAccountNumber = validateAccountNumber(fromAccountNumber, "From account");
        this.toAccountNumber = validateOptionalAccountNumber(toAccountNumber, "To account");
        this.amount = validateAmount(amount);
        this.type = Objects.requireNonNull(type, "Transaction type cannot be null");
        this.description = validateDescription(description);
        this.currency = validateCurrency(currency);
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }
    
    // Getters
    public String getTransactionId() { return transactionId; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionStatus getStatus() { return status; }
    public String getCurrency() { return currency; }
    
    /**
     * Checks if this transaction is a transfer between accounts.
     * 
     * @return true if this is a transfer transaction
     */
    public boolean isTransfer() {
        return type == TransactionType.TRANSFER && toAccountNumber != null;
    }
    
    /**
     * Checks if this transaction is a deposit.
     * 
     * @return true if this is a deposit transaction
     */
    public boolean isDeposit() {
        return type == TransactionType.DEPOSIT;
    }
    
    /**
     * Checks if this transaction is a withdrawal.
     * 
     * @return true if this is a withdrawal transaction
     */
    public boolean isWithdrawal() {
        return type == TransactionType.WITHDRAWAL;
    }
    
    /**
     * Checks if this transaction is pending.
     * 
     * @return true if transaction status is pending
     */
    public boolean isPending() {
        return status == TransactionStatus.PENDING;
    }
    
    /**
     * Checks if this transaction is completed.
     * 
     * @return true if transaction status is completed
     */
    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }
    
    /**
     * Checks if this transaction is failed.
     * 
     * @return true if transaction status is failed
     */
    public boolean isFailed() {
        return status == TransactionStatus.FAILED;
    }
    
    // Private validation methods
    private String validateAccountNumber(String accountNumber, String fieldName) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        if (!accountNumber.matches("^[A-Z0-9]{10}$")) {
            throw new IllegalArgumentException(fieldName + " must be 10 alphanumeric characters");
        }
        return accountNumber.trim();
    }
    
    private String validateOptionalAccountNumber(String accountNumber, String fieldName) {
        if (accountNumber == null) {
            return null;
        }
        return validateAccountNumber(accountNumber, fieldName);
    }
    
    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return amount;
    }
    
    private String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (description.length() > 255) {
            throw new IllegalArgumentException("Description cannot exceed 255 characters");
        }
        return description.trim();
    }
    
    private String validateCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (!currency.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO code");
        }
        return currency.trim();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return Objects.equals(transactionId, that.transactionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    @Override
    public String toString() {
        return String.format("Transaction{id='%s', type=%s, amount=%s %s, from='%s', to='%s', status=%s}", 
                           transactionId, type, amount, currency, fromAccountNumber, toAccountNumber, status);
    }
    
    /**
     * Enum representing different types of transactions.
     */
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, REFUND
    }
    
    /**
     * Enum representing the status of a transaction.
     */
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
} 