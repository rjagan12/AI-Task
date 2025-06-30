package com.edgecase;

import java.math.BigDecimal;

/**
 * Demo class to showcase the BankAccount edge cases and demonstrate
 * the robust error handling implemented in the banking system.
 */
public class BankAccountDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Bank Account Edge Cases Demonstration ===\n");
        
        // Create test accounts
        BankAccount account1 = new BankAccount("ACC001", "John Doe", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        BankAccount account2 = new BankAccount("ACC002", "Jane Smith", BigDecimal.valueOf(500), "USD", BankAccount.AccountType.CHECKING);
        
        System.out.println("Initial Account States:");
        System.out.println("Account 1: " + account1);
        System.out.println("Account 2: " + account2);
        System.out.println();
        
        // Demonstrate various edge cases
        demonstrateWithdrawalEdgeCases(account1);
        demonstrateDepositEdgeCases(account1);
        demonstrateTransferEdgeCases(account1, account2);
        demonstrateInterestEdgeCases(account1);
        demonstrateAccountClosureEdgeCases(account1);
        demonstrateTransactionLimitEdgeCases(account1);
        demonstrateBoundaryValueEdgeCases(account1);
        demonstrateDataIntegrityEdgeCases(account1);
        
        System.out.println("=== Edge Case Demonstration Complete ===");
    }
    
    private static void demonstrateWithdrawalEdgeCases(BankAccount account) {
        System.out.println("--- Withdrawal Edge Cases ---");
        
        // Edge Case 1: Null amount
        System.out.println("1. Withdrawal with null amount:");
        TransactionResult result = account.withdraw(null, "Test withdrawal");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 2: Negative amount
        System.out.println("2. Withdrawal with negative amount:");
        result = account.withdraw(BigDecimal.valueOf(-100), "Test withdrawal");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 3: Zero amount
        System.out.println("3. Withdrawal with zero amount:");
        result = account.withdraw(BigDecimal.ZERO, "Test withdrawal");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 4: Insufficient funds
        System.out.println("4. Withdrawal with insufficient funds:");
        result = account.withdraw(BigDecimal.valueOf(1500), "Test withdrawal");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 5: Extremely large amount
        System.out.println("5. Withdrawal with extremely large amount:");
        result = account.withdraw(BigDecimal.valueOf(150000), "Suspicious withdrawal");
        System.out.println("   Result: " + result.getMessage());
        
        System.out.println("   Current balance: " + account.getBalance());
        System.out.println();
    }
    
    private static void demonstrateDepositEdgeCases(BankAccount account) {
        System.out.println("--- Deposit Edge Cases ---");
        
        // Edge Case 1: Null amount
        System.out.println("1. Deposit with null amount:");
        TransactionResult result = account.deposit(null, "Test deposit");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 2: Negative amount
        System.out.println("2. Deposit with negative amount:");
        result = account.deposit(BigDecimal.valueOf(-100), "Test deposit");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 3: Suspiciously large amount
        System.out.println("3. Deposit with suspiciously large amount:");
        result = account.deposit(BigDecimal.valueOf(75000), "Suspicious deposit");
        System.out.println("   Result: " + result.getMessage());
        
        // Successful deposit
        System.out.println("4. Successful deposit:");
        result = account.deposit(BigDecimal.valueOf(500), "Regular deposit");
        System.out.println("   Result: " + result.getMessage());
        System.out.println("   Current balance: " + account.getBalance());
        System.out.println();
    }
    
    private static void demonstrateTransferEdgeCases(BankAccount account1, BankAccount account2) {
        System.out.println("--- Transfer Edge Cases ---");
        
        // Edge Case 1: Null recipient
        System.out.println("1. Transfer with null recipient:");
        TransactionResult result = account1.transfer(null, BigDecimal.valueOf(100), "Test transfer");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 2: Self-transfer
        System.out.println("2. Transfer to same account:");
        result = account1.transfer(account1, BigDecimal.valueOf(100), "Self transfer");
        System.out.println("   Result: " + result.getMessage());
        
        // Edge Case 3: Currency mismatch
        BankAccount euroAccount = new BankAccount("ACC003", "Euro User", BigDecimal.valueOf(100), "EUR", BankAccount.AccountType.SAVINGS);
        System.out.println("3. Transfer with currency mismatch:");
        result = account1.transfer(euroAccount, BigDecimal.valueOf(100), "Cross-currency transfer");
        System.out.println("   Result: " + result.getMessage());
        
        // Successful transfer
        System.out.println("4. Successful transfer:");
        result = account1.transfer(account2, BigDecimal.valueOf(100), "Regular transfer");
        System.out.println("   Result: " + result.getMessage());
        System.out.println("   Account 1 balance: " + account1.getBalance());
        System.out.println("   Account 2 balance: " + account2.getBalance());
        System.out.println();
    }
    
    private static void demonstrateInterestEdgeCases(BankAccount account) {
        System.out.println("--- Interest Calculation Edge Cases ---");
        
        // Edge Case 1: Negative interest rate
        System.out.println("1. Calculate interest with negative rate:");
        try {
            account.calculateInterest(-5.0);
        } catch (IllegalArgumentException e) {
            System.out.println("   Exception: " + e.getMessage());
        }
        
        // Edge Case 2: Unrealistic high rate
        System.out.println("2. Calculate interest with unrealistic rate:");
        try {
            account.calculateInterest(150.0);
        } catch (IllegalArgumentException e) {
            System.out.println("   Exception: " + e.getMessage());
        }
        
        // Edge Case 3: Zero rate
        System.out.println("3. Calculate interest with zero rate:");
        BigDecimal interest = account.calculateInterest(0.0);
        System.out.println("   Interest: " + interest);
        
        // Normal interest calculation
        System.out.println("4. Normal interest calculation:");
        interest = account.calculateInterest(5.0);
        System.out.println("   Interest at 5%: " + interest);
        System.out.println();
    }
    
    private static void demonstrateAccountClosureEdgeCases(BankAccount account) {
        System.out.println("--- Account Closure Edge Cases ---");
        
        // Edge Case 1: Close account with positive balance
        System.out.println("1. Close account with positive balance:");
        boolean result = account.closeAccount();
        System.out.println("   Result: " + result);
        System.out.println("   Account active: " + account.isActive());
        
        // Edge Case 2: Close already closed account
        System.out.println("2. Close already closed account:");
        result = account.closeAccount();
        System.out.println("   Result: " + result);
        System.out.println();
    }
    
    private static void demonstrateTransactionLimitEdgeCases(BankAccount account) {
        System.out.println("--- Transaction Limit Edge Cases ---");
        
        // Edge Case 1: Set negative daily limit
        System.out.println("1. Set negative daily transaction limit:");
        try {
            account.setDailyTransactionLimit(BigDecimal.valueOf(-100));
        } catch (IllegalArgumentException e) {
            System.out.println("   Exception: " + e.getMessage());
        }
        
        // Edge Case 2: Set zero daily limit
        System.out.println("2. Set zero daily transaction limit:");
        try {
            account.setDailyTransactionLimit(BigDecimal.ZERO);
        } catch (IllegalArgumentException e) {
            System.out.println("   Exception: " + e.getMessage());
        }
        
        // Set valid limit
        System.out.println("3. Set valid daily transaction limit:");
        account.setDailyTransactionLimit(BigDecimal.valueOf(500));
        System.out.println("   New daily limit: " + account.getDailyTransactionLimit());
        System.out.println();
    }
    
    private static void demonstrateBoundaryValueEdgeCases(BankAccount account) {
        System.out.println("--- Boundary Value Edge Cases ---");
        
        // Reset account for testing
        BankAccount testAccount = new BankAccount("ACC004", "Test User", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        
        // Edge Case 1: Withdrawal exactly equal to balance
        System.out.println("1. Withdrawal exactly equal to balance:");
        TransactionResult result = testAccount.withdraw(BigDecimal.valueOf(1000), "Exact balance withdrawal");
        System.out.println("   Result: " + result.getMessage());
        System.out.println("   Remaining balance: " + testAccount.getBalance());
        
        // Reset for next test
        testAccount = new BankAccount("ACC005", "Test User", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        
        // Edge Case 2: Withdrawal one cent less than balance
        System.out.println("2. Withdrawal one cent less than balance:");
        result = testAccount.withdraw(BigDecimal.valueOf(999.99), "One cent less withdrawal");
        System.out.println("   Result: " + result.getMessage());
        System.out.println("   Remaining balance: " + testAccount.getBalance());
        
        // Edge Case 3: Withdrawal one cent more than balance
        System.out.println("3. Withdrawal one cent more than balance:");
        result = testAccount.withdraw(BigDecimal.valueOf(1000.01), "One cent more withdrawal");
        System.out.println("   Result: " + result.getMessage());
        System.out.println("   Balance unchanged: " + testAccount.getBalance());
        System.out.println();
    }
    
    private static void demonstrateDataIntegrityEdgeCases(BankAccount account) {
        System.out.println("--- Data Integrity Edge Cases ---");
        
        // Edge Case 1: Transaction history immutability
        System.out.println("1. Transaction history immutability:");
        account.withdraw(BigDecimal.valueOf(50), "Test withdrawal");
        int originalSize = account.getTransactionHistory().size();
        account.getTransactionHistory().clear(); // This should not affect the original list
        int afterClearSize = account.getTransactionHistory().size();
        System.out.println("   Original size: " + originalSize);
        System.out.println("   After clear attempt: " + afterClearSize);
        System.out.println("   Immutability maintained: " + (originalSize == afterClearSize));
        
        // Edge Case 2: Account equality
        System.out.println("2. Account equality based on account number:");
        BankAccount account1 = new BankAccount("ACC001", "John Doe", BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
        BankAccount account2 = new BankAccount("ACC001", "Jane Smith", BigDecimal.valueOf(500), "EUR", BankAccount.AccountType.CHECKING);
        System.out.println("   Accounts equal: " + account1.equals(account2));
        System.out.println("   Hash codes equal: " + (account1.hashCode() == account2.hashCode()));
        System.out.println();
    }
} 