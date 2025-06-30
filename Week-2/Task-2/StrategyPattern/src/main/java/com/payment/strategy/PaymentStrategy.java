package com.payment.strategy;

/**
 * PaymentStrategy interface defines the contract for all payment strategies.
 * This follows the Strategy Pattern by providing a common interface for
 * different payment algorithms.
 */
public interface PaymentStrategy {
    
    /**
     * Processes a payment with the given amount.
     * 
     * @param amount the payment amount in cents
     * @return true if payment was successful, false otherwise
     */
    boolean processPayment(int amount);
    
    /**
     * Gets the name of the payment strategy.
     * 
     * @return the strategy name
     */
    String getStrategyName();
    
    /**
     * Gets the processing fee for this payment method.
     * 
     * @param amount the payment amount in cents
     * @return the processing fee in cents
     */
    int getProcessingFee(int amount);
} 