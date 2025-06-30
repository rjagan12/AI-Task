package com.payment.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreditCardPayment strategy.
 */
public class CreditCardPaymentTest {
    
    private CreditCardPayment validCreditCard;
    private CreditCardPayment invalidCreditCard;
    
    @BeforeEach
    void setUp() {
        validCreditCard = new CreditCardPayment(
            "4111111111111111", "John Doe", "12/25", "123"
        );
        
        invalidCreditCard = new CreditCardPayment(
            "123", "John Doe", "12/25", "123"
        );
    }
    
    @Test
    void testGetStrategyName() {
        assertEquals("Credit Card Payment", validCreditCard.getStrategyName());
    }
    
    @Test
    void testProcessPaymentWithValidCard() {
        boolean result = validCreditCard.processPayment(5000);
        assertTrue(result, "Payment should succeed with valid card details");
    }
    
    @Test
    void testProcessPaymentWithInvalidCard() {
        boolean result = invalidCreditCard.processPayment(5000);
        assertFalse(result, "Payment should fail with invalid card details");
    }
    
    @Test
    void testProcessPaymentWithZeroAmount() {
        boolean result = validCreditCard.processPayment(0);
        assertTrue(result, "Payment should succeed with zero amount");
    }
    
    @Test
    void testProcessPaymentWithNegativeAmount() {
        boolean result = validCreditCard.processPayment(-1000);
        assertTrue(result, "Payment should succeed even with negative amount (validation handled by payment gateway)");
    }
    
    @Test
    void testGetProcessingFee() {
        int amount = 10000; // $100.00
        int expectedFee = (int) Math.round(amount * 0.025); // 2.5%
        
        int actualFee = validCreditCard.getProcessingFee(amount);
        assertEquals(expectedFee, actualFee, "Processing fee should be 2.5% of transaction amount");
    }
    
    @Test
    void testGetProcessingFeeWithZeroAmount() {
        int fee = validCreditCard.getProcessingFee(0);
        assertEquals(0, fee, "Processing fee should be 0 for zero amount");
    }
    
    @Test
    void testGetProcessingFeeWithSmallAmount() {
        int amount = 100; // $1.00
        int fee = validCreditCard.getProcessingFee(amount);
        assertEquals(3, fee, "Processing fee should be rounded up for small amounts");
    }
    
    @Test
    void testConstructorWithNullValues() {
        CreditCardPayment nullCard = new CreditCardPayment(null, null, null, null);
        boolean result = nullCard.processPayment(1000);
        assertFalse(result, "Payment should fail with null card details");
    }
    
    @Test
    void testConstructorWithEmptyValues() {
        CreditCardPayment emptyCard = new CreditCardPayment("", "", "", "");
        boolean result = emptyCard.processPayment(1000);
        assertFalse(result, "Payment should fail with empty card details");
    }
} 