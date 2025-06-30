package com.payment.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PayPalPayment strategy.
 */
public class PayPalPaymentTest {
    
    private PayPalPayment validPayPal;
    private PayPalPayment invalidPayPal;
    
    @BeforeEach
    void setUp() {
        validPayPal = new PayPalPayment(
            "john.doe@example.com", "securepassword123"
        );
        
        invalidPayPal = new PayPalPayment(
            "invalid-email", "short"
        );
    }
    
    @Test
    void testGetStrategyName() {
        assertEquals("PayPal Payment", validPayPal.getStrategyName());
    }
    
    @Test
    void testProcessPaymentWithValidCredentials() {
        boolean result = validPayPal.processPayment(5000);
        assertTrue(result, "Payment should succeed with valid PayPal credentials");
    }
    
    @Test
    void testProcessPaymentWithInvalidCredentials() {
        boolean result = invalidPayPal.processPayment(5000);
        assertFalse(result, "Payment should fail with invalid PayPal credentials");
    }
    
    @Test
    void testProcessPaymentWithZeroAmount() {
        boolean result = validPayPal.processPayment(0);
        assertTrue(result, "Payment should succeed with zero amount");
    }
    
    @Test
    void testProcessPaymentWithNegativeAmount() {
        boolean result = validPayPal.processPayment(-1000);
        assertTrue(result, "Payment should succeed even with negative amount");
    }
    
    @Test
    void testGetProcessingFee() {
        int amount = 10000; // $100.00
        int expectedFee = (int) Math.round(amount * 0.029) + 30; // 2.9% + $0.30
        
        int actualFee = validPayPal.getProcessingFee(amount);
        assertEquals(expectedFee, actualFee, "Processing fee should be 2.9% + $0.30");
    }
    
    @Test
    void testGetProcessingFeeWithZeroAmount() {
        int fee = validPayPal.getProcessingFee(0);
        assertEquals(30, fee, "Processing fee should be $0.30 for zero amount");
    }
    
    @Test
    void testGetProcessingFeeWithSmallAmount() {
        int amount = 100; // $1.00
        int fee = validPayPal.getProcessingFee(amount);
        assertEquals(33, fee, "Processing fee should be 2.9% + $0.30 for small amounts");
    }
    
    @Test
    void testConstructorWithNullValues() {
        PayPalPayment nullPayPal = new PayPalPayment(null, null);
        boolean result = nullPayPal.processPayment(1000);
        assertFalse(result, "Payment should fail with null credentials");
    }
    
    @Test
    void testConstructorWithEmptyValues() {
        PayPalPayment emptyPayPal = new PayPalPayment("", "");
        boolean result = emptyPayPal.processPayment(1000);
        assertFalse(result, "Payment should fail with empty credentials");
    }
    
    @Test
    void testConstructorWithInvalidEmailFormat() {
        PayPalPayment invalidEmailPayPal = new PayPalPayment("not-an-email", "password123");
        boolean result = invalidEmailPayPal.processPayment(1000);
        assertFalse(result, "Payment should fail with invalid email format");
    }
    
    @Test
    void testConstructorWithShortPassword() {
        PayPalPayment shortPasswordPayPal = new PayPalPayment("test@example.com", "123");
        boolean result = shortPasswordPayPal.processPayment(1000);
        assertFalse(result, "Payment should fail with short password");
    }
} 