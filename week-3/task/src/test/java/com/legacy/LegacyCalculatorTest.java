package com.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LegacyCalculator Tests")
class LegacyCalculatorTest {

    private LegacyCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LegacyCalculator();
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize calculator successfully")
        void shouldInitializeCalculatorSuccessfully() {
            // When
            boolean result = calculator.initialize();

            // Then
            assertTrue(result);
            assertTrue(calculator.isInitialized());
            assertEquals(BigDecimal.ZERO, calculator.getCurrentResult());
            assertTrue(calculator.getOperationHistory().isEmpty());
        }

        @Test
        @DisplayName("Should not be initialized by default")
        void shouldNotBeInitializedByDefault() {
            // Then
            assertFalse(calculator.isInitialized());
        }
    }

    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should add two positive numbers")
        void shouldAddTwoPositiveNumbers() {
            // Given
            BigDecimal a = new BigDecimal("10.5");
            BigDecimal b = new BigDecimal("20.3");

            // When
            BigDecimal result = calculator.add(a, b);

            // Then
            assertEquals(new BigDecimal("30.8"), result);
            assertEquals(new BigDecimal("30.8"), calculator.getCurrentResult());
            assertEquals(1, calculator.getOperationCount());
        }

        @Test
        @DisplayName("Should add negative numbers")
        void shouldAddNegativeNumbers() {
            // Given
            BigDecimal a = new BigDecimal("-10.5");
            BigDecimal b = new BigDecimal("-20.3");

            // When
            BigDecimal result = calculator.add(a, b);

            // Then
            assertEquals(new BigDecimal("-30.8"), result);
        }

        @Test
        @DisplayName("Should add zero")
        void shouldAddZero() {
            // Given
            BigDecimal a = new BigDecimal("10.5");
            BigDecimal b = BigDecimal.ZERO;

            // When
            BigDecimal result = calculator.add(a, b);

            // Then
            assertEquals(new BigDecimal("10.5"), result);
        }

        @Test
        @DisplayName("Should throw exception when not initialized")
        void shouldThrowExceptionWhenNotInitialized() {
            // Given
            LegacyCalculator uninitializedCalculator = new LegacyCalculator();
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = new BigDecimal("20");

            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                uninitializedCalculator.add(a, b);
            });
        }

        @Test
        @DisplayName("Should throw exception for null operands")
        void shouldThrowExceptionForNullOperands() {
            // Given
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.add(a, b);
            });
        }

        @ParameterizedTest
        @CsvSource({
            "10.5, 20.3, 30.8",
            "0, 0, 0",
            "-5, 10, 5",
            "1000000, 2000000, 3000000"
        })
        @DisplayName("Should add various number combinations")
        void shouldAddVariousNumberCombinations(String a, String b, String expected) {
            // Given
            BigDecimal operandA = new BigDecimal(a);
            BigDecimal operandB = new BigDecimal(b);
            BigDecimal expectedResult = new BigDecimal(expected);

            // When
            BigDecimal result = calculator.add(operandA, operandB);

            // Then
            assertEquals(expectedResult, result);
        }
    }

    @Nested
    @DisplayName("Subtraction Tests")
    class SubtractionTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should subtract two positive numbers")
        void shouldSubtractTwoPositiveNumbers() {
            // Given
            BigDecimal a = new BigDecimal("20.5");
            BigDecimal b = new BigDecimal("10.3");

            // When
            BigDecimal result = calculator.subtract(a, b);

            // Then
            assertEquals(new BigDecimal("10.2"), result);
            assertEquals(new BigDecimal("10.2"), calculator.getCurrentResult());
        }

        @Test
        @DisplayName("Should handle negative result")
        void shouldHandleNegativeResult() {
            // Given
            BigDecimal a = new BigDecimal("10.5");
            BigDecimal b = new BigDecimal("20.3");

            // When
            BigDecimal result = calculator.subtract(a, b);

            // Then
            assertEquals(new BigDecimal("-9.8"), result);
        }

        @Test
        @DisplayName("Should subtract zero")
        void shouldSubtractZero() {
            // Given
            BigDecimal a = new BigDecimal("10.5");
            BigDecimal b = BigDecimal.ZERO;

            // When
            BigDecimal result = calculator.subtract(a, b);

            // Then
            assertEquals(new BigDecimal("10.5"), result);
        }

        @Test
        @DisplayName("Should throw exception for null operands")
        void shouldThrowExceptionForNullOperands() {
            // Given
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.subtract(a, b);
            });
        }
    }

    @Nested
    @DisplayName("Multiplication Tests")
    class MultiplicationTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should multiply two positive numbers")
        void shouldMultiplyTwoPositiveNumbers() {
            // Given
            BigDecimal a = new BigDecimal("10.5");
            BigDecimal b = new BigDecimal("2.0");

            // When
            BigDecimal result = calculator.multiply(a, b);

            // Then
            assertEquals(new BigDecimal("21.0"), result);
        }

        @Test
        @DisplayName("Should multiply by zero")
        void shouldMultiplyByZero() {
            // Given
            BigDecimal a = new BigDecimal("10.5");
            BigDecimal b = BigDecimal.ZERO;

            // When
            BigDecimal result = calculator.multiply(a, b);

            // Then
            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        @DisplayName("Should multiply negative numbers")
        void shouldMultiplyNegativeNumbers() {
            // Given
            BigDecimal a = new BigDecimal("-10.5");
            BigDecimal b = new BigDecimal("-2.0");

            // When
            BigDecimal result = calculator.multiply(a, b);

            // Then
            assertEquals(new BigDecimal("21.0"), result);
        }

        @Test
        @DisplayName("Should throw exception for null operands")
        void shouldThrowExceptionForNullOperands() {
            // Given
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.multiply(a, b);
            });
        }
    }

    @Nested
    @DisplayName("Division Tests")
    class DivisionTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should divide two positive numbers")
        void shouldDivideTwoPositiveNumbers() {
            // Given
            BigDecimal a = new BigDecimal("10.0");
            BigDecimal b = new BigDecimal("2.0");

            // When
            BigDecimal result = calculator.divide(a, b);

            // Then
            assertEquals(new BigDecimal("5.0000000000"), result);
        }

        @Test
        @DisplayName("Should handle decimal division")
        void shouldHandleDecimalDivision() {
            // Given
            BigDecimal a = new BigDecimal("10.0");
            BigDecimal b = new BigDecimal("3.0");

            // When
            BigDecimal result = calculator.divide(a, b);

            // Then
            assertEquals(new BigDecimal("3.3333333333"), result);
        }

        @Test
        @DisplayName("Should throw exception for division by zero")
        void shouldThrowExceptionForDivisionByZero() {
            // Given
            BigDecimal a = new BigDecimal("10.0");
            BigDecimal b = BigDecimal.ZERO;

            // When & Then
            assertThrows(ArithmeticException.class, () -> {
                calculator.divide(a, b);
            });
        }

        @Test
        @DisplayName("Should throw exception for null operands")
        void shouldThrowExceptionForNullOperands() {
            // Given
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.divide(a, b);
            });
        }

        @Test
        @DisplayName("Should handle division by one")
        void shouldHandleDivisionByOne() {
            // Given
            BigDecimal a = new BigDecimal("10.5");
            BigDecimal b = BigDecimal.ONE;

            // When
            BigDecimal result = calculator.divide(a, b);

            // Then
            assertEquals(new BigDecimal("10.5000000000"), result);
        }
    }

    @Nested
    @DisplayName("Power Tests")
    class PowerTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should calculate positive power")
        void shouldCalculatePositivePower() {
            // Given
            BigDecimal base = new BigDecimal("2.0");
            int exponent = 3;

            // When
            BigDecimal result = calculator.power(base, exponent);

            // Then
            assertEquals(new BigDecimal("8.0"), result);
        }

        @Test
        @DisplayName("Should calculate power of zero")
        void shouldCalculatePowerOfZero() {
            // Given
            BigDecimal base = new BigDecimal("5.0");
            int exponent = 0;

            // When
            BigDecimal result = calculator.power(base, exponent);

            // Then
            assertEquals(BigDecimal.ONE, result);
        }

        @Test
        @DisplayName("Should calculate power of one")
        void shouldCalculatePowerOfOne() {
            // Given
            BigDecimal base = new BigDecimal("5.0");
            int exponent = 1;

            // When
            BigDecimal result = calculator.power(base, exponent);

            // Then
            assertEquals(new BigDecimal("5.0"), result);
        }

        @Test
        @DisplayName("Should throw exception for negative exponent")
        void shouldThrowExceptionForNegativeExponent() {
            // Given
            BigDecimal base = new BigDecimal("2.0");
            int exponent = -1;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.power(base, exponent);
            });
        }

        @Test
        @DisplayName("Should throw exception for null base")
        void shouldThrowExceptionForNullBase() {
            // Given
            BigDecimal base = null;
            int exponent = 2;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.power(base, exponent);
            });
        }

        @Test
        @DisplayName("Should handle large powers")
        void shouldHandleLargePowers() {
            // Given
            BigDecimal base = new BigDecimal("2.0");
            int exponent = 10;

            // When
            BigDecimal result = calculator.power(base, exponent);

            // Then
            assertEquals(new BigDecimal("1024.0"), result);
        }
    }

    @Nested
    @DisplayName("Square Root Tests")
    class SquareRootTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should calculate square root of positive number")
        void shouldCalculateSquareRootOfPositiveNumber() {
            // Given
            BigDecimal number = new BigDecimal("16.0");

            // When
            BigDecimal result = calculator.sqrt(number);

            // Then
            assertEquals(new BigDecimal("4.0000000000"), result);
        }

        @Test
        @DisplayName("Should calculate square root of zero")
        void shouldCalculateSquareRootOfZero() {
            // Given
            BigDecimal number = BigDecimal.ZERO;

            // When
            BigDecimal result = calculator.sqrt(number);

            // Then
            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        @DisplayName("Should calculate square root of one")
        void shouldCalculateSquareRootOfOne() {
            // Given
            BigDecimal number = BigDecimal.ONE;

            // When
            BigDecimal result = calculator.sqrt(number);

            // Then
            assertEquals(BigDecimal.ONE, result);
        }

        @Test
        @DisplayName("Should throw exception for negative number")
        void shouldThrowExceptionForNegativeNumber() {
            // Given
            BigDecimal number = new BigDecimal("-16.0");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.sqrt(number);
            });
        }

        @Test
        @DisplayName("Should throw exception for null number")
        void shouldThrowExceptionForNullNumber() {
            // Given
            BigDecimal number = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.sqrt(number);
            });
        }

        @Test
        @DisplayName("Should calculate square root of decimal")
        void shouldCalculateSquareRootOfDecimal() {
            // Given
            BigDecimal number = new BigDecimal("2.0");

            // When
            BigDecimal result = calculator.sqrt(number);

            // Then
            // Should be approximately sqrt(2) ≈ 1.4142135624
            assertTrue(result.compareTo(new BigDecimal("1.4142135623")) > 0);
            assertTrue(result.compareTo(new BigDecimal("1.4142135625")) < 0);
        }
    }

    @Nested
    @DisplayName("Factorial Tests")
    class FactorialTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should calculate factorial of zero")
        void shouldCalculateFactorialOfZero() {
            // Given
            int n = 0;

            // When
            BigDecimal result = calculator.factorial(n);

            // Then
            assertEquals(BigDecimal.ONE, result);
        }

        @Test
        @DisplayName("Should calculate factorial of one")
        void shouldCalculateFactorialOfOne() {
            // Given
            int n = 1;

            // When
            BigDecimal result = calculator.factorial(n);

            // Then
            assertEquals(BigDecimal.ONE, result);
        }

        @Test
        @DisplayName("Should calculate factorial of five")
        void shouldCalculateFactorialOfFive() {
            // Given
            int n = 5;

            // When
            BigDecimal result = calculator.factorial(n);

            // Then
            assertEquals(new BigDecimal("120"), result);
        }

        @Test
        @DisplayName("Should calculate factorial of ten")
        void shouldCalculateFactorialOfTen() {
            // Given
            int n = 10;

            // When
            BigDecimal result = calculator.factorial(n);

            // Then
            assertEquals(new BigDecimal("3628800"), result);
        }

        @Test
        @DisplayName("Should throw exception for negative number")
        void shouldThrowExceptionForNegativeNumber() {
            // Given
            int n = -1;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.factorial(n);
            });
        }

        @Test
        @DisplayName("Should throw exception for large number")
        void shouldThrowExceptionForLargeNumber() {
            // Given
            int n = 101;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                calculator.factorial(n);
            });
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
        @DisplayName("Should calculate factorial for various numbers")
        void shouldCalculateFactorialForVariousNumbers(int n) {
            // When
            BigDecimal result = calculator.factorial(n);

            // Then
            assertNotNull(result);
            assertTrue(result.compareTo(BigDecimal.ZERO) >= 0);
        }
    }

    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {

        @Test
        @DisplayName("Should maintain operation history")
        void shouldMaintainOperationHistory() {
            // Given
            calculator.initialize();
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = new BigDecimal("5");

            // When
            calculator.add(a, b);
            calculator.subtract(a, b);
            calculator.multiply(a, b);

            // Then
            List<String> history = calculator.getOperationHistory();
            assertEquals(3, history.size());
            assertTrue(history.get(0).contains("ADD"));
            assertTrue(history.get(1).contains("SUBTRACT"));
            assertTrue(history.get(2).contains("MULTIPLY"));
        }

        @Test
        @DisplayName("Should update current result after each operation")
        void shouldUpdateCurrentResultAfterEachOperation() {
            // Given
            calculator.initialize();
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = new BigDecimal("5");

            // When
            calculator.add(a, b);
            assertEquals(new BigDecimal("15"), calculator.getCurrentResult());

            calculator.subtract(a, b);
            assertEquals(new BigDecimal("5"), calculator.getCurrentResult());

            calculator.multiply(a, b);
            assertEquals(new BigDecimal("50"), calculator.getCurrentResult());
        }

        @Test
        @DisplayName("Should clear calculator state")
        void shouldClearCalculatorState() {
            // Given
            calculator.initialize();
            calculator.add(new BigDecimal("10"), new BigDecimal("5"));

            // Verify state is not empty
            assertEquals(1, calculator.getOperationCount());
            assertEquals(new BigDecimal("15"), calculator.getCurrentResult());

            // When
            calculator.clear();

            // Then
            assertEquals(0, calculator.getOperationCount());
            assertEquals(BigDecimal.ZERO, calculator.getCurrentResult());
            assertTrue(calculator.getOperationHistory().isEmpty());
        }

        @Test
        @DisplayName("Should return operation count correctly")
        void shouldReturnOperationCountCorrectly() {
            // Given
            calculator.initialize();

            // When
            calculator.add(new BigDecimal("1"), new BigDecimal("2"));
            calculator.subtract(new BigDecimal("5"), new BigDecimal("3"));
            calculator.multiply(new BigDecimal("4"), new BigDecimal("2"));

            // Then
            assertEquals(3, calculator.getOperationCount());
        }

        @Test
        @DisplayName("Should return copy of operation history")
        void shouldReturnCopyOfOperationHistory() {
            // Given
            calculator.initialize();
            calculator.add(new BigDecimal("1"), new BigDecimal("2"));

            // When
            List<String> history1 = calculator.getOperationHistory();
            List<String> history2 = calculator.getOperationHistory();

            // Then
            assertNotSame(history1, history2); // Should be different objects
            assertEquals(history1, history2); // But same content
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @BeforeEach
        void setUp() {
            calculator.initialize();
        }

        @Test
        @DisplayName("Should handle very large numbers")
        void shouldHandleVeryLargeNumbers() {
            // Given
            BigDecimal largeNumber = new BigDecimal("999999999999999999.9999999999");
            BigDecimal smallNumber = new BigDecimal("0.0000000001");

            // When
            BigDecimal result = calculator.add(largeNumber, smallNumber);

            // Then
            assertEquals(new BigDecimal("1000000000000000000.0000000000"), result);
        }

        @Test
        @DisplayName("Should handle very small numbers")
        void shouldHandleVerySmallNumbers() {
            // Given
            BigDecimal smallNumber1 = new BigDecimal("0.0000000001");
            BigDecimal smallNumber2 = new BigDecimal("0.0000000002");

            // When
            BigDecimal result = calculator.add(smallNumber1, smallNumber2);

            // Then
            assertEquals(new BigDecimal("0.0000000003"), result);
        }

        @Test
        @DisplayName("Should handle precision in calculations")
        void shouldHandlePrecisionInCalculations() {
            // Given
            BigDecimal a = new BigDecimal("0.1");
            BigDecimal b = new BigDecimal("0.2");

            // When
            BigDecimal result = calculator.add(a, b);

            // Then
            assertEquals(new BigDecimal("0.3"), result);
        }

        @Test
        @DisplayName("Should handle multiple operations in sequence")
        void shouldHandleMultipleOperationsInSequence() {
            // Given
            BigDecimal a = new BigDecimal("10");
            BigDecimal b = new BigDecimal("5");
            BigDecimal c = new BigDecimal("2");

            // When
            BigDecimal result1 = calculator.add(a, b); // 15
            BigDecimal result2 = calculator.multiply(result1, c); // 30
            BigDecimal result3 = calculator.subtract(result2, a); // 20
            BigDecimal result4 = calculator.divide(result3, c); // 10

            // Then
            assertEquals(new BigDecimal("10.0000000000"), result4);
            assertEquals(4, calculator.getOperationCount());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should perform complex calculation workflow")
        void shouldPerformComplexCalculationWorkflow() {
            // Given
            calculator.initialize();

            // When - Perform a complex calculation: (10 + 5) * 2 - 3 / 2
            BigDecimal step1 = calculator.add(new BigDecimal("10"), new BigDecimal("5")); // 15
            BigDecimal step2 = calculator.multiply(step1, new BigDecimal("2")); // 30
            BigDecimal step3 = calculator.subtract(step2, new BigDecimal("3")); // 27
            BigDecimal finalResult = calculator.divide(step3, new BigDecimal("2")); // 13.5

            // Then
            assertEquals(new BigDecimal("13.5000000000"), finalResult);
            assertEquals(4, calculator.getOperationCount());

            // Verify operation history
            List<String> history = calculator.getOperationHistory();
            assertEquals(4, history.size());
            assertTrue(history.get(0).contains("ADD"));
            assertTrue(history.get(1).contains("MULTIPLY"));
            assertTrue(history.get(2).contains("SUBTRACT"));
            assertTrue(history.get(3).contains("DIVIDE"));
        }

        @Test
        @DisplayName("Should handle mathematical expression evaluation")
        void shouldHandleMathematicalExpressionEvaluation() {
            // Given
            calculator.initialize();

            // When - Evaluate: sqrt(16) + 2^3 - 5!
            BigDecimal sqrtResult = calculator.sqrt(new BigDecimal("16")); // 4
            BigDecimal powerResult = calculator.power(new BigDecimal("2"), 3); // 8
            BigDecimal factorialResult = calculator.factorial(5); // 120

            BigDecimal finalResult = calculator.add(sqrtResult, powerResult); // 12
            finalResult = calculator.subtract(finalResult, factorialResult); // -108

            // Then
            assertEquals(new BigDecimal("-108"), finalResult);
            assertEquals(5, calculator.getOperationCount());
        }
    }
} 