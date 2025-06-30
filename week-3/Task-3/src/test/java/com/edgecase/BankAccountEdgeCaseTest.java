package com.edgecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Comprehensive test suite for BankAccount edge cases identified through AI analysis.
 * This test suite covers 15+ edge cases to ensure robust error handling.
 */
public class BankAccountEdgeCaseTest {
    
    private BankAccount account;
    private BankAccount recipientAccount;
    
    @BeforeEach
    void setUp() {
        account = new BankAccount("ACC001", "John Doe", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        recipientAccount = new BankAccount("ACC002", "Jane Smith", BigDecimal.valueOf(500), "USD", BankAccount.AccountType.CHECKING);
    }

    // ==================== WITHDRAWAL EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 1: Withdrawal with null amount")
    void testWithdrawNullAmount() {
        TransactionResult result = account.withdraw(null, "Test withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Amount cannot be null", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 2: Withdrawal with negative amount")
    void testWithdrawNegativeAmount() {
        TransactionResult result = account.withdraw(BigDecimal.valueOf(-100), "Test withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Amount must be positive", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 3: Withdrawal with zero amount")
    void testWithdrawZeroAmount() {
        TransactionResult result = account.withdraw(BigDecimal.ZERO, "Test withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Amount must be positive", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 4: Withdrawal from inactive account")
    void testWithdrawFromInactiveAccount() {
        account.closeAccount();
        TransactionResult result = account.withdraw(BigDecimal.valueOf(100), "Test withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Account is not active", result.getMessage());
    }

    @Test
    @DisplayName("Edge Case 5: Withdrawal with insufficient funds")
    void testWithdrawInsufficientFunds() {
        TransactionResult result = account.withdraw(BigDecimal.valueOf(1500), "Test withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Insufficient funds", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 6: Withdrawal exceeding daily transaction limit")
    void testWithdrawExceedingDailyLimit() {
        // Perform 10 transactions to reach daily limit
        for (int i = 0; i < 10; i++) {
            account.withdraw(BigDecimal.valueOf(10), "Transaction " + i);
        }
        
        TransactionResult result = account.withdraw(BigDecimal.valueOf(10), "11th transaction");
        assertFalse(result.isSuccess());
        assertEquals("Daily transaction limit exceeded", result.getMessage());
    }

    @Test
    @DisplayName("Edge Case 7: Withdrawal exceeding daily amount limit")
    void testWithdrawExceedingDailyAmountLimit() {
        // Set a low daily limit
        account.setDailyTransactionLimit(BigDecimal.valueOf(100));
        
        TransactionResult result = account.withdraw(BigDecimal.valueOf(150), "Large withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Daily transaction amount limit exceeded", result.getMessage());
    }

    @Test
    @DisplayName("Edge Case 8: Withdrawal exceeding monthly transaction limit")
    void testWithdrawExceedingMonthlyLimit() {
        // Set a low monthly limit
        account.setMonthlyTransactionLimit(BigDecimal.valueOf(200));
        
        TransactionResult result = account.withdraw(BigDecimal.valueOf(250), "Large monthly withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Monthly transaction limit exceeded", result.getMessage());
    }

    @Test
    @DisplayName("Edge Case 9: Withdrawal with extremely large amount (fraud detection)")
    void testWithdrawExtremelyLargeAmount() {
        TransactionResult result = account.withdraw(BigDecimal.valueOf(150000), "Suspicious withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Transaction amount too large, requires approval", result.getMessage());
    }

    @Test
    @DisplayName("Edge Case 10: Withdrawal with maximum BigDecimal value")
    void testWithdrawMaxBigDecimalValue() {
        BigDecimal maxValue = new BigDecimal(BigInteger.valueOf(Long.MAX_VALUE));
        TransactionResult result = account.withdraw(maxValue, "Max value withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Transaction amount too large, requires approval", result.getMessage());
    }

    // ==================== DEPOSIT EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 11: Deposit with null amount")
    void testDepositNullAmount() {
        TransactionResult result = account.deposit(null, "Test deposit");
        assertFalse(result.isSuccess());
        assertEquals("Amount cannot be null", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 12: Deposit with negative amount")
    void testDepositNegativeAmount() {
        TransactionResult result = account.deposit(BigDecimal.valueOf(-100), "Test deposit");
        assertFalse(result.isSuccess());
        assertEquals("Amount must be positive", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 13: Deposit to inactive account")
    void testDepositToInactiveAccount() {
        account.closeAccount();
        TransactionResult result = account.deposit(BigDecimal.valueOf(100), "Test deposit");
        assertFalse(result.isSuccess());
        assertEquals("Account is not active", result.getMessage());
    }

    @Test
    @DisplayName("Edge Case 14: Deposit with suspiciously large amount")
    void testDepositSuspiciouslyLargeAmount() {
        TransactionResult result = account.deposit(BigDecimal.valueOf(75000), "Suspicious deposit");
        assertFalse(result.isSuccess());
        assertEquals("Large deposit requires verification", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    // ==================== TRANSFER EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 15: Transfer with null recipient")
    void testTransferNullRecipient() {
        TransactionResult result = account.transfer(null, BigDecimal.valueOf(100), "Test transfer");
        assertFalse(result.isSuccess());
        assertEquals("Recipient account cannot be null", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 16: Transfer to same account (self-transfer)")
    void testTransferToSameAccount() {
        TransactionResult result = account.transfer(account, BigDecimal.valueOf(100), "Self transfer");
        assertFalse(result.isSuccess());
        assertEquals("Cannot transfer to same account", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 17: Transfer with currency mismatch")
    void testTransferCurrencyMismatch() {
        BankAccount euroAccount = new BankAccount("ACC003", "Euro User", BigDecimal.valueOf(100), "EUR", BankAccount.AccountType.SAVINGS);
        TransactionResult result = account.transfer(euroAccount, BigDecimal.valueOf(100), "Cross-currency transfer");
        assertFalse(result.isSuccess());
        assertEquals("Currency mismatch between accounts", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 18: Transfer to inactive recipient account")
    void testTransferToInactiveRecipient() {
        recipientAccount.closeAccount();
        TransactionResult result = account.transfer(recipientAccount, BigDecimal.valueOf(100), "Transfer to inactive");
        assertFalse(result.isSuccess());
        assertEquals("Recipient account is not active", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 19: Transfer with insufficient funds")
    void testTransferInsufficientFunds() {
        TransactionResult result = account.transfer(recipientAccount, BigDecimal.valueOf(1500), "Transfer more than balance");
        assertFalse(result.isSuccess());
        assertEquals("Insufficient funds", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        assertEquals(BigDecimal.valueOf(500), recipientAccount.getBalance());
    }

    // ==================== INTEREST CALCULATION EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 20: Calculate interest with negative rate")
    void testCalculateInterestNegativeRate() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.calculateInterest(-5.0);
        });
    }

    @Test
    @DisplayName("Edge Case 21: Calculate interest with unrealistic high rate")
    void testCalculateInterestUnrealisticRate() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.calculateInterest(150.0);
        });
    }

    @Test
    @DisplayName("Edge Case 22: Calculate interest on zero balance")
    void testCalculateInterestZeroBalance() {
        BankAccount zeroBalanceAccount = new BankAccount("ACC004", "Zero User", BigDecimal.ZERO, "USD", BankAccount.AccountType.SAVINGS);
        BigDecimal interest = zeroBalanceAccount.calculateInterest(5.0);
        assertEquals(BigDecimal.ZERO, interest);
    }

    @Test
    @DisplayName("Edge Case 23: Calculate interest with zero rate")
    void testCalculateInterestZeroRate() {
        BigDecimal interest = account.calculateInterest(0.0);
        assertEquals(BigDecimal.ZERO, interest);
    }

    // ==================== ACCOUNT CLOSURE EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 24: Close already closed account")
    void testCloseAlreadyClosedAccount() {
        account.closeAccount();
        boolean result = account.closeAccount();
        assertFalse(result);
    }

    @Test
    @DisplayName("Edge Case 25: Close account with positive balance")
    void testCloseAccountWithPositiveBalance() {
        boolean result = account.closeAccount();
        assertFalse(result);
        assertTrue(account.isActive());
    }

    @Test
    @DisplayName("Edge Case 26: Close account with pending transactions")
    void testCloseAccountWithPendingTransactions() {
        account.withdraw(BigDecimal.valueOf(100), "Test withdrawal");
        boolean result = account.closeAccount();
        assertFalse(result);
        assertTrue(account.isActive());
    }

    // ==================== TRANSACTION LIMIT EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 27: Set negative daily transaction limit")
    void testSetNegativeDailyTransactionLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.setDailyTransactionLimit(BigDecimal.valueOf(-100));
        });
    }

    @Test
    @DisplayName("Edge Case 28: Set zero daily transaction limit")
    void testSetZeroDailyTransactionLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.setDailyTransactionLimit(BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Edge Case 29: Set negative monthly transaction limit")
    void testSetNegativeMonthlyTransactionLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.setMonthlyTransactionLimit(BigDecimal.valueOf(-100));
        });
    }

    // ==================== BOUNDARY VALUE EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 30: Withdrawal exactly equal to balance")
    void testWithdrawExactBalance() {
        TransactionResult result = account.withdraw(BigDecimal.valueOf(1000), "Exact balance withdrawal");
        assertTrue(result.isSuccess());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 31: Withdrawal one cent less than balance")
    void testWithdrawOneCentLessThanBalance() {
        TransactionResult result = account.withdraw(BigDecimal.valueOf(999.99), "One cent less withdrawal");
        assertTrue(result.isSuccess());
        assertEquals(BigDecimal.valueOf(0.01), account.getBalance());
    }

    @Test
    @DisplayName("Edge Case 32: Withdrawal one cent more than balance")
    void testWithdrawOneCentMoreThanBalance() {
        TransactionResult result = account.withdraw(BigDecimal.valueOf(1000.01), "One cent more withdrawal");
        assertFalse(result.isSuccess());
        assertEquals("Insufficient funds", result.getMessage());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    // ==================== CONCURRENT TRANSACTION EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 33: Multiple rapid transactions")
    void testMultipleRapidTransactions() {
        // Perform multiple transactions rapidly
        for (int i = 0; i < 5; i++) {
            TransactionResult result = account.withdraw(BigDecimal.valueOf(10), "Rapid transaction " + i);
            assertTrue(result.isSuccess());
        }
        
        assertEquals(BigDecimal.valueOf(950), account.getBalance());
        assertEquals(5, account.getDailyTransactionCount());
    }

    // ==================== EDGE CASES WITH SPECIAL CHARACTERS ====================

    @Test
    @DisplayName("Edge Case 34: Transaction description with special characters")
    void testTransactionWithSpecialCharacters() {
        String specialDescription = "Test withdrawal with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        TransactionResult result = account.withdraw(BigDecimal.valueOf(100), specialDescription);
        assertTrue(result.isSuccess());
        assertEquals(specialDescription, result.getTransaction().getDescription());
    }

    @Test
    @DisplayName("Edge Case 35: Account holder name with special characters")
    void testAccountHolderNameWithSpecialCharacters() {
        BankAccount specialAccount = new BankAccount("ACC005", "José María O'Connor-Smith", BigDecimal.valueOf(100), "USD", BankAccount.AccountType.SAVINGS);
        assertEquals("José María O'Connor-Smith", specialAccount.getAccountHolderName());
    }

    // ==================== PERFORMANCE EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 36: Large number of transactions")
    void testLargeNumberOfTransactions() {
        // This test simulates a high-volume account
        for (int i = 0; i < 10; i++) {
            account.deposit(BigDecimal.valueOf(100), "Deposit " + i);
            account.withdraw(BigDecimal.valueOf(50), "Withdrawal " + i);
        }
        
        assertEquals(BigDecimal.valueOf(1500), account.getBalance());
        assertEquals(20, account.getTransactionHistory().size());
    }

    // ==================== DATA INTEGRITY EDGE CASES ====================

    @Test
    @DisplayName("Edge Case 37: Verify transaction history immutability")
    void testTransactionHistoryImmutability() {
        account.withdraw(BigDecimal.valueOf(100), "Test withdrawal");
        
        // Try to modify the returned list
        account.getTransactionHistory().clear();
        
        // Original list should remain unchanged
        assertEquals(1, account.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Edge Case 38: Verify account equality based on account number")
    void testAccountEquality() {
        BankAccount account1 = new BankAccount("ACC001", "John Doe", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        BankAccount account2 = new BankAccount("ACC001", "Jane Smith", BigDecimal.valueOf(500), "EUR", BankAccount.AccountType.CHECKING);
        
        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    @DisplayName("Edge Case 39: Verify account inequality")
    void testAccountInequality() {
        BankAccount account1 = new BankAccount("ACC001", "John Doe", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        BankAccount account2 = new BankAccount("ACC002", "John Doe", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        
        assertNotEquals(account1, account2);
    }
} 