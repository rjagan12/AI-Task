package com.example.bank;

import com.example.bank.dto.TransactionRequest;
import com.example.bank.model.Account;
import com.example.bank.model.Transaction;
import com.example.bank.service.TransactionService;
import com.example.bank.security.SecurityService;
import com.example.bank.validation.TransactionValidator;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import com.example.bank.service.NotificationService;

import java.math.BigDecimal;

/**
 * Main class demonstrating the refactored banking system.
 * 
 * This class shows how the refactored codebase addresses the issues
 * found in the original messy codebase.
 * 
 * @author Refactored Banking System
 * @version 2.0
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Refactored Banking System Demo ===");
        
        try {
            // Initialize services (in a real application, this would be done by a DI container)
            AccountRepository accountRepository = new MockAccountRepository();
            TransactionRepository transactionRepository = new MockTransactionRepository();
            SecurityService securityService = new SecurityService();
            TransactionValidator transactionValidator = new MockTransactionValidator();
            NotificationService notificationService = new MockNotificationService();
            
            TransactionService transactionService = new TransactionService(
                accountRepository, transactionRepository, securityService, 
                transactionValidator, notificationService
            );
            
            // Create test accounts
            Account account1 = new Account("ACC0010001", "USER001", Account.AccountType.CHECKING, BigDecimal.valueOf(1000));
            Account account2 = new Account("ACC0020002", "USER002", Account.AccountType.SAVINGS, BigDecimal.valueOf(500));
            
            accountRepository.save(account1);
            accountRepository.save(account2);
            
            System.out.println("Created accounts:");
            System.out.println("Account 1: " + account1.getAccountNumber() + " - Balance: " + account1.getBalance());
            System.out.println("Account 2: " + account2.getAccountNumber() + " - Balance: " + account2.getBalance());
            
            // Demonstrate a transfer transaction using the Builder pattern
            TransactionRequest transferRequest = new TransactionRequest.Builder()
                .fromAccount("ACC0010001")
                .toAccount("ACC0020002")
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .description("Test transfer")
                .type(TransactionRequest.TransactionType.TRANSFER)
                .urgent(false)
                .userId("USER001")
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .sessionId("session12345678901234567890")
                .build();
            
            System.out.println("\nProcessing transfer transaction...");
            Transaction transferTransaction = transactionService.processTransaction(transferRequest);
            System.out.println("Transfer completed: " + transferTransaction);
            
            // Demonstrate a deposit transaction
            TransactionRequest depositRequest = new TransactionRequest.Builder()
                .fromAccount("ACC0010001") // Not used for deposits
                .toAccount("ACC0010001")
                .amount(BigDecimal.valueOf(500))
                .currency("USD")
                .description("Salary deposit")
                .type(TransactionRequest.TransactionType.DEPOSIT)
                .urgent(false)
                .userId("USER001")
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .sessionId("session12345678901234567890")
                .build();
            
            System.out.println("\nProcessing deposit transaction...");
            Transaction depositTransaction = transactionService.processTransaction(depositRequest);
            System.out.println("Deposit completed: " + depositTransaction);
            
            // Demonstrate a withdrawal transaction
            TransactionRequest withdrawalRequest = new TransactionRequest.Builder()
                .fromAccount("ACC0010001")
                .toAccount("ACC0010001") // Not used for withdrawals
                .amount(BigDecimal.valueOf(50))
                .currency("USD")
                .description("ATM withdrawal")
                .type(TransactionRequest.TransactionType.WITHDRAWAL)
                .urgent(false)
                .userId("USER001")
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .sessionId("session12345678901234567890")
                .build();
            
            System.out.println("\nProcessing withdrawal transaction...");
            Transaction withdrawalTransaction = transactionService.processTransaction(withdrawalRequest);
            System.out.println("Withdrawal completed: " + withdrawalTransaction);
            
            // Show final account balances
            Account updatedAccount1 = accountRepository.findByAccountNumber("ACC0010001").orElse(null);
            Account updatedAccount2 = accountRepository.findByAccountNumber("ACC0020002").orElse(null);
            
            System.out.println("\nFinal account balances:");
            System.out.println("Account 1: " + updatedAccount1.getAccountNumber() + " - Balance: " + updatedAccount1.getBalance());
            System.out.println("Account 2: " + updatedAccount2.getAccountNumber() + " - Balance: " + updatedAccount2.getBalance());
            
            System.out.println("\n=== Demo Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// Mock implementations for demonstration purposes
class MockAccountRepository implements AccountRepository {
    private java.util.Map<String, Account> accounts = new java.util.HashMap<>();
    
    @Override
    public java.util.Optional<Account> findByAccountNumber(String accountNumber) {
        return java.util.Optional.ofNullable(accounts.get(accountNumber));
    }
    
    @Override
    public Account save(Account account) {
        accounts.put(account.getAccountNumber(), account);
        return account;
    }
}

class MockTransactionRepository implements TransactionRepository {
    @Override
    public Transaction save(Transaction transaction) {
        return transaction;
    }
    
    @Override
    public BigDecimal getDailyTransactionTotal(String accountNumber, TransactionRequest.TransactionType type) {
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getMonthlyTransactionTotal(String accountNumber, TransactionRequest.TransactionType type) {
        return BigDecimal.ZERO;
    }
}

class MockTransactionValidator implements TransactionValidator {
    @Override
    public void validate(TransactionRequest request) {
        // Mock validation - always passes
    }
}

class MockNotificationService implements NotificationService {
    @Override
    public void sendTransactionConfirmation(Transaction transaction) {
        System.out.println("Notification: Transaction confirmed - " + transaction.getTransactionId());
    }
    
    @Override
    public void sendLargeTransactionAlert(Transaction transaction) {
        System.out.println("Alert: Large transaction detected - " + transaction.getTransactionId());
    }
}

// Interface definitions
interface AccountRepository {
    java.util.Optional<Account> findByAccountNumber(String accountNumber);
    Account save(Account account);
}

interface TransactionRepository {
    Transaction save(Transaction transaction);
    BigDecimal getDailyTransactionTotal(String accountNumber, TransactionRequest.TransactionType type);
    BigDecimal getMonthlyTransactionTotal(String accountNumber, TransactionRequest.TransactionType type);
}

interface TransactionValidator {
    void validate(TransactionRequest request);
}

interface NotificationService {
    void sendTransactionConfirmation(Transaction transaction);
    void sendLargeTransactionAlert(Transaction transaction);
} 