package com.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LegacyDataProcessor Tests")
class LegacyDataProcessorTest {

    private LegacyDataProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new LegacyDataProcessor();
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize with valid configuration")
        void shouldInitializeWithValidConfiguration() {
            // Given
            Map<String, Object> config = new HashMap<>();
            config.put("maxCacheSize", 1000);
            config.put("enableLogging", true);

            // When
            boolean result = processor.initialize(config);

            // Then
            assertTrue(result);
            assertTrue(processor.isInitialized());
        }

        @Test
        @DisplayName("Should fail initialization with null configuration")
        void shouldFailInitializationWithNullConfiguration() {
            // When
            boolean result = processor.initialize(null);

            // Then
            assertFalse(result);
            assertFalse(processor.isInitialized());
        }

        @Test
        @DisplayName("Should fail initialization with empty configuration")
        void shouldFailInitializationWithEmptyConfiguration() {
            // Given
            Map<String, Object> config = new HashMap<>();

            // When
            boolean result = processor.initialize(config);

            // Then
            assertFalse(result);
            assertFalse(processor.isInitialized());
        }

        @Test
        @DisplayName("Should fail initialization with missing required parameters")
        void shouldFailInitializationWithMissingRequiredParameters() {
            // Given
            Map<String, Object> config = new HashMap<>();
            config.put("maxCacheSize", 1000);
            // Missing enableLogging

            // When
            boolean result = processor.initialize(config);

            // Then
            assertFalse(result);
            assertFalse(processor.isInitialized());
        }
    }

    @Nested
    @DisplayName("Data Processing Tests")
    class DataProcessingTests {

        @Test
        @DisplayName("Should process valid data successfully")
        void shouldProcessValidDataSuccessfully() {
            // Given
            String validData = "AB123456,Product A,100.50,2024-01-15 10:30:00\n" +
                              "CD789012,Product B,200.75,2024-01-15 11:45:00";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(validData);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(result.getMessage().contains("Successfully processed 2 records"));
            assertNotNull(result.getData());
            
            Map<String, Object> data = result.getData();
            assertEquals(2, data.get("count"));
            assertEquals(new BigDecimal("301.25"), data.get("total"));
            assertEquals(new BigDecimal("150.63"), data.get("average"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should handle null and empty data")
        void shouldHandleNullAndEmptyData(String input) {
            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(input);

            // Then
            assertFalse(result.isSuccess());
            assertEquals("Invalid input data", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("Should handle data with invalid ID format")
        void shouldHandleDataWithInvalidIdFormat() {
            // Given
            String invalidData = "INVALID,Product A,100.50\n" +
                                "AB123456,Product B,200.75";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(invalidData);

            // Then
            assertTrue(result.isSuccess()); // Should still process valid lines
            assertTrue(result.getMessage().contains("Successfully processed 1 records"));
            assertEquals(1, processor.getErrorCount());
        }

        @Test
        @DisplayName("Should handle data with negative values")
        void shouldHandleDataWithNegativeValues() {
            // Given
            String negativeData = "AB123456,Product A,-100.50\n" +
                                 "CD789012,Product B,200.75";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(negativeData);

            // Then
            assertTrue(result.isSuccess()); // Should still process valid lines
            assertTrue(result.getMessage().contains("Successfully processed 1 records"));
            assertEquals(1, processor.getErrorCount());
        }

        @Test
        @DisplayName("Should handle data with invalid numeric values")
        void shouldHandleDataWithInvalidNumericValues() {
            // Given
            String invalidNumericData = "AB123456,Product A,invalid\n" +
                                       "CD789012,Product B,200.75";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(invalidNumericData);

            // Then
            assertTrue(result.isSuccess()); // Should still process valid lines
            assertTrue(result.getMessage().contains("Successfully processed 1 records"));
            assertEquals(1, processor.getErrorCount());
        }

        @Test
        @DisplayName("Should apply 5% discount for amounts over 1000")
        void shouldApplyDiscountForAmountsOver1000() {
            // Given
            String largeAmountData = "AB123456,Product A,600.00\n" +
                                    "CD789012,Product B,500.00";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(largeAmountData);

            // Then
            assertTrue(result.isSuccess());
            Map<String, Object> data = result.getData();
            BigDecimal total = (BigDecimal) data.get("total");
            // Expected: 1100 - 5% = 1045.00
            assertEquals(new BigDecimal("1045.00"), total);
        }

        @Test
        @DisplayName("Should apply bulk discount for more than 10 items")
        void shouldApplyBulkDiscountForMoreThan10Items() {
            // Given
            StringBuilder largeDataSet = new StringBuilder();
            for (int i = 1; i <= 12; i++) {
                largeDataSet.append(String.format("AB%06d,Product %d,100.00\n", i, i));
            }

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(largeDataSet.toString());

            // Then
            assertTrue(result.isSuccess());
            Map<String, Object> data = result.getData();
            BigDecimal total = (BigDecimal) data.get("total");
            // Expected: 1200 - 2% = 1176.00
            assertEquals(new BigDecimal("1176.00"), total);
        }

        @Test
        @DisplayName("Should handle empty data set")
        void shouldHandleEmptyDataSet() {
            // Given
            String emptyData = "";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(emptyData);

            // Then
            assertFalse(result.isSuccess());
            assertEquals("Invalid input data", result.getMessage());
        }

        @Test
        @DisplayName("Should handle data with only empty lines")
        void shouldHandleDataWithOnlyEmptyLines() {
            // Given
            String emptyLinesData = "\n\n  \n\t\n";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(emptyLinesData);

            // Then
            assertFalse(result.isSuccess());
            assertEquals("Invalid input data", result.getMessage());
        }
    }

    @Nested
    @DisplayName("Data Validation Tests")
    class DataValidationTests {

        @Test
        @DisplayName("Should validate correct data format")
        void shouldValidateCorrectDataFormat() {
            // Given
            String validData = "AB123456,Product A,100.50\n" +
                              "CD789012,Product B,200.75";

            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(validData);

            // Then
            assertTrue(result.isValid());
            assertEquals("Valid", result.getMessage());
        }

        @Test
        @DisplayName("Should reject null data")
        void shouldRejectNullData() {
            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(null);

            // Then
            assertFalse(result.isValid());
            assertEquals("Data cannot be null", result.getMessage());
        }

        @Test
        @DisplayName("Should reject empty data")
        void shouldRejectEmptyData() {
            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData("");

            // Then
            assertFalse(result.isValid());
            assertEquals("Data cannot be empty", result.getMessage());
        }

        @Test
        @DisplayName("Should reject data with insufficient fields")
        void shouldRejectDataWithInsufficientFields() {
            // Given
            String invalidData = "AB123456,Product A\n" + // Missing value
                                "CD789012,Product B,200.75";

            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(invalidData);

            // Then
            assertFalse(result.isValid());
            assertTrue(result.getMessage().contains("Insufficient data fields"));
        }

        @Test
        @DisplayName("Should reject data with invalid ID format")
        void shouldRejectDataWithInvalidIdFormat() {
            // Given
            String invalidData = "INVALID,Product A,100.50\n" +
                                "AB123456,Product B,200.75";

            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(invalidData);

            // Then
            assertFalse(result.isValid());
            assertTrue(result.getMessage().contains("Invalid ID format"));
        }

        @Test
        @DisplayName("Should reject data with invalid name length")
        void shouldRejectDataWithInvalidNameLength() {
            // Given
            String invalidData = "AB123456,A,100.50\n" + // Name too short
                                "CD789012,Very Long Product Name That Exceeds Fifty Characters Limit,200.75"; // Name too long

            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(invalidData);

            // Then
            assertFalse(result.isValid());
            assertTrue(result.getMessage().contains("Name length must be between 2 and 50 characters"));
        }

        @Test
        @DisplayName("Should reject data with negative values")
        void shouldRejectDataWithNegativeValues() {
            // Given
            String invalidData = "AB123456,Product A,-100.50\n" +
                                "CD789012,Product B,200.75";

            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(invalidData);

            // Then
            assertFalse(result.isValid());
            assertTrue(result.getMessage().contains("Value cannot be negative"));
        }

        @Test
        @DisplayName("Should reject data with invalid numeric values")
        void shouldRejectDataWithInvalidNumericValues() {
            // Given
            String invalidData = "AB123456,Product A,invalid\n" +
                                "CD789012,Product B,200.75";

            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(invalidData);

            // Then
            assertFalse(result.isValid());
            assertTrue(result.getMessage().contains("Invalid numeric value"));
        }

        @Test
        @DisplayName("Should handle multiple validation errors")
        void shouldHandleMultipleValidationErrors() {
            // Given
            String invalidData = "INVALID,A,-100.50\n" + // Multiple errors
                                "CD789012,Product B,200.75";

            // When
            LegacyDataProcessor.ValidationResult result = processor.validateData(invalidData);

            // Then
            assertFalse(result.isValid());
            String message = result.getMessage();
            assertTrue(message.contains("Invalid ID format"));
            assertTrue(message.contains("Name length must be between 2 and 50 characters"));
            assertTrue(message.contains("Value cannot be negative"));
        }
    }

    @Nested
    @DisplayName("Statistics Generation Tests")
    class StatisticsGenerationTests {

        @Test
        @DisplayName("Should generate correct statistics for valid data")
        void shouldGenerateCorrectStatisticsForValidData() {
            // Given
            String data = "AB123456,Product A,100.00\n" +
                          "CD789012,Product B,200.00\n" +
                          "EF345678,Product C,300.00";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(data);

            // Then
            assertTrue(result.isSuccess());
            Map<String, Object> stats = result.getData();
            
            assertEquals(3, stats.get("count"));
            assertEquals(new BigDecimal("600.00"), stats.get("total"));
            assertEquals(new BigDecimal("200.00"), stats.get("average"));
            assertEquals(new BigDecimal("100.00"), stats.get("min"));
            assertEquals(new BigDecimal("300.00"), stats.get("max"));
            
            @SuppressWarnings("unchecked")
            Map<String, Long> nameFrequency = (Map<String, Long>) stats.get("nameFrequency");
            assertEquals(1L, nameFrequency.get("Product A"));
            assertEquals(1L, nameFrequency.get("Product B"));
            assertEquals(1L, nameFrequency.get("Product C"));
        }

        @Test
        @DisplayName("Should handle statistics for empty data set")
        void shouldHandleStatisticsForEmptyDataSet() {
            // Given
            String data = "AB123456,Product A,100.00\n" +
                          "CD789012,Product B,invalid\n" + // This will cause an error
                          "EF345678,Product C,300.00";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(data);

            // Then
            assertTrue(result.isSuccess());
            Map<String, Object> stats = result.getData();
            
            assertEquals(2, stats.get("count")); // Only valid records
            assertEquals(new BigDecimal("400.00"), stats.get("total"));
            assertEquals(new BigDecimal("200.00"), stats.get("average"));
        }

        @Test
        @DisplayName("Should handle duplicate product names in statistics")
        void shouldHandleDuplicateProductNamesInStatistics() {
            // Given
            String data = "AB123456,Product A,100.00\n" +
                          "CD789012,Product A,200.00\n" +
                          "EF345678,Product B,300.00";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(data);

            // Then
            assertTrue(result.isSuccess());
            Map<String, Object> stats = result.getData();
            
            @SuppressWarnings("unchecked")
            Map<String, Long> nameFrequency = (Map<String, Long>) stats.get("nameFrequency");
            assertEquals(2L, nameFrequency.get("Product A"));
            assertEquals(1L, nameFrequency.get("Product B"));
        }
    }

    @Nested
    @DisplayName("JSON Export Tests")
    class JsonExportTests {

        @Test
        @DisplayName("Should export data to valid JSON format")
        void shouldExportDataToValidJsonFormat() {
            // Given
            String data = "AB123456,Product A,100.00\n" +
                          "CD789012,Product B,200.00";
            processor.processData(data);

            // When
            String json = processor.exportToJson();

            // Then
            assertNotNull(json);
            assertTrue(json.contains("processedCount"));
            assertTrue(json.contains("totalAmount"));
            assertTrue(json.contains("processedItems"));
            assertTrue(json.contains("errorLog"));
            assertTrue(json.contains("cacheSize"));
            assertTrue(json.contains("exportTimestamp"));
        }

        @Test
        @DisplayName("Should export empty state to JSON")
        void shouldExportEmptyStateToJson() {
            // When
            String json = processor.exportToJson();

            // Then
            assertNotNull(json);
            assertTrue(json.contains("\"processedCount\":0"));
            assertTrue(json.contains("\"totalAmount\":0"));
            assertTrue(json.contains("\"processedItems\":[]"));
            assertTrue(json.contains("\"errorLog\":[]"));
        }
    }

    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {

        @Test
        @DisplayName("Should reset processor state correctly")
        void shouldResetProcessorStateCorrectly() {
            // Given
            String data = "AB123456,Product A,100.00\n" +
                          "CD789012,Product B,200.00";
            processor.processData(data);
            
            // Verify state is not empty
            assertTrue(processor.getProcessedCount() > 0);
            assertTrue(processor.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
            assertFalse(processor.getProcessedItems().isEmpty());

            // When
            processor.reset();

            // Then
            assertEquals(0, processor.getProcessedCount());
            assertEquals(BigDecimal.ZERO, processor.getTotalAmount());
            assertTrue(processor.getProcessedItems().isEmpty());
            assertTrue(processor.getErrorLog().isEmpty());
            assertEquals(0, processor.getCacheSize());
            assertFalse(processor.isInitialized());
        }

        @Test
        @DisplayName("Should maintain state across multiple operations")
        void shouldMaintainStateAcrossMultipleOperations() {
            // Given
            String data1 = "AB123456,Product A,100.00";
            String data2 = "CD789012,Product B,200.00";

            // When
            processor.processData(data1);
            processor.processData(data2);

            // Then
            assertEquals(2, processor.getProcessedCount());
            assertEquals(new BigDecimal("300.00"), processor.getTotalAmount());
            assertEquals(2, processor.getProcessedItems().size());
            assertTrue(processor.getProcessedItems().contains("AB123456"));
            assertTrue(processor.getProcessedItems().contains("CD789012"));
        }

        @Test
        @DisplayName("Should track error count correctly")
        void shouldTrackErrorCountCorrectly() {
            // Given
            String invalidData = "INVALID,Product A,100.00\n" +
                                "AB123456,Product B,200.00";

            // When
            processor.processData(invalidData);

            // Then
            assertEquals(1, processor.getErrorCount());
            assertEquals(1, processor.getErrorLog().size());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very large numeric values")
        void shouldHandleVeryLargeNumericValues() {
            // Given
            String largeValueData = "AB123456,Product A,999999999.99\n" +
                                   "CD789012,Product B,0.01";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(largeValueData);

            // Then
            assertTrue(result.isSuccess());
            Map<String, Object> data = result.getData();
            BigDecimal total = (BigDecimal) data.get("total");
            assertTrue(total.compareTo(new BigDecimal("1000000000")) > 0);
        }

        @Test
        @DisplayName("Should handle special characters in product names")
        void shouldHandleSpecialCharactersInProductNames() {
            // Given
            String specialCharData = "AB123456,Product-A,100.00\n" +
                                    "CD789012,Product_B,200.00\n" +
                                    "EF345678,Product.C,300.00";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(specialCharData);

            // Then
            assertTrue(result.isSuccess());
            Map<String, Object> data = result.getData();
            assertEquals(3, data.get("count"));
        }

        @Test
        @DisplayName("Should handle timestamp parsing errors gracefully")
        void shouldHandleTimestampParsingErrorsGracefully() {
            // Given
            String invalidTimestampData = "AB123456,Product A,100.00,invalid-timestamp\n" +
                                         "CD789012,Product B,200.00,2024-01-15 10:30:00";

            // When
            LegacyDataProcessor.ProcessingResult result = processor.processData(invalidTimestampData);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(result.getMessage().contains("Successfully processed 2 records"));
        }

        @Test
        @DisplayName("Should handle concurrent access to processor")
        void shouldHandleConcurrentAccessToProcessor() throws InterruptedException {
            // Given
            String data = "AB123456,Product A,100.00";
            int threadCount = 10;
            Thread[] threads = new Thread[threadCount];

            // When
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> processor.processData(data));
                threads[i].start();
            }

            // Wait for all threads to complete
            for (Thread thread : threads) {
                thread.join();
            }

            // Then
            assertEquals(threadCount, processor.getProcessedCount());
            assertEquals(new BigDecimal("1000.00"), processor.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Data Record Tests")
    class DataRecordTests {

        @Test
        @DisplayName("Should create DataRecord with valid parameters")
        void shouldCreateDataRecordWithValidParameters() {
            // Given
            String id = "AB123456";
            String name = "Test Product";
            BigDecimal value = new BigDecimal("100.50");
            LocalDateTime timestamp = LocalDateTime.now();

            // When
            LegacyDataProcessor.DataRecord record = new LegacyDataProcessor.DataRecord(id, name, value, timestamp);

            // Then
            assertEquals(id, record.getId());
            assertEquals(name, record.getName());
            assertEquals(value, record.getValue());
            assertEquals(timestamp, record.getTimestamp());
        }

        @Test
        @DisplayName("Should handle DataRecord with null timestamp")
        void shouldHandleDataRecordWithNullTimestamp() {
            // Given
            String id = "AB123456";
            String name = "Test Product";
            BigDecimal value = new BigDecimal("100.50");

            // When
            LegacyDataProcessor.DataRecord record = new LegacyDataProcessor.DataRecord(id, name, value, null);

            // Then
            assertEquals(id, record.getId());
            assertEquals(name, record.getName());
            assertEquals(value, record.getValue());
            assertNull(record.getTimestamp());
        }
    }
} 