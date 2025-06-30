package com.example.bank;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;

/**
 * Transaction processing system with performance and design issues
 */
public class TransactionProcessor {
    // Bad: global state
    private static List<Transaction> pendingTransactions = new ArrayList<>();
    private static Map<String, BigDecimal> accountBalances = new HashMap<>();
    private static int processedCount = 0;
    
    // Bad: hardcoded limits
    private static final double DAILY_LIMIT = 10000.0;
    private static final double MONTHLY_LIMIT = 50000.0;
    
    public TransactionProcessor() {
        // Bad: initializing with test data
        accountBalances.put("ACC001", new BigDecimal("1000.00"));
        accountBalances.put("ACC002", new BigDecimal("2500.00"));
        accountBalances.put("ACC003", new BigDecimal("500.00"));
    }
    
    // Bad: extremely complex method with multiple responsibilities
    public boolean processTransaction(String transactionId, String fromAccount, String toAccount, 
                                    BigDecimal amount, String currency, String description, 
                                    String transactionType, boolean isUrgent, String approvalCode, 
                                    String userId, String ipAddress, String userAgent, 
                                    String sessionId, boolean isMobile, String deviceId, 
                                    String location, String timezone, String language, 
                                    String channel, String productCode, String feeCode, 
                                    String taxCode, String exchangeRate, String commission, 
                                    String discount, String loyaltyPoints, String riskScore, 
                                    String fraudScore, String complianceScore, String auditTrail, 
                                    String metadata, String signature, String timestamp) {
        
        try {
            // Bad: complex validation logic
            if (fromAccount == null || fromAccount.isEmpty()) {
                System.out.println("Error: From account is required");
                return false;
            }
            
            if (toAccount == null || toAccount.isEmpty()) {
                System.out.println("Error: To account is required");
                return false;
            }
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Error: Invalid amount");
                return false;
            }
            
            if (currency == null || currency.isEmpty()) {
                System.out.println("Error: Currency is required");
                return false;
            }
            
            if (description == null || description.isEmpty()) {
                System.out.println("Error: Description is required");
                return false;
            }
            
            if (transactionType == null || transactionType.isEmpty()) {
                System.out.println("Error: Transaction type is required");
                return false;
            }
            
            if (userId == null || userId.isEmpty()) {
                System.out.println("Error: User ID is required");
                return false;
            }
            
            // Bad: complex business logic with nested conditions
            if (transactionType.equals("TRANSFER")) {
                if (amount.compareTo(new BigDecimal(DAILY_LIMIT)) > 0) {
                    if (isUrgent) {
                        if (approvalCode != null && !approvalCode.isEmpty()) {
                            if (validateApprovalCode(approvalCode)) {
                                if (checkDailyLimit(fromAccount, amount)) {
                                    if (checkMonthlyLimit(fromAccount, amount)) {
                                        if (checkAccountBalance(fromAccount, amount)) {
                                            if (validateAccount(toAccount)) {
                                                if (checkFraudScore(riskScore, fraudScore)) {
                                                    if (checkCompliance(complianceScore)) {
                                                        if (processTransfer(fromAccount, toAccount, amount)) {
                                                            if (updateAuditTrail(transactionId, auditTrail)) {
                                                                if (sendNotification(userId, amount)) {
                                                                    if (updateLoyaltyPoints(userId, loyaltyPoints)) {
                                                                        if (calculateFees(feeCode, amount)) {
                                                                            if (calculateTaxes(taxCode, amount)) {
                                                                                if (updateExchangeRate(exchangeRate)) {
                                                                                    if (processCommission(commission)) {
                                                                                        if (applyDiscount(discount)) {
                                                                                            if (updateMetadata(metadata)) {
                                                                                                if (validateSignature(signature)) {
                                                                                                    if (updateTimestamp(timestamp)) {
                                                                                                        processedCount++;
                                                                                                        return true;
                                                                                                    } else {
                                                                                                        System.out.println("Error: Invalid timestamp");
                                                                                                        return false;
                                                                                                    }
                                                                                                } else {
                                                                                                    System.out.println("Error: Invalid signature");
                                                                                                    return false;
                                                                                                }
                                                                                            } else {
                                                                                                System.out.println("Error: Failed to update metadata");
                                                                                                return false;
                                                                                            }
                                                                                        } else {
                                                                                            System.out.println("Error: Failed to apply discount");
                                                                                            return false;
                                                                                        }
                                                                                    } else {
                                                                                        System.out.println("Error: Failed to process commission");
                                                                                        return false;
                                                                                    }
                                                                                } else {
                                                                                    System.out.println("Error: Failed to update exchange rate");
                                                                                    return false;
                                                                                }
                                                                            } else {
                                                                                System.out.println("Error: Failed to calculate taxes");
                                                                                return false;
                                                                            }
                                                                        } else {
                                                                            System.out.println("Error: Failed to calculate fees");
                                                                            return false;
                                                                        }
                                                                    } else {
                                                                        System.out.println("Error: Failed to update loyalty points");
                                                                        return false;
                                                                    }
                                                                } else {
                                                                    System.out.println("Error: Failed to send notification");
                                                                    return false;
                                                                }
                                                            } else {
                                                                System.out.println("Error: Failed to update audit trail");
                                                                return false;
                                                            }
                                                        } else {
                                                            System.out.println("Error: Failed to process transfer");
                                                            return false;
                                                        }
                                                    } else {
                                                        System.out.println("Error: Compliance check failed");
                                                        return false;
                                                    }
                                                } else {
                                                    System.out.println("Error: Fraud check failed");
                                                    return false;
                                                }
                                            } else {
                                                System.out.println("Error: Invalid destination account");
                                                return false;
                                            }
                                        } else {
                                            System.out.println("Error: Insufficient funds");
                                            return false;
                                        }
                                    } else {
                                        System.out.println("Error: Monthly limit exceeded");
                                        return false;
                                    }
                                } else {
                                    System.out.println("Error: Daily limit exceeded");
                                    return false;
                                }
                            } else {
                                System.out.println("Error: Invalid approval code");
                                return false;
                            }
                        } else {
                            System.out.println("Error: Approval code required for large amounts");
                            return false;
                        }
                    } else {
                        System.out.println("Error: Urgent flag required for large amounts");
                        return false;
                    }
                } else {
                    // Process normal transfer
                    if (checkDailyLimit(fromAccount, amount)) {
                        if (checkMonthlyLimit(fromAccount, amount)) {
                            if (checkAccountBalance(fromAccount, amount)) {
                                if (validateAccount(toAccount)) {
                                    if (processTransfer(fromAccount, toAccount, amount)) {
                                        processedCount++;
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (transactionType.equals("DEPOSIT")) {
                // Similar complex logic for deposits
                if (amount.compareTo(new BigDecimal(DAILY_LIMIT)) > 0) {
                    if (isUrgent && approvalCode != null && validateApprovalCode(approvalCode)) {
                        if (processDeposit(toAccount, amount)) {
                            processedCount++;
                            return true;
                        }
                    } else {
                        System.out.println("Error: Large deposits require urgent flag and approval code");
                        return false;
                    }
                } else {
                    if (processDeposit(toAccount, amount)) {
                        processedCount++;
                        return true;
                    }
                }
            } else if (transactionType.equals("WITHDRAWAL")) {
                // Similar complex logic for withdrawals
                if (amount.compareTo(new BigDecimal(DAILY_LIMIT)) > 0) {
                    if (isUrgent && approvalCode != null && validateApprovalCode(approvalCode)) {
                        if (checkAccountBalance(fromAccount, amount) && processWithdrawal(fromAccount, amount)) {
                            processedCount++;
                            return true;
                        }
                    } else {
                        System.out.println("Error: Large withdrawals require urgent flag and approval code");
                        return false;
                    }
                } else {
                    if (checkAccountBalance(fromAccount, amount) && processWithdrawal(fromAccount, amount)) {
                        processedCount++;
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.out.println("Transaction processing error: " + e.getMessage());
            return false;
        }
    }
    
    // Bad: inefficient helper methods
    private boolean validateApprovalCode(String approvalCode) {
        // Bad: simple validation
        return approvalCode != null && approvalCode.length() >= 6;
    }
    
    private boolean checkDailyLimit(String account, BigDecimal amount) {
        // Bad: inefficient daily limit check
        BigDecimal dailyTotal = BigDecimal.ZERO;
        for (Transaction t : pendingTransactions) {
            if (t.getFromAccount().equals(account) && isToday(t.getTimestamp())) {
                dailyTotal = dailyTotal.add(t.getAmount());
            }
        }
        return dailyTotal.add(amount).compareTo(new BigDecimal(DAILY_LIMIT)) <= 0;
    }
    
    private boolean checkMonthlyLimit(String account, BigDecimal amount) {
        // Bad: inefficient monthly limit check
        BigDecimal monthlyTotal = BigDecimal.ZERO;
        for (Transaction t : pendingTransactions) {
            if (t.getFromAccount().equals(account) && isThisMonth(t.getTimestamp())) {
                monthlyTotal = monthlyTotal.add(t.getAmount());
            }
        }
        return monthlyTotal.add(amount).compareTo(new BigDecimal(MONTHLY_LIMIT)) <= 0;
    }
    
    private boolean checkAccountBalance(String account, BigDecimal amount) {
        BigDecimal balance = accountBalances.get(account);
        return balance != null && balance.compareTo(amount) >= 0;
    }
    
    private boolean validateAccount(String account) {
        return accountBalances.containsKey(account);
    }
    
    private boolean checkFraudScore(String riskScore, String fraudScore) {
        // Bad: simple fraud check
        return riskScore != null && fraudScore != null;
    }
    
    private boolean checkCompliance(String complianceScore) {
        // Bad: simple compliance check
        return complianceScore != null;
    }
    
    private boolean processTransfer(String fromAccount, String toAccount, BigDecimal amount) {
        BigDecimal fromBalance = accountBalances.get(fromAccount);
        BigDecimal toBalance = accountBalances.get(toAccount);
        
        if (fromBalance.compareTo(amount) >= 0) {
            accountBalances.put(fromAccount, fromBalance.subtract(amount));
            accountBalances.put(toAccount, toBalance.add(amount));
            return true;
        }
        return false;
    }
    
    private boolean processDeposit(String account, BigDecimal amount) {
        BigDecimal balance = accountBalances.get(account);
        accountBalances.put(account, balance.add(amount));
        return true;
    }
    
    private boolean processWithdrawal(String account, BigDecimal amount) {
        BigDecimal balance = accountBalances.get(account);
        if (balance.compareTo(amount) >= 0) {
            accountBalances.put(account, balance.subtract(amount));
            return true;
        }
        return false;
    }
    
    // Bad: placeholder methods
    private boolean updateAuditTrail(String transactionId, String auditTrail) { return true; }
    private boolean sendNotification(String userId, BigDecimal amount) { return true; }
    private boolean updateLoyaltyPoints(String userId, String loyaltyPoints) { return true; }
    private boolean calculateFees(String feeCode, BigDecimal amount) { return true; }
    private boolean calculateTaxes(String taxCode, BigDecimal amount) { return true; }
    private boolean updateExchangeRate(String exchangeRate) { return true; }
    private boolean processCommission(String commission) { return true; }
    private boolean applyDiscount(String discount) { return true; }
    private boolean updateMetadata(String metadata) { return true; }
    private boolean validateSignature(String signature) { return true; }
    private boolean updateTimestamp(String timestamp) { return true; }
    private boolean isToday(String timestamp) { return true; }
    private boolean isThisMonth(String timestamp) { return true; }
    
    // Bad: getter that exposes internal state
    public static int getProcessedCount() {
        return processedCount;
    }
    
    // Bad: method that returns internal collection
    public static Map<String, BigDecimal> getAccountBalances() {
        return accountBalances;
    }
}

// Bad: inner class for transaction
class Transaction {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String timestamp;
    
    public Transaction(String fromAccount, String toAccount, BigDecimal amount, String timestamp) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = timestamp;
    }
    
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public String getTimestamp() { return timestamp; }
} 