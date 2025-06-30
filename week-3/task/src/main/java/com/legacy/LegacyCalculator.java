package com.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Calculator - A simple calculator class with basic arithmetic operations.
 * This class has no existing tests and needs comprehensive test coverage.
 */
public class LegacyCalculator {
    private static final Logger logger = LoggerFactory.getLogger(LegacyCalculator.class);
    
    private final List<String> operationHistory = new ArrayList<>();
    private BigDecimal currentResult = BigDecimal.ZERO;
    private boolean isInitialized = false;
    
    /**
     * Initializes the calculator
     */
    public boolean initialize() {
        try {
            isInitialized = true;
            currentResult = BigDecimal.ZERO;
            operationHistory.clear();
            logger.info("Calculator initialized successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to initialize calculator", e);
            return false;
        }
    }
    
    /**
     * Adds two numbers
     */
    public BigDecimal add(BigDecimal a, BigDecimal b) {
        if (!isInitialized) {
            throw new IllegalStateException("Calculator not initialized");
        }
        
        if (a == null || b == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        
        BigDecimal result = a.add(b);
        currentResult = result;
        operationHistory.add(String.format("ADD: %s + %s = %s", a, b, result));
        logger.info("Addition performed: {} + {} = {}", a, b, result);
        return result;
    }
    
    /**
     * Subtracts two numbers
     */
    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        if (!isInitialized) {
            throw new IllegalStateException("Calculator not initialized");
        }
        
        if (a == null || b == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        
        BigDecimal result = a.subtract(b);
        currentResult = result;
        operationHistory.add(String.format("SUBTRACT: %s - %s = %s", a, b, result));
        logger.info("Subtraction performed: {} - {} = {}", a, b, result);
        return result;
    }
    
    /**
     * Multiplies two numbers
     */
    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        if (!isInitialized) {
            throw new IllegalStateException("Calculator not initialized");
        }
        
        if (a == null || b == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        
        BigDecimal result = a.multiply(b);
        currentResult = result;
        operationHistory.add(String.format("MULTIPLY: %s * %s = %s", a, b, result));
        logger.info("Multiplication performed: {} * {} = {}", a, b, result);
        return result;
    }
    
    /**
     * Divides two numbers
     */
    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (!isInitialized) {
            throw new IllegalStateException("Calculator not initialized");
        }
        
        if (a == null || b == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero");
        }
        
        BigDecimal result = a.divide(b, 10, RoundingMode.HALF_UP);
        currentResult = result;
        operationHistory.add(String.format("DIVIDE: %s / %s = %s", a, b, result));
        logger.info("Division performed: {} / {} = {}", a, b, result);
        return result;
    }
    
    /**
     * Calculates the power of a number
     */
    public BigDecimal power(BigDecimal base, int exponent) {
        if (!isInitialized) {
            throw new IllegalStateException("Calculator not initialized");
        }
        
        if (base == null) {
            throw new IllegalArgumentException("Base cannot be null");
        }
        
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative");
        }
        
        BigDecimal result = base.pow(exponent);
        currentResult = result;
        operationHistory.add(String.format("POWER: %s ^ %d = %s", base, exponent, result));
        logger.info("Power calculation performed: {} ^ {} = {}", base, exponent, result);
        return result;
    }
    
    /**
     * Calculates the square root of a number
     */
    public BigDecimal sqrt(BigDecimal number) {
        if (!isInitialized) {
            throw new IllegalStateException("Calculator not initialized");
        }
        
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        
        if (number.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot calculate square root of negative number");
        }
        
        // Simple square root approximation using Newton's method
        BigDecimal result = calculateSquareRoot(number);
        currentResult = result;
        operationHistory.add(String.format("SQRT: sqrt(%s) = %s", number, result));
        logger.info("Square root calculated: sqrt({}) = {}", number, result);
        return result;
    }
    
    /**
     * Calculates the factorial of a number
     */
    public BigDecimal factorial(int n) {
        if (!isInitialized) {
            throw new IllegalStateException("Calculator not initialized");
        }
        
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers");
        }
        
        if (n > 100) {
            throw new IllegalArgumentException("Factorial calculation limited to numbers <= 100");
        }
        
        BigDecimal result = calculateFactorial(n);
        currentResult = result;
        operationHistory.add(String.format("FACTORIAL: %d! = %s", n, result));
        logger.info("Factorial calculated: {}! = {}", n, result);
        return result;
    }
    
    /**
     * Gets the current result
     */
    public BigDecimal getCurrentResult() {
        return currentResult;
    }
    
    /**
     * Gets the operation history
     */
    public List<String> getOperationHistory() {
        return new ArrayList<>(operationHistory);
    }
    
    /**
     * Clears the calculator state
     */
    public void clear() {
        currentResult = BigDecimal.ZERO;
        operationHistory.clear();
        logger.info("Calculator cleared");
    }
    
    /**
     * Checks if the calculator is initialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * Gets the number of operations performed
     */
    public int getOperationCount() {
        return operationHistory.size();
    }
    
    // Private helper methods
    
    private BigDecimal calculateSquareRoot(BigDecimal number) {
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = number;
        BigDecimal y = BigDecimal.ZERO;
        BigDecimal epsilon = new BigDecimal("0.0000000001");
        
        while (x.subtract(y).abs().compareTo(epsilon) > 0) {
            y = x;
            x = number.divide(x, 10, RoundingMode.HALF_UP).add(x).divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);
        }
        
        return x.setScale(10, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateFactorial(int n) {
        if (n == 0 || n == 1) {
            return BigDecimal.ONE;
        }
        
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i));
        }
        
        return result;
    }
} 