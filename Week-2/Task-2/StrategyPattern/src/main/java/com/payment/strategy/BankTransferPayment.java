package com.payment.strategy;

/**
 * BankTransferPayment implements the PaymentStrategy interface for bank transfer payments.
 * This strategy handles bank transfer processing with account validation and fee calculation.
 */
public class BankTransferPayment implements PaymentStrategy {
    
    private final String accountNumber;
    private final String routingNumber;
    private final String accountHolderName;
    
    public BankTransferPayment(String accountNumber, String routingNumber, String accountHolderName) {
        this.accountNumber = accountNumber;
        this.routingNumber = routingNumber;
        this.accountHolderName = accountHolderName;
    }
    
    @Override
    public boolean processPayment(int amount) {
        if (!validateBankDetails()) {
            System.out.println("Bank account validation failed");
            return false;
        }
        
        // Simulate bank transfer processing
        System.out.println("Processing bank transfer of $" + (amount / 100.0) + 
                          " to account ending in " + getLastFourDigits());
        
        // Simulate bank processing delay (typically longer than other methods)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // Simulate successful transfer (in real implementation, this would call bank API)
        System.out.println("Bank transfer processed successfully");
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "Bank Transfer Payment";
    }
    
    @Override
    public int getProcessingFee(int amount) {
        // Bank transfer processing fee: flat $1.50 per transaction
        return 150;
    }
    
    private boolean validateBankDetails() {
        return accountNumber != null && accountNumber.matches("\\d{8,17}") &&
               routingNumber != null && routingNumber.matches("\\d{9}") &&
               accountHolderName != null && !accountHolderName.trim().isEmpty();
    }
    
    private String getLastFourDigits() {
        if (accountNumber != null && accountNumber.length() >= 4) {
            return accountNumber.substring(accountNumber.length() - 4);
        }
        return "****";
    }
} 