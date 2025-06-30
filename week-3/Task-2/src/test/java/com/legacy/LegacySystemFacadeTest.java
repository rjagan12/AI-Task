package com.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LegacySystemFacade Tests")
class LegacySystemFacadeTest {

    private LegacySystemFacade facade;

    @BeforeEach
    void setUp() {
        facade = new LegacySystemFacade();
    }

    @Nested
    @DisplayName("System Initialization Tests")
    class SystemInitializationTests {

        @Test
        @DisplayName("Should initialize system with valid configuration")
        void shouldInitializeSystemWithValidConfiguration(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);

            // When
            LegacySystemFacade.SystemInitializationResult result = facade.initializeSystem(config);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(facade.isSystemReady());
            assertEquals("System initialized successfully", result.getMessage());
        }

        @Test
        @DisplayName("Should fail initialization with null configuration")
        void shouldFailInitializationWithNullConfiguration() {
            // When
            LegacySystemFacade.SystemInitializationResult result = facade.initializeSystem(null);

            // Then
            assertFalse(result.isSuccess());
            assertFalse(facade.isSystemReady());
            assertTrue(result.getMessage().contains("Failed to initialize"));
        }

        @Test
        @DisplayName("Should fail initialization with empty configuration")
        void shouldFailInitializationWithEmptyConfiguration() {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();

            // When
            LegacySystemFacade.SystemInitializationResult result = facade.initializeSystem(config);

            // Then
            assertFalse(result.isSuccess());
            assertFalse(facade.isSystemReady());
            assertTrue(result.getMessage().contains("Failed to initialize"));
        }

        @Test
        @DisplayName("Should fail initialization with invalid base directory")
        void shouldFailInitializationWithInvalidBaseDirectory() {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory("/invalid/path/that/does/not/exist");
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);

            // When
            LegacySystemFacade.SystemInitializationResult result = facade.initializeSystem(config);

            // Then
            assertFalse(result.isSuccess());
            assertFalse(facade.isSystemReady());
        }
    }

    @Nested
    @DisplayName("Coordinated Processing Tests")
    class CoordinatedProcessingTests {

        @Test
        @DisplayName("Should process data and files successfully")
        void shouldProcessDataAndFilesSuccessfully(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String data = "AB123456,Product A,100.00\n" +
                          "CD789012,Product B,200.00";

            // Create test files
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();
            options.setValidateFiles(true);

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(data, tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(result.getMessage().contains("Processing completed successfully"));
            assertNotNull(result.getData());
            
            Map<String, Object> dataMap = result.getData();
            assertNotNull(dataMap.get("dataProcessing"));
            assertNotNull(dataMap.get("fileProcessing"));
            assertNotNull(dataMap.get("systemMetrics"));
        }

        @Test
        @DisplayName("Should fail processing when system not initialized")
        void shouldFailProcessingWhenSystemNotInitialized(@TempDir Path tempDir) {
            // Given
            String data = "AB123456,Product A,100.00";
            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(data, tempDir.toString(), options);

            // Then
            assertFalse(result.isSuccess());
            assertTrue(result.getMessage().contains("System not initialized"));
        }

        @Test
        @DisplayName("Should handle data processing failure")
        void shouldHandleDataProcessingFailure(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String invalidData = "INVALID,Product A,100.00"; // Invalid ID format
            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(invalidData, tempDir.toString(), options);

            // Then
            assertFalse(result.isSuccess());
            assertTrue(result.getMessage().contains("Data processing failed"));
        }

        @Test
        @DisplayName("Should handle file processing failure")
        void shouldHandleFileProcessingFailure(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String data = "AB123456,Product A,100.00";
            String nonExistentDir = tempDir.resolve("non-existent").toString();
            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(data, nonExistentDir, options);

            // Then
            assertFalse(result.isSuccess());
            assertTrue(result.getMessage().contains("File processing failed"));
        }

        @Test
        @DisplayName("Should process with backup enabled")
        void shouldProcessWithBackupEnabled(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String data = "AB123456,Product A,100.00";
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();
            options.setBackupFiles(true);

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(data, tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            
            // Check if backup was created
            boolean backupExists = Files.list(tempDir)
                .anyMatch(path -> path.getFileName().toString().startsWith("test.txt.backup."));
            assertTrue(backupExists);
        }

        @Test
        @DisplayName("Should process with compression enabled")
        void shouldProcessWithCompressionEnabled(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String data = "AB123456,Product A,100.00";
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();
            options.setCompressFiles(true);

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(data, tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            
            // Check if compressed file was created
            Path compressedFile = tempDir.resolve("test.txt.zip");
            assertTrue(Files.exists(compressedFile));
        }
    }

    @Nested
    @DisplayName("System Validation Tests")
    class SystemValidationTests {

        @Test
        @DisplayName("Should validate properly initialized system")
        void shouldValidateProperlyInitializedSystem(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            // When
            LegacySystemFacade.ValidationResult result = facade.validateSystem();

            // Then
            assertTrue(result.isValid());
            assertTrue(result.getMessage().contains("System validation passed"));
            assertTrue(result.getErrors().isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when system not initialized")
        void shouldFailValidationWhenSystemNotInitialized() {
            // When
            LegacySystemFacade.ValidationResult result = facade.validateSystem();

            // Then
            assertFalse(result.isValid());
            assertTrue(result.getMessage().contains("System validation failed"));
            assertFalse(result.getErrors().isEmpty());
        }

        @Test
        @DisplayName("Should include warnings in validation result")
        void shouldIncludeWarningsInValidationResult(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            // When
            LegacySystemFacade.ValidationResult result = facade.validateSystem();

            // Then
            assertTrue(result.isValid());
            assertFalse(result.getWarnings().isEmpty());
            assertTrue(result.getWarnings().stream()
                .anyMatch(warning -> warning.contains("No operations have been performed")));
        }
    }

    @Nested
    @DisplayName("Maintenance Tests")
    class MaintenanceTests {

        @Test
        @DisplayName("Should perform maintenance successfully")
        void shouldPerformMaintenanceSuccessfully(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            LegacySystemFacade.MaintenanceOptions options = new LegacySystemFacade.MaintenanceOptions();
            options.setGenerateReports(true);

            // When
            LegacySystemFacade.MaintenanceResult result = facade.performMaintenance(options);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(result.getMessage().contains("Maintenance completed successfully"));
        }

        @Test
        @DisplayName("Should fail maintenance when system not initialized")
        void shouldFailMaintenanceWhenSystemNotInitialized() {
            // Given
            LegacySystemFacade.MaintenanceOptions options = new LegacySystemFacade.MaintenanceOptions();

            // When
            LegacySystemFacade.MaintenanceResult result = facade.performMaintenance(options);

            // Then
            assertFalse(result.isSuccess());
            assertTrue(result.getMessage().contains("System not initialized"));
        }

        @Test
        @DisplayName("Should clear caches when requested")
        void shouldClearCachesWhenRequested(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            // Process some data to populate caches
            String data = "AB123456,Product A,100.00";
            LegacySystemFacade.ProcessingOptions processingOptions = new LegacySystemFacade.ProcessingOptions();
            facade.processDataAndFiles(data, tempDir.toString(), processingOptions);

            LegacySystemFacade.MaintenanceOptions options = new LegacySystemFacade.MaintenanceOptions();
            options.setClearCaches(true);

            // When
            LegacySystemFacade.MaintenanceResult result = facade.performMaintenance(options);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(result.getMessage().contains("Data processor cache cleared"));
        }
    }

    @Nested
    @DisplayName("System Metrics Tests")
    class SystemMetricsTests {

        @Test
        @DisplayName("Should get system metrics for initialized system")
        void shouldGetSystemMetricsForInitializedSystem(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            // When
            LegacySystemFacade.SystemMetrics metrics = facade.getSystemMetrics();

            // Then
            assertTrue(metrics.isSystemReady());
            assertNotNull(metrics.getDataProcessorMetrics());
            assertNotNull(metrics.getFileManagerMetrics());
            assertNotNull(metrics.getSystemStateMetrics());
            assertNotNull(metrics.getLastOperationTime());
            assertTrue(metrics.getEventLogSize() > 0);
        }

        @Test
        @DisplayName("Should get system metrics for uninitialized system")
        void shouldGetSystemMetricsForUninitializedSystem() {
            // When
            LegacySystemFacade.SystemMetrics metrics = facade.getSystemMetrics();

            // Then
            assertFalse(metrics.isSystemReady());
            assertNotNull(metrics.getErrorMessage());
            assertTrue(metrics.getErrorMessage().contains("System not initialized"));
        }

        @Test
        @DisplayName("Should update metrics after operations")
        void shouldUpdateMetricsAfterOperations(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            // Get initial metrics
            LegacySystemFacade.SystemMetrics initialMetrics = facade.getSystemMetrics();
            int initialEventLogSize = initialMetrics.getEventLogSize();

            // Perform an operation
            String data = "AB123456,Product A,100.00";
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());
            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();
            facade.processDataAndFiles(data, tempDir.toString(), options);

            // Get updated metrics
            LegacySystemFacade.SystemMetrics updatedMetrics = facade.getSystemMetrics();

            // Then
            assertTrue(updatedMetrics.getEventLogSize() > initialEventLogSize);
            assertNotNull(updatedMetrics.getLastOperationTime());
        }
    }

    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {

        @Test
        @DisplayName("Should handle different configuration settings")
        void shouldHandleDifferentConfigurationSettings(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(500);
            config.setEnableLogging(false);
            config.setEnableBackup(true);
            config.setEnableCompression(true);

            // When
            LegacySystemFacade.SystemInitializationResult result = facade.initializeSystem(config);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(facade.isSystemReady());
        }

        @Test
        @DisplayName("Should handle processing options configuration")
        void shouldHandleProcessingOptionsConfiguration(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String data = "AB123456,Product A,100.00";
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();
            options.setBackupFiles(true);
            options.setCompressFiles(true);
            options.setValidateFiles(true);
            options.setArchiveFiles(false);
            options.setFileExtensions(Arrays.asList(".txt", ".log"));
            options.setMaxFileSize(1024);
            options.setMinFileSize(1);

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(data, tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
        }
    }

    @Nested
    @DisplayName("Shutdown Tests")
    class ShutdownTests {

        @Test
        @DisplayName("Should shutdown system gracefully")
        void shouldShutdownSystemGracefully(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            // Verify system is ready
            assertTrue(facade.isSystemReady());

            // When
            facade.shutdown();

            // Then
            assertFalse(facade.isSystemReady());
            assertEquals(0, facade.getEventLogSize());
            assertEquals(0, facade.getSystemStateSize());
        }

        @Test
        @DisplayName("Should handle shutdown of uninitialized system")
        void shouldHandleShutdownOfUninitializedSystem() {
            // Given
            assertFalse(facade.isSystemReady());

            // When
            facade.shutdown();

            // Then
            assertFalse(facade.isSystemReady());
            assertEquals(0, facade.getEventLogSize());
        }
    }

    @Nested
    @DisplayName("Event Logging Tests")
    class EventLoggingTests {

        @Test
        @DisplayName("Should log system events")
        void shouldLogSystemEvents(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);

            // When
            facade.initializeSystem(config);

            // Then
            assertTrue(facade.getEventLogSize() > 0);
            List<LegacySystemFacade.SystemEvent> events = facade.getEventLog();
            assertTrue(events.stream().anyMatch(event -> event.getEventType().equals("SYSTEM_INITIALIZED")));
        }

        @Test
        @DisplayName("Should log processing events")
        void shouldLogProcessingEvents(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String data = "AB123456,Product A,100.00";
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());
            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();

            // When
            facade.processDataAndFiles(data, tempDir.toString(), options);

            // Then
            List<LegacySystemFacade.SystemEvent> events = facade.getEventLog();
            assertTrue(events.stream().anyMatch(event -> event.getEventType().equals("PROCESSING_COMPLETED")));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle initialization errors gracefully")
        void shouldHandleInitializationErrorsGracefully() {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory("/invalid/path/that/will/fail");
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);

            // When
            LegacySystemFacade.SystemInitializationResult result = facade.initializeSystem(config);

            // Then
            assertFalse(result.isSuccess());
            assertFalse(facade.isSystemReady());
        }

        @Test
        @DisplayName("Should handle processing errors gracefully")
        void shouldHandleProcessingErrorsGracefully(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            String invalidData = "INVALID,Product A,100.00";
            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();

            // When
            LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(invalidData, tempDir.toString(), options);

            // Then
            assertFalse(result.isSuccess());
            assertTrue(result.getMessage().contains("Data processing failed"));
        }

        @Test
        @DisplayName("Should handle maintenance errors gracefully")
        void shouldHandleMaintenanceErrorsGracefully(@TempDir Path tempDir) {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            LegacySystemFacade.MaintenanceOptions options = new LegacySystemFacade.MaintenanceOptions();
            options.setClearCaches(true);
            options.setGenerateReports(true);

            // When
            LegacySystemFacade.MaintenanceResult result = facade.performMaintenance(options);

            // Then
            assertTrue(result.isSuccess());
            assertTrue(result.getMessage().contains("Maintenance completed successfully"));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should perform complete workflow successfully")
        void shouldPerformCompleteWorkflowSuccessfully(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);

            // Step 1: Initialize system
            LegacySystemFacade.SystemInitializationResult initResult = facade.initializeSystem(config);
            assertTrue(initResult.isSuccess());

            // Step 2: Validate system
            LegacySystemFacade.ValidationResult validationResult = facade.validateSystem();
            assertTrue(validationResult.isValid());

            // Step 3: Process data and files
            String data = "AB123456,Product A,100.00\n" +
                          "CD789012,Product B,200.00";
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());
            
            LegacySystemFacade.ProcessingOptions processingOptions = new LegacySystemFacade.ProcessingOptions();
            processingOptions.setValidateFiles(true);
            
            LegacySystemFacade.ProcessingResult processingResult = facade.processDataAndFiles(data, tempDir.toString(), processingOptions);
            assertTrue(processingResult.isSuccess());

            // Step 4: Get system metrics
            LegacySystemFacade.SystemMetrics metrics = facade.getSystemMetrics();
            assertTrue(metrics.isSystemReady());
            assertTrue(metrics.getEventLogSize() > 0);

            // Step 5: Perform maintenance
            LegacySystemFacade.MaintenanceOptions maintenanceOptions = new LegacySystemFacade.MaintenanceOptions();
            maintenanceOptions.setGenerateReports(true);
            
            LegacySystemFacade.MaintenanceResult maintenanceResult = facade.performMaintenance(maintenanceOptions);
            assertTrue(maintenanceResult.isSuccess());

            // Step 6: Shutdown system
            facade.shutdown();
            assertFalse(facade.isSystemReady());
        }

        @Test
        @DisplayName("Should handle multiple processing operations")
        void shouldHandleMultipleProcessingOperations(@TempDir Path tempDir) throws IOException {
            // Given
            LegacySystemFacade.SystemConfiguration config = new LegacySystemFacade.SystemConfiguration();
            config.setBaseDirectory(tempDir.toString());
            config.setMaxCacheSize(1000);
            config.setEnableLogging(true);
            facade.initializeSystem(config);

            // Create multiple test files
            for (int i = 0; i < 5; i++) {
                Path testFile = tempDir.resolve("test" + i + ".txt");
                Files.write(testFile, ("content" + i).getBytes());
            }

            String data = "AB123456,Product A,100.00";
            LegacySystemFacade.ProcessingOptions options = new LegacySystemFacade.ProcessingOptions();
            options.setValidateFiles(true);

            // When - Perform multiple processing operations
            for (int i = 0; i < 3; i++) {
                LegacySystemFacade.ProcessingResult result = facade.processDataAndFiles(data, tempDir.toString(), options);
                assertTrue(result.isSuccess());
            }

            // Then
            LegacySystemFacade.SystemMetrics metrics = facade.getSystemMetrics();
            assertTrue(metrics.isSystemReady());
            assertTrue(metrics.getEventLogSize() > 0);
        }
    }
} 