package com.example.bank;

import java.util.*;
import java.math.BigDecimal;

/**
 * Main class demonstrating the messy codebase issues
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Messy Codebase Demo ===");
        
        // Demonstrate BankAccount issues
        BankAccount account = new BankAccount();
        System.out.println("Account created: " + account.accountNumber);
        System.out.println("Balance: " + account.balance);
        System.out.println("Password: " + account.password); // Security issue!
        
        // Demonstrate UserManager issues
        UserManager userManager = new UserManager();
        boolean auth = userManager.authenticateUser("user1", "password123", "192.168.1.1", 
                                                   "Mozilla/5.0", "session123", false);
        System.out.println("Authentication result: " + auth);
        
        // Demonstrate TransactionProcessor issues
        TransactionProcessor processor = new TransactionProcessor();
        boolean result = processor.processTransaction(
            "TXN001", "ACC001", "ACC002", new BigDecimal("100.00"), "USD", 
            "Test transfer", "TRANSFER", false, null, "user1", "192.168.1.1", 
            "Mozilla/5.0", "session123", false, "device123", "New York", 
            "EST", "en", "WEB", "PROD001", "FEE001", "TAX001", "1.0", "0.01", 
            "0.0", "10", "LOW", "LOW", "PASS", "audit123", "meta123", 
            "sig123", "2024-01-01T10:00:00Z"
        );
        System.out.println("Transaction result: " + result);
        
        System.out.println("=== Demo Complete ===");
    }
} 