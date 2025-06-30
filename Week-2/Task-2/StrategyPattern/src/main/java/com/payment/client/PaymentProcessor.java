package com.payment.client;

import com.payment.strategy.PaymentStrategy;

/**
 * PaymentProcessor is the client class that uses the Strategy Pattern.
 * It can switch between different payment strategies at runtime without
 * changing the client code.
 */
public class PaymentProcessor {
    
    private PaymentStrategy paymentStrategy;
    
    /**
     * Constructor with initial payment strategy.
     * 
     * @param paymentStrategy the initial payment strategy to use
     */
    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    /**
     * Default constructor.
     */
    public PaymentProcessor() {
        // No default strategy - must be set before processing
    }
    
    /**
     * Sets the payment strategy to use for processing payments.
     * 
     * @param paymentStrategy the payment strategy to set
     */
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    /**
     * Gets the current payment strategy.
     * 
     * @return the current payment strategy
     */
    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }
    
    /**
     * Processes a payment using the current payment strategy.
     * 
     * @param amount the payment amount in cents
     * @return true if payment was successful, false otherwise
     * @throws IllegalStateException if no payment strategy is set
     */
    public boolean processPayment(int amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("No payment strategy has been set");
        }
        
        System.out.println("Using payment strategy: " + paymentStrategy.getStrategyName());
        
        int processingFee = paymentStrategy.getProcessingFee(amount);
        int totalAmount = amount + processingFee;
        
        System.out.println("Payment amount: $" + (amount / 100.0));
        System.out.println("Processing fee: $" + (processingFee / 100.0));
        System.out.println("Total amount: $" + (totalAmount / 100.0));
        
        return paymentStrategy.processPayment(amount);
    }
    
    /**
     * Gets information about the current payment strategy.
     * 
     * @return information about the current strategy
     */
    public String getStrategyInfo() {
        if (paymentStrategy == null) {
            return "No payment strategy set";
        }
        return "Current strategy: " + paymentStrategy.getStrategyName();
    }
} 