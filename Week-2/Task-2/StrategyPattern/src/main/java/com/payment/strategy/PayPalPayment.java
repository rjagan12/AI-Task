package com.payment.strategy;

/**
 * PayPalPayment implements the PaymentStrategy interface for PayPal payments.
 * This strategy handles PayPal payment processing with email validation and fee calculation.
 */
public class PayPalPayment implements PaymentStrategy {
    
    private final String email;
    private final String password;
    
    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public boolean processPayment(int amount) {
        if (!validatePayPalCredentials()) {
            System.out.println("PayPal credentials validation failed");
            return false;
        }
        
        // Simulate PayPal payment processing
        System.out.println("Processing PayPal payment of $" + (amount / 100.0) + 
                          " using account: " + maskEmail(email));
        
        // Simulate PayPal API call delay
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // Simulate successful payment (in real implementation, this would call PayPal API)
        System.out.println("PayPal payment processed successfully");
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "PayPal Payment";
    }
    
    @Override
    public int getProcessingFee(int amount) {
        // PayPal processing fee: 2.9% + $0.30 per transaction
        return (int) Math.round(amount * 0.029) + 30;
    }
    
    private boolean validatePayPalCredentials() {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$") &&
               password != null && password.length() >= 8;
    }
    
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "****@****.com";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "****@****.com";
        }
        
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (username.length() <= 2) {
            return "****" + domain;
        }
        
        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + domain;
    }
} 