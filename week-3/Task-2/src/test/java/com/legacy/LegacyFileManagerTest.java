package com.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LegacyFileManager Tests")
class LegacyFileManagerTest {

    private LegacyFileManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = new LegacyFileManager();
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize with valid directory path")
        void shouldInitializeWithValidDirectoryPath(@TempDir Path tempDir) {
            // When
            boolean result = fileManager.initialize(tempDir.toString());

            // Then
            assertTrue(result);
            assertTrue(fileManager.isInitialized());
            assertEquals(tempDir, fileManager.getBaseDirectory());
        }

        @Test
        @DisplayName("Should create directory if it doesn't exist")
        void shouldCreateDirectoryIfItDoesntExist(@TempDir Path tempDir) throws IOException {
            // Given
            Path nonExistentDir = tempDir.resolve("new-directory");

            // When
            boolean result = fileManager.initialize(nonExistentDir.toString());

            // Then
            assertTrue(result);
            assertTrue(Files.exists(nonExistentDir));
            assertTrue(Files.isDirectory(nonExistentDir));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should fail initialization with null or empty path")
        void shouldFailInitializationWithNullOrEmptyPath(String path) {
            // When
            boolean result = fileManager.initialize(path);

            // Then
            assertFalse(result);
            assertFalse(fileManager.isInitialized());
        }

        @Test
        @DisplayName("Should fail initialization when path is not a directory")
        void shouldFailInitializationWhenPathIsNotADirectory(@TempDir Path tempDir) throws IOException {
            // Given
            Path filePath = tempDir.resolve("test-file.txt");
            Files.createFile(filePath);

            // When
            boolean result = fileManager.initialize(filePath.toString());

            // Then
            assertFalse(result);
            assertFalse(fileManager.isInitialized());
        }

        @Test
        @DisplayName("Should fail initialization when directory is not readable")
        void shouldFailInitializationWhenDirectoryIsNotReadable(@TempDir Path tempDir) {
            // Given
            Path nonReadableDir = tempDir.resolve("non-readable");
            try {
                Files.createDirectory(nonReadableDir);
                nonReadableDir.toFile().setReadable(false);
            } catch (IOException e) {
                // Skip test if we can't set permissions
                return;
            }

            // When
            boolean result = fileManager.initialize(nonReadableDir.toString());

            // Then
            assertFalse(result);
            assertFalse(fileManager.isInitialized());
        }
    }

    @Nested
    @DisplayName("Directory Processing Tests")
    class DirectoryProcessingTests {

        @Test
        @DisplayName("Should process directory with valid files")
        void shouldProcessDirectoryWithValidFiles(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            // Create test files
            Path file1 = tempDir.resolve("test1.txt");
            Path file2 = tempDir.resolve("test2.log");
            Files.write(file1, "content1".getBytes());
            Files.write(file2, "content2".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setValidateEnabled(true);

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(2, result.getProcessedCount());
            assertTrue(result.getTotalSize() > 0);
        }

        @Test
        @DisplayName("Should handle empty directory")
        void shouldHandleEmptyDirectory(@TempDir Path tempDir) {
            // Given
            fileManager.initialize(tempDir.toString());
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(0, result.getProcessedCount());
            assertEquals(0, result.getTotalSize());
        }

        @Test
        @DisplayName("Should fail processing when directory doesn't exist")
        void shouldFailProcessingWhenDirectoryDoesntExist(@TempDir Path tempDir) {
            // Given
            fileManager.initialize(tempDir.toString());
            Path nonExistentDir = tempDir.resolve("non-existent");
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(nonExistentDir.toString(), options);

            // Then
            assertFalse(result.isSuccess());
            assertTrue(result.getMessage().contains("Directory does not exist"));
        }

        @Test
        @DisplayName("Should fail processing when file manager not initialized")
        void shouldFailProcessingWhenFileManagerNotInitialized(@TempDir Path tempDir) {
            // Given
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertFalse(result.isSuccess());
            assertTrue(result.getMessage().contains("File manager not initialized"));
        }

        @Test
        @DisplayName("Should filter files by extension")
        void shouldFilterFilesByExtension(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            // Create test files
            Path txtFile = tempDir.resolve("test.txt");
            Path logFile = tempDir.resolve("test.log");
            Path csvFile = tempDir.resolve("test.csv");
            Files.write(txtFile, "content".getBytes());
            Files.write(logFile, "content".getBytes());
            Files.write(csvFile, "content".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setIncludeExtensions(Arrays.asList(".txt", ".log"));

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(2, result.getProcessedCount()); // Only .txt and .log files
        }

        @Test
        @DisplayName("Should filter files by size limits")
        void shouldFilterFilesBySizeLimits(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            // Create test files with different sizes
            Path smallFile = tempDir.resolve("small.txt");
            Path largeFile = tempDir.resolve("large.txt");
            Files.write(smallFile, "small".getBytes());
            Files.write(largeFile, "large content that exceeds the limit".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setMinFileSize(10); // Minimum 10 bytes
            options.setMaxFileSize(20); // Maximum 20 bytes

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount()); // Only the large file
        }

        @Test
        @DisplayName("Should filter files by filename pattern")
        void shouldFilterFilesByFilenamePattern(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            // Create test files
            Path testFile = tempDir.resolve("test.txt");
            Path otherFile = tempDir.resolve("other.txt");
            Files.write(testFile, "content".getBytes());
            Files.write(otherFile, "content".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setFilenamePattern("test.*");

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount()); // Only test.txt
        }
    }

    @Nested
    @DisplayName("File Processing Options Tests")
    class FileProcessingOptionsTests {

        @Test
        @DisplayName("Should process files with backup enabled")
        void shouldProcessFilesWithBackupEnabled(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setBackupEnabled(true);

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount());
            
            // Check if backup was created
            boolean backupExists = Files.list(tempDir)
                .anyMatch(path -> path.getFileName().toString().startsWith("test.txt.backup."));
            assertTrue(backupExists);
        }

        @Test
        @DisplayName("Should process files with compression enabled")
        void shouldProcessFilesWithCompressionEnabled(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setCompressEnabled(true);

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount());
            
            // Check if compressed file was created
            Path compressedFile = tempDir.resolve("test.txt.zip");
            assertTrue(Files.exists(compressedFile));
        }

        @Test
        @DisplayName("Should process files with validation enabled")
        void shouldProcessFilesWithValidationEnabled(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setValidateEnabled(true);

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount());
        }

        @Test
        @DisplayName("Should process files with archiving enabled")
        void shouldProcessFilesWithArchivingEnabled(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());

            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setArchiveEnabled(true);

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount());
            
            // Check if archive directory was created
            Path archiveDir = tempDir.resolve("archive");
            assertTrue(Files.exists(archiveDir));
        }
    }

    @Nested
    @DisplayName("File Search Tests")
    class FileSearchTests {

        @Test
        @DisplayName("Should search files by name pattern")
        void shouldSearchFilesByNamePattern(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            // Create test files and process them
            Path testFile = tempDir.resolve("test.txt");
            Path otherFile = tempDir.resolve("other.txt");
            Files.write(testFile, "content".getBytes());
            Files.write(otherFile, "content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            fileManager.processDirectory(tempDir.toString(), options);

            LegacyFileManager.FileSearchCriteria criteria = new LegacyFileManager.FileSearchCriteria();
            criteria.setNamePattern("test.*");

            // When
            List<LegacyFileManager.FileInfo> results = fileManager.searchFiles(criteria);

            // Then
            assertEquals(1, results.size());
            assertEquals("test.txt", results.get(0).getName());
        }

        @Test
        @DisplayName("Should search files by size range")
        void shouldSearchFilesBySizeRange(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            // Create test files with different sizes
            Path smallFile = tempDir.resolve("small.txt");
            Path largeFile = tempDir.resolve("large.txt");
            Files.write(smallFile, "small".getBytes());
            Files.write(largeFile, "large content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            fileManager.processDirectory(tempDir.toString(), options);

            LegacyFileManager.FileSearchCriteria criteria = new LegacyFileManager.FileSearchCriteria();
            criteria.setMinSize(10);
            criteria.setMaxSize(20);

            // When
            List<LegacyFileManager.FileInfo> results = fileManager.searchFiles(criteria);

            // Then
            assertEquals(1, results.size());
            assertTrue(results.get(0).getSize() >= 10 && results.get(0).getSize() <= 20);
        }

        @Test
        @DisplayName("Should search files by processing type")
        void shouldSearchFilesByProcessingType(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setValidateEnabled(true);
            fileManager.processDirectory(tempDir.toString(), options);

            LegacyFileManager.FileSearchCriteria criteria = new LegacyFileManager.FileSearchCriteria();
            criteria.setProcessingType("VALIDATE");

            // When
            List<LegacyFileManager.FileInfo> results = fileManager.searchFiles(criteria);

            // Then
            assertEquals(1, results.size());
            assertEquals("VALIDATE", results.get(0).getProcessingType());
        }

        @Test
        @DisplayName("Should return empty results for no matching criteria")
        void shouldReturnEmptyResultsForNoMatchingCriteria(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            fileManager.processDirectory(tempDir.toString(), options);

            LegacyFileManager.FileSearchCriteria criteria = new LegacyFileManager.FileSearchCriteria();
            criteria.setNamePattern("nonexistent.*");

            // When
            List<LegacyFileManager.FileInfo> results = fileManager.searchFiles(criteria);

            // Then
            assertTrue(results.isEmpty());
        }
    }

    @Nested
    @DisplayName("Report Generation Tests")
    class ReportGenerationTests {

        @Test
        @DisplayName("Should generate report with processing information")
        void shouldGenerateReportWithProcessingInformation(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            fileManager.processDirectory(tempDir.toString(), options);

            // When
            String report = fileManager.generateReport();

            // Then
            assertNotNull(report);
            assertTrue(report.contains("File Manager Report"));
            assertTrue(report.contains("Total Files Processed: 1"));
            assertTrue(report.contains("Registry Size: 1"));
            assertTrue(report.contains("Recent Operations:"));
        }

        @Test
        @DisplayName("Should generate report for empty state")
        void shouldGenerateReportForEmptyState(@TempDir Path tempDir) {
            // Given
            fileManager.initialize(tempDir.toString());

            // When
            String report = fileManager.generateReport();

            // Then
            assertNotNull(report);
            assertTrue(report.contains("Total Files Processed: 0"));
            assertTrue(report.contains("Registry Size: 0"));
        }
    }

    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {

        @Test
        @DisplayName("Should reset file manager state correctly")
        void shouldResetFileManagerStateCorrectly(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            fileManager.processDirectory(tempDir.toString(), options);
            
            // Verify state is not empty
            assertTrue(fileManager.getFilesProcessed() > 0);
            assertTrue(fileManager.getTotalSizeProcessed() > 0);
            assertTrue(fileManager.getRegistrySize() > 0);

            // When
            fileManager.reset();

            // Then
            assertEquals(0, fileManager.getFilesProcessed());
            assertEquals(0, fileManager.getTotalSizeProcessed());
            assertEquals(0, fileManager.getRegistrySize());
            assertEquals(0, fileManager.getProcessedFilesCount());
            assertTrue(fileManager.getOperationLog().isEmpty());
            assertFalse(fileManager.isInitialized());
        }

        @Test
        @DisplayName("Should maintain state across multiple operations")
        void shouldMaintainStateAcrossMultipleOperations(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path file1 = tempDir.resolve("file1.txt");
            Path file2 = tempDir.resolve("file2.txt");
            Files.write(file1, "content1".getBytes());
            Files.write(file2, "content2".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();

            // When
            fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertEquals(2, fileManager.getFilesProcessed());
            assertTrue(fileManager.getTotalSizeProcessed() > 0);
            assertEquals(2, fileManager.getRegistrySize());
            assertEquals(2, fileManager.getProcessedFilesCount());
        }
    }

    @Nested
    @DisplayName("File Info Tests")
    class FileInfoTests {

        @Test
        @DisplayName("Should create FileInfo with valid parameters")
        void shouldCreateFileInfoWithValidParameters() {
            // Given
            String name = "test.txt";
            String path = "/path/to/test.txt";
            long size = 1024L;
            LocalDateTime processedDate = LocalDateTime.now();
            String processingType = "VALIDATE";

            // When
            LegacyFileManager.FileInfo fileInfo = new LegacyFileManager.FileInfo(name, path, size, processedDate, processingType);

            // Then
            assertEquals(name, fileInfo.getName());
            assertEquals(path, fileInfo.getPath());
            assertEquals(size, fileInfo.getSize());
            assertEquals(processedDate, fileInfo.getProcessedDate());
            assertEquals(processingType, fileInfo.getProcessingType());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle files with special characters in names")
        void shouldHandleFilesWithSpecialCharactersInNames(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path specialFile = tempDir.resolve("test-file_with.special_chars.txt");
            Files.write(specialFile, "content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            options.setValidateEnabled(true);

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount());
        }

        @Test
        @DisplayName("Should handle very large files")
        void shouldHandleVeryLargeFiles(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path largeFile = tempDir.resolve("large.txt");
            byte[] largeContent = new byte[1024 * 1024]; // 1MB
            Arrays.fill(largeContent, (byte) 'A');
            Files.write(largeFile, largeContent);
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount());
            assertTrue(result.getTotalSize() >= 1024 * 1024);
        }

        @Test
        @DisplayName("Should handle nested directories")
        void shouldHandleNestedDirectories(@TempDir Path tempDir) throws IOException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            Path nestedDir = tempDir.resolve("nested");
            Files.createDirectory(nestedDir);
            Path nestedFile = nestedDir.resolve("test.txt");
            Files.write(nestedFile, "content".getBytes());
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();

            // When
            LegacyFileManager.ProcessingSummary result = fileManager.processDirectory(tempDir.toString(), options);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(1, result.getProcessedCount());
        }

        @Test
        @DisplayName("Should handle concurrent file processing")
        void shouldHandleConcurrentFileProcessing(@TempDir Path tempDir) throws IOException, InterruptedException {
            // Given
            fileManager.initialize(tempDir.toString());
            
            // Create multiple files
            for (int i = 0; i < 10; i++) {
                Path file = tempDir.resolve("file" + i + ".txt");
                Files.write(file, ("content" + i).getBytes());
            }
            
            LegacyFileManager.FileProcessingOptions options = new LegacyFileManager.FileProcessingOptions();
            
            // Create multiple threads to process the same directory
            int threadCount = 5;
            Thread[] threads = new Thread[threadCount];
            
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> fileManager.processDirectory(tempDir.toString(), options));
                threads[i].start();
            }
            
            // Wait for all threads to complete
            for (Thread thread : threads) {
                thread.join();
            }

            // Then
            assertEquals(10, fileManager.getRegistrySize()); // Should have processed all files
        }
    }
} 