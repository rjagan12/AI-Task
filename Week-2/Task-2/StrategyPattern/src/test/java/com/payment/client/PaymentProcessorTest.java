package com.payment.client;

import com.payment.strategy.BankTransferPayment;
import com.payment.strategy.CreditCardPayment;
import com.payment.strategy.PayPalPayment;
import com.payment.strategy.PaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaymentProcessor client.
 */
public class PaymentProcessorTest {
    
    private PaymentProcessor processor;
    private PaymentStrategy creditCardStrategy;
    private PaymentStrategy paypalStrategy;
    private PaymentStrategy bankTransferStrategy;
    
    @BeforeEach
    void setUp() {
        processor = new PaymentProcessor();
        creditCardStrategy = new CreditCardPayment("4111111111111111", "John Doe", "12/25", "123");
        paypalStrategy = new PayPalPayment("john.doe@example.com", "securepassword123");
        bankTransferStrategy = new BankTransferPayment("1234567890", "021000021", "John Doe");
    }
    
    @Test
    void testConstructorWithStrategy() {
        PaymentProcessor processorWithStrategy = new PaymentProcessor(creditCardStrategy);
        assertEquals(creditCardStrategy, processorWithStrategy.getPaymentStrategy());
    }
    
    @Test
    void testDefaultConstructor() {
        PaymentProcessor defaultProcessor = new PaymentProcessor();
        assertNull(defaultProcessor.getPaymentStrategy());
    }
    
    @Test
    void testSetPaymentStrategy() {
        processor.setPaymentStrategy(creditCardStrategy);
        assertEquals(creditCardStrategy, processor.getPaymentStrategy());
    }
    
    @Test
    void testGetPaymentStrategy() {
        processor.setPaymentStrategy(paypalStrategy);
        assertEquals(paypalStrategy, processor.getPaymentStrategy());
    }
    
    @Test
    void testProcessPaymentWithCreditCard() {
        processor.setPaymentStrategy(creditCardStrategy);
        boolean result = processor.processPayment(5000);
        assertTrue(result, "Credit card payment should succeed");
    }
    
    @Test
    void testProcessPaymentWithPayPal() {
        processor.setPaymentStrategy(paypalStrategy);
        boolean result = processor.processPayment(5000);
        assertTrue(result, "PayPal payment should succeed");
    }
    
    @Test
    void testProcessPaymentWithBankTransfer() {
        processor.setPaymentStrategy(bankTransferStrategy);
        boolean result = processor.processPayment(5000);
        assertTrue(result, "Bank transfer should succeed");
    }
    
    @Test
    void testProcessPaymentWithoutStrategy() {
        assertThrows(IllegalStateException.class, () -> {
            processor.processPayment(5000);
        }, "Should throw IllegalStateException when no strategy is set");
    }
    
    @Test
    void testProcessPaymentWithZeroAmount() {
        processor.setPaymentStrategy(creditCardStrategy);
        boolean result = processor.processPayment(0);
        assertTrue(result, "Payment should succeed with zero amount");
    }
    
    @Test
    void testProcessPaymentWithNegativeAmount() {
        processor.setPaymentStrategy(creditCardStrategy);
        boolean result = processor.processPayment(-1000);
        assertTrue(result, "Payment should succeed with negative amount");
    }
    
    @Test
    void testGetStrategyInfoWithStrategy() {
        processor.setPaymentStrategy(creditCardStrategy);
        String info = processor.getStrategyInfo();
        assertEquals("Current strategy: Credit Card Payment", info);
    }
    
    @Test
    void testGetStrategyInfoWithoutStrategy() {
        String info = processor.getStrategyInfo();
        assertEquals("No payment strategy set", info);
    }
    
    @Test
    void testStrategySwitching() {
        // Start with credit card
        processor.setPaymentStrategy(creditCardStrategy);
        assertEquals(creditCardStrategy, processor.getPaymentStrategy());
        
        // Switch to PayPal
        processor.setPaymentStrategy(paypalStrategy);
        assertEquals(paypalStrategy, processor.getPaymentStrategy());
        
        // Switch to bank transfer
        processor.setPaymentStrategy(bankTransferStrategy);
        assertEquals(bankTransferStrategy, processor.getPaymentStrategy());
    }
    
    @Test
    void testProcessPaymentWithDifferentAmounts() {
        processor.setPaymentStrategy(creditCardStrategy);
        
        // Test different amounts
        assertTrue(processor.processPayment(100), "Should handle small amounts");
        assertTrue(processor.processPayment(10000), "Should handle medium amounts");
        assertTrue(processor.processPayment(100000), "Should handle large amounts");
    }
} 