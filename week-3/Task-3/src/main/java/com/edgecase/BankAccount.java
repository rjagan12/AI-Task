package com.edgecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * BankAccount class with critical business logic for financial operations.
 * This class will be used to identify and test various edge cases.
 */
public class BankAccount {
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
    private String currency;
    private AccountType accountType;
    private boolean isActive;
    private List<Transaction> transactionHistory;
    private LocalDateTime lastTransactionTime;
    private int dailyTransactionCount;
    private BigDecimal dailyTransactionLimit;
    private BigDecimal monthlyTransactionLimit;
    private BigDecimal currentMonthTotal;

    public enum AccountType {
        SAVINGS, CHECKING, BUSINESS, PREMIUM
    }

    public BankAccount(String accountNumber, String accountHolderName, BigDecimal initialBalance, 
                      String currency, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.currency = currency;
        this.accountType = accountType;
        this.isActive = true;
        this.transactionHistory = new ArrayList<>();
        this.lastTransactionTime = LocalDateTime.now();
        this.dailyTransactionCount = 0;
        this.dailyTransactionLimit = BigDecimal.valueOf(10000);
        this.monthlyTransactionLimit = BigDecimal.valueOf(50000);
        this.currentMonthTotal = BigDecimal.ZERO;
    }

    /**
     * Critical business logic: Process a withdrawal transaction
     */
    public TransactionResult withdraw(BigDecimal amount, String description) {
        // Edge case 1: Null amount
        if (amount == null) {
            return new TransactionResult(false, "Amount cannot be null", null);
        }

        // Edge case 2: Negative or zero amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TransactionResult(false, "Amount must be positive", null);
        }

        // Edge case 3: Account not active
        if (!isActive) {
            return new TransactionResult(false, "Account is not active", null);
        }

        // Edge case 4: Insufficient funds
        if (balance.compareTo(amount) < 0) {
            return new TransactionResult(false, "Insufficient funds", null);
        }

        // Edge case 5: Daily transaction limit exceeded
        if (dailyTransactionCount >= 10) {
            return new TransactionResult(false, "Daily transaction limit exceeded", null);
        }

        // Edge case 6: Daily amount limit exceeded
        if (currentMonthTotal.add(amount).compareTo(dailyTransactionLimit) > 0) {
            return new TransactionResult(false, "Daily transaction amount limit exceeded", null);
        }

        // Edge case 7: Monthly transaction limit exceeded
        if (currentMonthTotal.add(amount).compareTo(monthlyTransactionLimit) > 0) {
            return new TransactionResult(false, "Monthly transaction limit exceeded", null);
        }

        // Edge case 8: Very large amount (potential fraud)
        if (amount.compareTo(BigDecimal.valueOf(100000)) > 0) {
            return new TransactionResult(false, "Transaction amount too large, requires approval", null);
        }

        // Process the withdrawal
        balance = balance.subtract(amount);
        currentMonthTotal = currentMonthTotal.add(amount);
        dailyTransactionCount++;
        
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, amount, description, LocalDateTime.now());
        transactionHistory.add(transaction);
        lastTransactionTime = LocalDateTime.now();

        return new TransactionResult(true, "Withdrawal successful", transaction);
    }

    /**
     * Critical business logic: Process a deposit transaction
     */
    public TransactionResult deposit(BigDecimal amount, String description) {
        // Edge case 1: Null amount
        if (amount == null) {
            return new TransactionResult(false, "Amount cannot be null", null);
        }

        // Edge case 2: Negative or zero amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TransactionResult(false, "Amount must be positive", null);
        }

        // Edge case 3: Account not active
        if (!isActive) {
            return new TransactionResult(false, "Account is not active", null);
        }

        // Edge case 4: Very large deposit (potential money laundering)
        if (amount.compareTo(BigDecimal.valueOf(50000)) > 0) {
            return new TransactionResult(false, "Large deposit requires verification", null);
        }

        // Process the deposit
        balance = balance.add(amount);
        
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, description, LocalDateTime.now());
        transactionHistory.add(transaction);
        lastTransactionTime = LocalDateTime.now();

        return new TransactionResult(true, "Deposit successful", transaction);
    }

    /**
     * Critical business logic: Transfer money to another account
     */
    public TransactionResult transfer(BankAccount recipient, BigDecimal amount, String description) {
        // Edge case 1: Null recipient
        if (recipient == null) {
            return new TransactionResult(false, "Recipient account cannot be null", null);
        }

        // Edge case 2: Self-transfer
        if (this.equals(recipient)) {
            return new TransactionResult(false, "Cannot transfer to same account", null);
        }

        // Edge case 3: Different currencies
        if (!this.currency.equals(recipient.getCurrency())) {
            return new TransactionResult(false, "Currency mismatch between accounts", null);
        }

        // Edge case 4: Recipient account not active
        if (!recipient.isActive()) {
            return new TransactionResult(false, "Recipient account is not active", null);
        }

        // Process withdrawal from this account
        TransactionResult withdrawalResult = withdraw(amount, "Transfer to " + recipient.getAccountNumber() + ": " + description);
        if (!withdrawalResult.isSuccess()) {
            return withdrawalResult;
        }

        // Process deposit to recipient account
        TransactionResult depositResult = recipient.deposit(amount, "Transfer from " + this.accountNumber + ": " + description);
        if (!depositResult.isSuccess()) {
            // Rollback the withdrawal
            balance = balance.add(amount);
            currentMonthTotal = currentMonthTotal.subtract(amount);
            dailyTransactionCount--;
            transactionHistory.remove(transactionHistory.size() - 1);
            return new TransactionResult(false, "Transfer failed: " + depositResult.getMessage(), null);
        }

        return new TransactionResult(true, "Transfer successful", withdrawalResult.getTransaction());
    }

    /**
     * Critical business logic: Calculate interest
     */
    public BigDecimal calculateInterest(double rate) {
        // Edge case 1: Negative interest rate
        if (rate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }

        // Edge case 2: Very high interest rate
        if (rate > 100) {
            throw new IllegalArgumentException("Interest rate seems unrealistic");
        }

        // Edge case 3: Zero balance
        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return balance.multiply(BigDecimal.valueOf(rate / 100));
    }

    /**
     * Critical business logic: Close account
     */
    public boolean closeAccount() {
        // Edge case 1: Account already closed
        if (!isActive) {
            return false;
        }

        // Edge case 2: Account has pending transactions
        if (dailyTransactionCount > 0) {
            return false;
        }

        // Edge case 3: Account has positive balance
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            return false;
        }

        isActive = false;
        return true;
    }

    // Getters and setters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public AccountType getAccountType() { return accountType; }
    public boolean isActive() { return isActive; }
    public List<Transaction> getTransactionHistory() { return new ArrayList<>(transactionHistory); }
    public LocalDateTime getLastTransactionTime() { return lastTransactionTime; }
    public int getDailyTransactionCount() { return dailyTransactionCount; }
    public BigDecimal getDailyTransactionLimit() { return dailyTransactionLimit; }
    public BigDecimal getMonthlyTransactionLimit() { return monthlyTransactionLimit; }
    public BigDecimal getCurrentMonthTotal() { return currentMonthTotal; }

    public void setDailyTransactionLimit(BigDecimal limit) {
        if (limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction limit must be positive");
        }
        this.dailyTransactionLimit = limit;
    }

    public void setMonthlyTransactionLimit(BigDecimal limit) {
        if (limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction limit must be positive");
        }
        this.monthlyTransactionLimit = limit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BankAccount that = (BankAccount) obj;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", accountType=" + accountType +
                ", isActive=" + isActive +
                '}';
    }
} 