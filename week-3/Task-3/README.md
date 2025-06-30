# Bank Account Edge Cases Project

## Overview
This project demonstrates comprehensive edge case identification and testing using AI analysis for a banking system. The implementation includes a robust BankAccount class with critical business logic, extensive error handling, and 39+ edge cases identified and tested.

## Project Structure
```
Task-3/
├── src/
│   ├── main/java/com/edgecase/
│   │   ├── BankAccount.java          # Main business logic class
│   │   ├── Transaction.java          # Transaction entity
│   │   ├── TransactionResult.java    # Transaction result wrapper
│   │   ├── TransactionType.java      # Transaction type enum
│   │   └── BankAccountDemo.java      # Demo application
│   └── test/java/com/edgecase/
│       └── BankAccountEdgeCaseTest.java  # Comprehensive test suite
├── pom.xml                           # Maven configuration
├── README.md                         # This file
└── EDGE_CASES_DOCUMENTATION.md       # Detailed edge case documentation
```

## Features

### Critical Business Logic
- **Account Management**: Create, manage, and close bank accounts
- **Financial Operations**: Deposit, withdraw, and transfer funds
- **Interest Calculation**: Calculate interest with validation
- **Transaction History**: Maintain immutable transaction records
- **Security Features**: Fraud detection and transaction limits

### Edge Case Coverage (39+ Cases)
1. **Withdrawal Edge Cases** (10 cases)
   - Null amounts, negative amounts, insufficient funds
   - Daily/monthly limits, fraud detection
   - Boundary value testing

2. **Deposit Edge Cases** (4 cases)
   - Input validation, suspicious amounts
   - Account status validation

3. **Transfer Edge Cases** (5 cases)
   - Null recipients, self-transfers, currency mismatch
   - Insufficient funds, inactive accounts

4. **Interest Calculation** (4 cases)
   - Negative rates, unrealistic rates
   - Zero balance and zero rate handling

5. **Account Closure** (3 cases)
   - Already closed accounts, positive balances
   - Pending transactions

6. **Transaction Limits** (3 cases)
   - Negative limits, zero limits validation

7. **Boundary Values** (3 cases)
   - Exact balance, precision testing

8. **Concurrent Operations** (1 case)
   - Multiple rapid transactions

9. **Special Characters** (2 cases)
   - International character support

10. **Performance** (1 case)
    - Large transaction volumes

11. **Data Integrity** (3 cases)
    - Immutability, equality, precision

## Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

## Installation and Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Task-3
```

### 2. Compile the Project
```bash
mvn compile
```

### 3. Run Tests
```bash
mvn test
```

### 4. Run Demo Application
```bash
mvn exec:java -Dexec.mainClass="com.edgecase.BankAccountDemo"
```

## Usage Examples

### Basic Account Operations
```java
// Create accounts
BankAccount account1 = new BankAccount("ACC001", "John Doe", 
    BigDecimal.valueOf(1000), "USD", BankAccount.AccountType.SAVINGS);
BankAccount account2 = new BankAccount("ACC002", "Jane Smith", 
    BigDecimal.valueOf(500), "USD", BankAccount.AccountType.CHECKING);

// Perform transactions
TransactionResult result = account1.withdraw(BigDecimal.valueOf(100), "ATM withdrawal");
if (result.isSuccess()) {
    System.out.println("Withdrawal successful: " + result.getMessage());
} else {
    System.out.println("Withdrawal failed: " + result.getMessage());
}

// Transfer between accounts
result = account1.transfer(account2, BigDecimal.valueOf(50), "Payment");
```

### Edge Case Testing
```java
// Test null amount
TransactionResult result = account.withdraw(null, "Test");
assertFalse(result.isSuccess());
assertEquals("Amount cannot be null", result.getMessage());

// Test insufficient funds
result = account.withdraw(BigDecimal.valueOf(1500), "Large withdrawal");
assertFalse(result.isSuccess());
assertEquals("Insufficient funds", result.getMessage());

// Test fraud detection
result = account.withdraw(BigDecimal.valueOf(150000), "Suspicious");
assertFalse(result.isSuccess());
assertEquals("Transaction amount too large, requires approval", result.getMessage());
```

## Test Results

### Running All Tests
```bash
mvn test
```

Expected output:
```
[INFO] Tests run: 39, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 39, Failures: 0, Errors: 0, Skipped: 0
```

### Test Categories
- **Input Validation Tests**: 14 tests
- **Business Logic Tests**: 15 tests
- **Boundary Value Tests**: 3 tests
- **Data Integrity Tests**: 3 tests
- **Performance Tests**: 2 tests
- **Special Character Tests**: 2 tests

## Demo Application Output

When running the demo application, you'll see output like:
```
=== Bank Account Edge Cases Demonstration ===

Initial Account States:
Account 1: BankAccount{accountNumber='ACC001', accountHolderName='John Doe', balance=1000, currency='USD', accountType=SAVINGS, isActive=true}
Account 2: BankAccount{accountNumber='ACC002', accountHolderName='Jane Smith', balance=500, currency='USD', accountType=CHECKING, isActive=true}

--- Withdrawal Edge Cases ---
1. Withdrawal with null amount:
   Result: Amount cannot be null
2. Withdrawal with negative amount:
   Result: Amount must be positive
3. Withdrawal with zero amount:
   Result: Amount must be positive
4. Withdrawal with insufficient funds:
   Result: Insufficient funds
5. Withdrawal with extremely large amount:
   Result: Transaction amount too large, requires approval
   Current balance: 1000

[... additional edge case demonstrations ...]

=== Edge Case Demonstration Complete ===
```

## Success Criteria Met

✅ **10+ Edge Cases Identified**: 39 edge cases identified and tested
✅ **Robust Error Handling**: Comprehensive error handling for all scenarios
✅ **Clear Documentation**: Detailed documentation of all edge cases
✅ **Comprehensive Testing**: Full test suite with 39 test methods
✅ **Business Logic Coverage**: All critical banking operations covered
✅ **Security Implementation**: Fraud detection and transaction limits
✅ **Data Integrity**: Immutable collections and atomic operations

## Key Features

### Error Handling
- **Graceful Degradation**: All operations return meaningful error messages
- **State Preservation**: Failed operations don't modify account state
- **Exception Handling**: Critical errors throw appropriate exceptions
- **Audit Trail**: All transactions are logged for compliance

### Security Measures
- **Fraud Detection**: Large transactions require approval
- **Transaction Limits**: Daily and monthly limits prevent abuse
- **Currency Validation**: Prevents cross-currency issues
- **Account Status Validation**: Only active accounts can transact

### Data Integrity
- **Immutable Collections**: Transaction history cannot be modified externally
- **Atomic Operations**: Transfers are atomic with rollback capability
- **Precision Handling**: BigDecimal ensures accurate financial calculations
- **State Validation**: All operations validate account state before execution

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new edge cases
4. Ensure all tests pass
5. Submit a pull request

## License

This project is for educational purposes and demonstrates edge case testing methodologies.

## Contact

For questions or issues, please refer to the documentation in `EDGE_CASES_DOCUMENTATION.md`. 