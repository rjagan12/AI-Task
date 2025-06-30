package com.payment.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BankTransferPayment strategy.
 */
public class BankTransferPaymentTest {
    
    private BankTransferPayment validBankTransfer;
    private BankTransferPayment invalidBankTransfer;
    
    @BeforeEach
    void setUp() {
        validBankTransfer = new BankTransferPayment(
            "1234567890", "021000021", "John Doe"
        );
        
        invalidBankTransfer = new BankTransferPayment(
            "123", "123", "John Doe"
        );
    }
    
    @Test
    void testGetStrategyName() {
        assertEquals("Bank Transfer Payment", validBankTransfer.getStrategyName());
    }
    
    @Test
    void testProcessPaymentWithValidBankDetails() {
        boolean result = validBankTransfer.processPayment(5000);
        assertTrue(result, "Payment should succeed with valid bank details");
    }
    
    @Test
    void testProcessPaymentWithInvalidBankDetails() {
        boolean result = invalidBankTransfer.processPayment(5000);
        assertFalse(result, "Payment should fail with invalid bank details");
    }
    
    @Test
    void testProcessPaymentWithZeroAmount() {
        boolean result = validBankTransfer.processPayment(0);
        assertTrue(result, "Payment should succeed with zero amount");
    }
    
    @Test
    void testProcessPaymentWithNegativeAmount() {
        boolean result = validBankTransfer.processPayment(-1000);
        assertTrue(result, "Payment should succeed even with negative amount");
    }
    
    @Test
    void testGetProcessingFee() {
        int amount = 10000; // $100.00
        int expectedFee = 150; // Flat $1.50
        
        int actualFee = validBankTransfer.getProcessingFee(amount);
        assertEquals(expectedFee, actualFee, "Processing fee should be flat $1.50");
    }
    
    @Test
    void testGetProcessingFeeWithZeroAmount() {
        int fee = validBankTransfer.getProcessingFee(0);
        assertEquals(150, fee, "Processing fee should be $1.50 even for zero amount");
    }
    
    @Test
    void testGetProcessingFeeWithLargeAmount() {
        int amount = 1000000; // $10,000.00
        int fee = validBankTransfer.getProcessingFee(amount);
        assertEquals(150, fee, "Processing fee should remain $1.50 for large amounts");
    }
    
    @Test
    void testConstructorWithNullValues() {
        BankTransferPayment nullBankTransfer = new BankTransferPayment(null, null, null);
        boolean result = nullBankTransfer.processPayment(1000);
        assertFalse(result, "Payment should fail with null bank details");
    }
    
    @Test
    void testConstructorWithEmptyValues() {
        BankTransferPayment emptyBankTransfer = new BankTransferPayment("", "", "");
        boolean result = emptyBankTransfer.processPayment(1000);
        assertFalse(result, "Payment should fail with empty bank details");
    }
    
    @Test
    void testConstructorWithInvalidAccountNumber() {
        BankTransferPayment invalidAccount = new BankTransferPayment("123", "021000021", "John Doe");
        boolean result = invalidAccount.processPayment(1000);
        assertFalse(result, "Payment should fail with invalid account number");
    }
    
    @Test
    void testConstructorWithInvalidRoutingNumber() {
        BankTransferPayment invalidRouting = new BankTransferPayment("1234567890", "123", "John Doe");
        boolean result = invalidRouting.processPayment(1000);
        assertFalse(result, "Payment should fail with invalid routing number");
    }
    
    @Test
    void testConstructorWithEmptyAccountHolderName() {
        BankTransferPayment emptyName = new BankTransferPayment("1234567890", "021000021", "");
        boolean result = emptyName.processPayment(1000);
        assertFalse(result, "Payment should fail with empty account holder name");
    }
} 