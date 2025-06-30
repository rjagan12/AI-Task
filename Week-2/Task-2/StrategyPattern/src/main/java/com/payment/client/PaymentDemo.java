package com.payment.client;

import com.payment.strategy.BankTransferPayment;
import com.payment.strategy.CreditCardPayment;
import com.payment.strategy.PayPalPayment;
import com.payment.strategy.PaymentStrategy;

/**
 * PaymentDemo demonstrates the Strategy Pattern in action.
 * It shows how different payment strategies can be used interchangeably
 * without changing the client code.
 */
public class PaymentDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Strategy Pattern Payment Processing Demo ===\n");
        
        // Create payment processor
        PaymentProcessor processor = new PaymentProcessor();
        
        // Test different payment strategies
        testCreditCardPayment(processor);
        testPayPalPayment(processor);
        testBankTransferPayment(processor);
        
        // Demonstrate runtime strategy switching
        demonstrateRuntimeStrategySwitching(processor);
    }
    
    private static void testCreditCardPayment(PaymentProcessor processor) {
        System.out.println("--- Testing Credit Card Payment ---");
        
        PaymentStrategy creditCardStrategy = new CreditCardPayment(
            "4111111111111111", "John Doe", "12/25", "123"
        );
        
        processor.setPaymentStrategy(creditCardStrategy);
        boolean result = processor.processPayment(5000); // $50.00
        
        System.out.println("Credit card payment result: " + (result ? "SUCCESS" : "FAILED"));
        System.out.println();
    }
    
    private static void testPayPalPayment(PaymentProcessor processor) {
        System.out.println("--- Testing PayPal Payment ---");
        
        PaymentStrategy paypalStrategy = new PayPalPayment(
            "john.doe@example.com", "securepassword123"
        );
        
        processor.setPaymentStrategy(paypalStrategy);
        boolean result = processor.processPayment(7500); // $75.00
        
        System.out.println("PayPal payment result: " + (result ? "SUCCESS" : "FAILED"));
        System.out.println();
    }
    
    private static void testBankTransferPayment(PaymentProcessor processor) {
        System.out.println("--- Testing Bank Transfer Payment ---");
        
        PaymentStrategy bankTransferStrategy = new BankTransferPayment(
            "1234567890", "021000021", "John Doe"
        );
        
        processor.setPaymentStrategy(bankTransferStrategy);
        boolean result = processor.processPayment(10000); // $100.00
        
        System.out.println("Bank transfer result: " + (result ? "SUCCESS" : "FAILED"));
        System.out.println();
    }
    
    private static void demonstrateRuntimeStrategySwitching(PaymentProcessor processor) {
        System.out.println("--- Demonstrating Runtime Strategy Switching ---");
        
        int amount = 2500; // $25.00
        
        // Switch between strategies for the same amount
        PaymentStrategy[] strategies = {
            new CreditCardPayment("4111111111111111", "John Doe", "12/25", "123"),
            new PayPalPayment("john.doe@example.com", "securepassword123"),
            new BankTransferPayment("1234567890", "021000021", "John Doe")
        };
        
        for (PaymentStrategy strategy : strategies) {
            processor.setPaymentStrategy(strategy);
            System.out.println("Processing $" + (amount / 100.0) + " with " + strategy.getStrategyName());
            processor.processPayment(amount);
            System.out.println();
        }
    }
} 