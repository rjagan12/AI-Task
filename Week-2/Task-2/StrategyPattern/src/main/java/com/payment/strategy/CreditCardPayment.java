package com.payment.strategy;

/**
 * CreditCardPayment implements the PaymentStrategy interface for credit card payments.
 * This strategy handles credit card payment processing with validation and fee calculation.
 */
public class CreditCardPayment implements PaymentStrategy {
    
    private final String cardNumber;
    private final String cardHolderName;
    private final String expiryDate;
    private final String cvv;
    
    public CreditCardPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }
    
    @Override
    public boolean processPayment(int amount) {
        if (!validateCardDetails()) {
            System.out.println("Credit card validation failed");
            return false;
        }
        
        // Simulate credit card payment processing
        System.out.println("Processing credit card payment of $" + (amount / 100.0) + 
                          " using card ending in " + getLastFourDigits());
        
        // Simulate network delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // Simulate successful payment (in real implementation, this would call payment gateway)
        System.out.println("Credit card payment processed successfully");
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "Credit Card Payment";
    }
    
    @Override
    public int getProcessingFee(int amount) {
        // Credit card processing fee: 2.5% of transaction amount
        return (int) Math.round(amount * 0.025);
    }
    
    private boolean validateCardDetails() {
        return cardNumber != null && cardNumber.length() >= 13 && cardNumber.length() <= 19 &&
               cardHolderName != null && !cardHolderName.trim().isEmpty() &&
               expiryDate != null && expiryDate.matches("\\d{2}/\\d{2}") &&
               cvv != null && cvv.matches("\\d{3,4}");
    }
    
    private String getLastFourDigits() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return "****";
    }
} 