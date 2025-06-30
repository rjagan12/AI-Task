package com.example.bank.service;

import com.example.bank.dto.TransactionRequest;
import com.example.bank.model.Account;
import com.example.bank.model.Transaction;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import com.example.bank.security.SecurityService;
import com.example.bank.validation.TransactionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service class responsible for processing financial transactions.
 * 
 * This class follows the Single Responsibility Principle by focusing solely
 * on transaction processing logic. It delegates validation, security, and
 * data persistence to specialized services.
 * 
 * @author Refactored Banking System
 * @version 2.0
 */
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityService securityService;
    private final TransactionValidator transactionValidator;
    private final NotificationService notificationService;
    
    /**
     * Creates a new TransactionService with the required dependencies.
     * 
     * @param accountRepository repository for account operations
     * @param transactionRepository repository for transaction operations
     * @param securityService service for security validations
     * @param transactionValidator validator for transaction requests
     * @param notificationService service for sending notifications
     */
    public TransactionService(AccountRepository accountRepository,
                            TransactionRepository transactionRepository,
                            SecurityService securityService,
                            TransactionValidator transactionValidator,
                            NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.securityService = securityService;
        this.transactionValidator = transactionValidator;
        this.notificationService = notificationService;
    }
    
    /**
     * Processes a transaction request.
     * 
     * This method demonstrates the Extract Method refactoring pattern by
     * breaking down the complex transaction processing into smaller, focused methods.
     * 
     * @param request the transaction request to process
     * @return the processed transaction
     * @throws IllegalArgumentException if the request is invalid
     * @throws IllegalStateException if the transaction cannot be processed
     */
    public Transaction processTransaction(TransactionRequest request) {
        logger.info("Processing transaction request: {}", request);
        
        try {
            // Step 1: Validate the request
            validateTransactionRequest(request);
            
            // Step 2: Perform security checks
            performSecurityChecks(request);
            
            // Step 3: Check business rules
            validateBusinessRules(request);
            
            // Step 4: Process the transaction based on type
            Transaction transaction = processTransactionByType(request);
            
            // Step 5: Update account balances
            updateAccountBalances(transaction);
            
            // Step 6: Save transaction to repository
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            // Step 7: Send notifications
            sendTransactionNotifications(savedTransaction);
            
            logger.info("Transaction processed successfully: {}", savedTransaction.getTransactionId());
            return savedTransaction;
            
        } catch (Exception e) {
            logger.error("Failed to process transaction: {}", request, e);
            throw new IllegalStateException("Transaction processing failed", e);
        }
    }
    
    /**
     * Validates the transaction request using the validator service.
     * 
     * @param request the transaction request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTransactionRequest(TransactionRequest request) {
        transactionValidator.validate(request);
    }
    
    /**
     * Performs security checks on the transaction request.
     * 
     * @param request the transaction request to check
     * @throws SecurityException if security checks fail
     */
    private void performSecurityChecks(TransactionRequest request) {
        securityService.validateTransactionRequest(request);
    }
    
    /**
     * Validates business rules for the transaction.
     * 
     * @param request the transaction request to validate
     * @throws IllegalArgumentException if business rules are violated
     */
    private void validateBusinessRules(TransactionRequest request) {
        validateAccountExistence(request);
        validateSufficientFunds(request);
        validateTransactionLimits(request);
    }
    
    /**
     * Validates that the accounts involved in the transaction exist.
     * 
     * @param request the transaction request
     * @throws IllegalArgumentException if accounts don't exist
     */
    private void validateAccountExistence(TransactionRequest request) {
        Optional<Account> fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber());
        if (fromAccount.isEmpty()) {
            throw new IllegalArgumentException("Source account not found: " + request.getFromAccountNumber());
        }
        
        if (request.getToAccountNumber() != null) {
            Optional<Account> toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber());
            if (toAccount.isEmpty()) {
                throw new IllegalArgumentException("Destination account not found: " + request.getToAccountNumber());
            }
        }
    }
    
    /**
     * Validates that the source account has sufficient funds.
     * 
     * @param request the transaction request
     * @throws IllegalArgumentException if insufficient funds
     */
    private void validateSufficientFunds(TransactionRequest request) {
        if (request.getType() == TransactionRequest.TransactionType.WITHDRAWAL || 
            request.getType() == TransactionRequest.TransactionType.TRANSFER) {
            
            Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
            
            if (!fromAccount.hasSufficientFunds(request.getAmount())) {
                throw new IllegalArgumentException("Insufficient funds for transaction");
            }
        }
    }
    
    /**
     * Validates transaction limits based on amount and account type.
     * 
     * @param request the transaction request
     * @throws IllegalArgumentException if limits are exceeded
     */
    private void validateTransactionLimits(TransactionRequest request) {
        BigDecimal amount = request.getAmount();
        
        // Check daily limits
        BigDecimal dailyTotal = transactionRepository.getDailyTransactionTotal(
            request.getFromAccountNumber(), request.getType());
        
        if (dailyTotal.add(amount).compareTo(BigDecimal.valueOf(10000)) > 0) {
            throw new IllegalArgumentException("Daily transaction limit exceeded");
        }
        
        // Check monthly limits
        BigDecimal monthlyTotal = transactionRepository.getMonthlyTransactionTotal(
            request.getFromAccountNumber(), request.getType());
        
        if (monthlyTotal.add(amount).compareTo(BigDecimal.valueOf(50000)) > 0) {
            throw new IllegalArgumentException("Monthly transaction limit exceeded");
        }
    }
    
    /**
     * Processes the transaction based on its type.
     * 
     * @param request the transaction request
     * @return the created transaction
     */
    private Transaction processTransactionByType(TransactionRequest request) {
        switch (request.getType()) {
            case DEPOSIT:
                return processDeposit(request);
            case WITHDRAWAL:
                return processWithdrawal(request);
            case TRANSFER:
                return processTransfer(request);
            case PAYMENT:
                return processPayment(request);
            case REFUND:
                return processRefund(request);
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
        }
    }
    
    /**
     * Processes a deposit transaction.
     * 
     * @param request the deposit request
     * @return the created transaction
     */
    private Transaction processDeposit(TransactionRequest request) {
        return new Transaction(
            null, // No from account for deposits
            request.getToAccountNumber(),
            request.getAmount(),
            Transaction.TransactionType.DEPOSIT,
            request.getDescription(),
            request.getCurrency()
        );
    }
    
    /**
     * Processes a withdrawal transaction.
     * 
     * @param request the withdrawal request
     * @return the created transaction
     */
    private Transaction processWithdrawal(TransactionRequest request) {
        return new Transaction(
            request.getFromAccountNumber(),
            null, // No to account for withdrawals
            request.getAmount(),
            Transaction.TransactionType.WITHDRAWAL,
            request.getDescription(),
            request.getCurrency()
        );
    }
    
    /**
     * Processes a transfer transaction.
     * 
     * @param request the transfer request
     * @return the created transaction
     */
    private Transaction processTransfer(TransactionRequest request) {
        return new Transaction(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            Transaction.TransactionType.TRANSFER,
            request.getDescription(),
            request.getCurrency()
        );
    }
    
    /**
     * Processes a payment transaction.
     * 
     * @param request the payment request
     * @return the created transaction
     */
    private Transaction processPayment(TransactionRequest request) {
        return new Transaction(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            Transaction.TransactionType.PAYMENT,
            request.getDescription(),
            request.getCurrency()
        );
    }
    
    /**
     * Processes a refund transaction.
     * 
     * @param request the refund request
     * @return the created transaction
     */
    private Transaction processRefund(TransactionRequest request) {
        return new Transaction(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            Transaction.TransactionType.REFUND,
            request.getDescription(),
            request.getCurrency()
        );
    }
    
    /**
     * Updates account balances based on the transaction.
     * 
     * @param transaction the transaction to process
     */
    private void updateAccountBalances(Transaction transaction) {
        if (transaction.isTransfer() || transaction.isPayment() || transaction.isRefund()) {
            Account fromAccount = accountRepository.findByAccountNumber(transaction.getFromAccountNumber())
                .orElseThrow(() -> new IllegalStateException("Source account not found"));
            
            Account toAccount = accountRepository.findByAccountNumber(transaction.getToAccountNumber())
                .orElseThrow(() -> new IllegalStateException("Destination account not found"));
            
            fromAccount.transfer(transaction.getAmount(), toAccount);
            
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            
        } else if (transaction.isDeposit()) {
            Account toAccount = accountRepository.findByAccountNumber(transaction.getToAccountNumber())
                .orElseThrow(() -> new IllegalStateException("Destination account not found"));
            
            toAccount.deposit(transaction.getAmount());
            accountRepository.save(toAccount);
            
        } else if (transaction.isWithdrawal()) {
            Account fromAccount = accountRepository.findByAccountNumber(transaction.getFromAccountNumber())
                .orElseThrow(() -> new IllegalStateException("Source account not found"));
            
            fromAccount.withdraw(transaction.getAmount());
            accountRepository.save(fromAccount);
        }
    }
    
    /**
     * Sends notifications for the processed transaction.
     * 
     * @param transaction the processed transaction
     */
    private void sendTransactionNotifications(Transaction transaction) {
        notificationService.sendTransactionConfirmation(transaction);
        
        if (transaction.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            notificationService.sendLargeTransactionAlert(transaction);
        }
    }
} 