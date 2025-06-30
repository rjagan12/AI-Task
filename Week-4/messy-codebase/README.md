# Messy Bank System - Code Analysis Project

This is a deliberately messy Java banking system created for demonstrating code analysis, refactoring, and improvement techniques.

## Project Structure

```
messy-codebase/
├── src/main/java/com/example/bank/
│   ├── BankAccount.java      # Main account class with multiple issues
│   ├── UserManager.java      # User management with security vulnerabilities
│   ├── TransactionProcessor.java # Complex transaction processing
│   └── Main.java            # Demo application
├── pom.xml                  # Maven configuration
└── README.md               # This file
```

## Module 1: AI Code Analysis Foundations

### Issues Found in Messy Functions

#### BankAccount.java
1. **Complexity Issues:**
   - `processTransaction()` method: 200+ lines with deeply nested conditions
   - `generateReport()` method: Multiple responsibilities (file I/O, email, formatting)
   - `createAccount()` method: Too many parameters (14 parameters)

2. **Security Issues:**
   - Public fields exposing sensitive data (`accountNumber`, `balance`, `password`)
   - SQL injection vulnerabilities in multiple methods
   - Hardcoded database credentials
   - Command injection vulnerability in email sending
   - Insecure file path handling

3. **Bugs:**
   - Resource leaks (database connections not properly closed)
   - Generic exception handling
   - Inefficient string concatenation in loops
   - Race conditions with static variables

#### UserManager.java
1. **Security Issues:**
   - Plain text password storage
   - Exposed SSN data
   - Command injection vulnerabilities
   - Logging sensitive information
   - Hardcoded admin credentials

2. **Performance Issues:**
   - Inefficient search algorithms
   - Unnecessary sorting operations
   - Blocking operations in authentication

#### TransactionProcessor.java
1. **Complexity Issues:**
   - `processTransaction()` method: 300+ lines with extreme nesting
   - Multiple responsibilities in single method
   - Complex validation logic mixed with business logic

2. **Performance Issues:**
   - Inefficient daily/monthly limit checks
   - Linear search through all transactions
   - Redundant calculations

## Module 2: Security & Performance Deep Dive

### 5 Security Issues Identified:
1. **SQL Injection** - Multiple instances in BankAccount and UserManager
2. **Command Injection** - Runtime.exec() calls with user input
3. **Information Exposure** - Plain text passwords and SSNs
4. **Insecure File Operations** - Hardcoded paths and permissions
5. **Authentication Bypass** - Weak password validation

### 2 Performance Bottlenecks:
1. **Inefficient Transaction Processing** - O(n) searches through all transactions
2. **String Concatenation in Loops** - Creating new String objects repeatedly

## Module 3: Documentation & Code Style

### Functions Requiring Documentation:
1. `processTransaction()` - Complex business logic needs clear documentation
2. `authenticateUser()` - Security-critical method needs detailed docs
3. `generateReport()` - Multiple responsibilities need explanation

### Style Issues:
- Inconsistent naming conventions
- No proper error handling
- Missing input validation
- Poor separation of concerns

## Module 4: Advanced Refactoring

### Target for Refactoring:
- `processTransaction()` method (300+ lines) in TransactionProcessor.java
- `processTransaction()` method (200+ lines) in BankAccount.java
- `generateReport()` method in BankAccount.java

## Running the Code

```bash
cd messy-codebase
mvn compile
mvn exec:java -Dexec.mainClass="com.example.bank.Main"
```

## Next Steps

This codebase will be refactored to demonstrate:
- Extract Method refactoring
- SOLID principles application
- Parameter Object pattern
- Security improvements
- Performance optimizations
- Proper error handling
- Unit testing 