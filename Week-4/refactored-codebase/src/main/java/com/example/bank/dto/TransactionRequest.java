package com.example.bank.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Parameter Object for transaction requests, following the Parameter Object pattern
 * to replace long parameter lists and improve code readability.
 * 
 * This class encapsulates all the data needed to process a transaction request
 * and provides validation for the input parameters.
 * 
 * @author Refactored Banking System
 * @version 2.0
 */
public class TransactionRequest {
    private final String fromAccountNumber;
    private final String toAccountNumber;
    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final TransactionType type;
    private final boolean isUrgent;
    private final String approvalCode;
    private final String userId;
    private final String ipAddress;
    private final String userAgent;
    private final String sessionId;
    
    private TransactionRequest(Builder builder) {
        this.fromAccountNumber = builder.fromAccountNumber;
        this.toAccountNumber = builder.toAccountNumber;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.description = builder.description;
        this.type = builder.type;
        this.isUrgent = builder.isUrgent;
        this.approvalCode = builder.approvalCode;
        this.userId = builder.userId;
        this.ipAddress = builder.ipAddress;
        this.userAgent = builder.userAgent;
        this.sessionId = builder.sessionId;
        
        validateRequest();
    }
    
    // Getters
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public TransactionType getType() { return type; }
    public boolean isUrgent() { return isUrgent; }
    public String getApprovalCode() { return approvalCode; }
    public String getUserId() { return userId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getSessionId() { return sessionId; }
    
    /**
     * Checks if this request requires approval based on amount and urgency.
     * 
     * @return true if approval is required
     */
    public boolean requiresApproval() {
        return amount.compareTo(BigDecimal.valueOf(10000)) > 0 && !isUrgent;
    }
    
    /**
     * Checks if this request requires an approval code.
     * 
     * @return true if approval code is required
     */
    public boolean requiresApprovalCode() {
        return amount.compareTo(BigDecimal.valueOf(10000)) > 0 && isUrgent;
    }
    
    /**
     * Validates the transaction request parameters.
     * 
     * @throws IllegalArgumentException if any required parameters are invalid
     */
    private void validateRequest() {
        validateAccountNumber(fromAccountNumber, "From account");
        validateAmount(amount);
        validateCurrency(currency);
        validateDescription(description);
        validateTransactionType(type);
        validateUserId(userId);
        validateIpAddress(ipAddress);
        
        // Validate to account for transfers
        if (type == TransactionType.TRANSFER) {
            validateAccountNumber(toAccountNumber, "To account");
            if (fromAccountNumber.equals(toAccountNumber)) {
                throw new IllegalArgumentException("Cannot transfer to the same account");
            }
        }
        
        // Validate approval code if required
        if (requiresApprovalCode() && (approvalCode == null || approvalCode.trim().isEmpty())) {
            throw new IllegalArgumentException("Approval code required for large urgent transactions");
        }
    }
    
    private void validateAccountNumber(String accountNumber, String fieldName) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        if (!accountNumber.matches("^[A-Z0-9]{10}$")) {
            throw new IllegalArgumentException(fieldName + " must be 10 alphanumeric characters");
        }
    }
    
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount.compareTo(BigDecimal.valueOf(1000000)) > 0) {
            throw new IllegalArgumentException("Amount cannot exceed 1,000,000");
        }
    }
    
    private void validateCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (!currency.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO code");
        }
    }
    
    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (description.length() > 255) {
            throw new IllegalArgumentException("Description cannot exceed 255 characters");
        }
    }
    
    private void validateTransactionType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
    }
    
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
    
    private void validateIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        // Basic IP validation
        if (!ipAddress.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            throw new IllegalArgumentException("Invalid IP address format");
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransactionRequest that = (TransactionRequest) obj;
        return isUrgent == that.isUrgent &&
               Objects.equals(fromAccountNumber, that.fromAccountNumber) &&
               Objects.equals(toAccountNumber, that.toAccountNumber) &&
               Objects.equals(amount, that.amount) &&
               Objects.equals(currency, that.currency) &&
               Objects.equals(description, that.description) &&
               type == that.type &&
               Objects.equals(approvalCode, that.approvalCode) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(ipAddress, that.ipAddress) &&
               Objects.equals(userAgent, that.userAgent) &&
               Objects.equals(sessionId, that.sessionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(fromAccountNumber, toAccountNumber, amount, currency, 
                          description, type, isUrgent, approvalCode, userId, 
                          ipAddress, userAgent, sessionId);
    }
    
    @Override
    public String toString() {
        return String.format("TransactionRequest{type=%s, amount=%s %s, from='%s', to='%s', urgent=%s}", 
                           type, amount, currency, fromAccountNumber, toAccountNumber, isUrgent);
    }
    
    /**
     * Builder class for TransactionRequest following the Builder pattern.
     */
    public static class Builder {
        private String fromAccountNumber;
        private String toAccountNumber;
        private BigDecimal amount;
        private String currency = "USD";
        private String description;
        private TransactionType type;
        private boolean isUrgent = false;
        private String approvalCode;
        private String userId;
        private String ipAddress;
        private String userAgent;
        private String sessionId;
        
        public Builder fromAccount(String fromAccountNumber) {
            this.fromAccountNumber = fromAccountNumber;
            return this;
        }
        
        public Builder toAccount(String toAccountNumber) {
            this.toAccountNumber = toAccountNumber;
            return this;
        }
        
        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }
        
        public Builder urgent(boolean isUrgent) {
            this.isUrgent = isUrgent;
            return this;
        }
        
        public Builder approvalCode(String approvalCode) {
            this.approvalCode = approvalCode;
            return this;
        }
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public TransactionRequest build() {
            return new TransactionRequest(this);
        }
    }
    
    /**
     * Enum representing different types of transactions.
     */
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, REFUND
    }
} 