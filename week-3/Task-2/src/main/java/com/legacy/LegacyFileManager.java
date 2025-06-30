package com.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Legacy File Manager - Handles complex file operations, archiving, and data management.
 * This class has no existing tests and needs comprehensive test coverage.
 */
public class LegacyFileManager {
    private static final Logger logger = LoggerFactory.getLogger(LegacyFileManager.class);
    private static final DateTimeFormatter BACKUP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Pattern VALID_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");
    
    private final Map<String, FileInfo> fileRegistry = new ConcurrentHashMap<>();
    private final Set<String> processedFiles = new HashSet<>();
    private final List<String> operationLog = new ArrayList<>();
    
    private Path baseDirectory;
    private long totalSizeProcessed = 0;
    private int filesProcessed = 0;
    private boolean isInitialized = false;
    
    /**
     * Initializes the file manager with a base directory
     */
    public boolean initialize(String baseDirPath) {
        try {
            if (baseDirPath == null || baseDirPath.trim().isEmpty()) {
                logger.error("Base directory path cannot be null or empty");
                return false;
            }
            
            baseDirectory = Paths.get(baseDirPath);
            if (!Files.exists(baseDirectory)) {
                Files.createDirectories(baseDirectory);
                logger.info("Created base directory: " + baseDirectory);
            }
            
            if (!Files.isDirectory(baseDirectory)) {
                logger.error("Base directory path is not a directory: " + baseDirectory);
                return false;
            }
            
            if (!Files.isReadable(baseDirectory) || !Files.isWritable(baseDirectory)) {
                logger.error("Base directory is not readable/writable: " + baseDirectory);
                return false;
            }
            
            isInitialized = true;
            logger.info("File manager initialized with base directory: " + baseDirectory);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to initialize file manager", e);
            return false;
        }
    }
    
    /**
     * Processes files in the specified directory with various operations
     */
    public ProcessingSummary processDirectory(String directoryPath, FileProcessingOptions options) {
        if (!isInitialized) {
            return new ProcessingSummary(false, "File manager not initialized", 0, 0);
        }
        
        try {
            Path dirPath = Paths.get(directoryPath);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                return new ProcessingSummary(false, "Directory does not exist: " + directoryPath, 0, 0);
            }
            
            List<Path> files = new ArrayList<>();
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (shouldProcessFile(file, options)) {
                        files.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.warn("Failed to visit file: " + file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
            
            int processedCount = 0;
            long totalSize = 0;
            
            for (Path file : files) {
                try {
                    FileProcessingResult result = processFile(file, options);
                    if (result.isSuccess()) {
                        processedCount++;
                        totalSize += result.getFileSize();
                        processedFiles.add(file.toString());
                        
                        // Register file info
                        FileInfo fileInfo = new FileInfo(
                            file.getFileName().toString(),
                            file.toString(),
                            result.getFileSize(),
                            LocalDateTime.now(),
                            result.getProcessingType()
                        );
                        fileRegistry.put(file.toString(), fileInfo);
                    }
                    
                    operationLog.add("Processed: " + file + " - " + result.getMessage());
                    
                } catch (Exception e) {
                    logger.error("Error processing file: " + file, e);
                    operationLog.add("Error: " + file + " - " + e.getMessage());
                }
            }
            
            filesProcessed += processedCount;
            totalSizeProcessed += totalSize;
            
            return new ProcessingSummary(true, 
                "Successfully processed " + processedCount + " files", 
                processedCount, totalSize);
                
        } catch (Exception e) {
            logger.error("Error processing directory: " + directoryPath, e);
            return new ProcessingSummary(false, "Directory processing failed: " + e.getMessage(), 0, 0);
        }
    }
    
    /**
     * Processes a single file based on the provided options
     */
    private FileProcessingResult processFile(Path file, FileProcessingOptions options) {
        try {
            if (!Files.exists(file)) {
                return new FileProcessingResult(false, "File does not exist", 0, "NONE");
            }
            
            long fileSize = Files.size(file);
            String processingType = "NONE";
            
            // Apply file processing based on options
            if (options.isBackupEnabled()) {
                createBackup(file);
                processingType = "BACKUP";
            }
            
            if (options.isCompressEnabled() && shouldCompress(file)) {
                compressFile(file);
                processingType = "COMPRESS";
            }
            
            if (options.isValidateEnabled()) {
                boolean isValid = validateFile(file);
                if (!isValid) {
                    return new FileProcessingResult(false, "File validation failed", fileSize, "VALIDATION_FAILED");
                }
                processingType = "VALIDATE";
            }
            
            if (options.isArchiveEnabled() && shouldArchive(file)) {
                archiveFile(file);
                processingType = "ARCHIVE";
            }
            
            return new FileProcessingResult(true, "File processed successfully", fileSize, processingType);
            
        } catch (Exception e) {
            logger.error("Error processing file: " + file, e);
            return new FileProcessingResult(false, "Processing failed: " + e.getMessage(), 0, "ERROR");
        }
    }
    
    /**
     * Determines if a file should be processed based on options
     */
    private boolean shouldProcessFile(Path file, FileProcessingOptions options) {
        String fileName = file.getFileName().toString();
        
        // Check file extension filters
        if (options.getIncludeExtensions() != null && !options.getIncludeExtensions().isEmpty()) {
            boolean hasValidExtension = options.getIncludeExtensions().stream()
                .anyMatch(ext -> fileName.toLowerCase().endsWith(ext.toLowerCase()));
            if (!hasValidExtension) {
                return false;
            }
        }
        
        // Check file size limits
        try {
            long fileSize = Files.size(file);
            if (options.getMaxFileSize() > 0 && fileSize > options.getMaxFileSize()) {
                return false;
            }
            if (options.getMinFileSize() > 0 && fileSize < options.getMinFileSize()) {
                return false;
            }
        } catch (IOException e) {
            logger.warn("Could not determine file size for: " + file, e);
            return false;
        }
        
        // Check filename pattern
        if (options.getFilenamePattern() != null && !options.getFilenamePattern().isEmpty()) {
            if (!fileName.matches(options.getFilenamePattern())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Creates a backup of the specified file
     */
    private void createBackup(Path file) throws IOException {
        String timestamp = LocalDateTime.now().format(BACKUP_FORMATTER);
        String backupName = file.getFileName() + ".backup." + timestamp;
        Path backupPath = baseDirectory.resolve(backupName);
        
        Files.copy(file, backupPath, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Created backup: " + backupPath);
    }
    
    /**
     * Compresses a file using ZIP format
     */
    private void compressFile(Path file) throws IOException {
        Path zipPath = Paths.get(file.toString() + ".zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            ZipEntry entry = new ZipEntry(file.getFileName().toString());
            zos.putNextEntry(entry);
            
            Files.copy(file, zos);
            zos.closeEntry();
        }
        
        logger.info("Compressed file: " + zipPath);
    }
    
    /**
     * Validates file content and structure
     */
    private boolean validateFile(Path file) {
        try {
            String fileName = file.getFileName().toString();
            
            // Check filename validity
            if (!VALID_FILENAME_PATTERN.matcher(fileName).matches()) {
                logger.warn("Invalid filename pattern: " + fileName);
                return false;
            }
            
            // Check file size
            long fileSize = Files.size(file);
            if (fileSize == 0) {
                logger.warn("Empty file: " + fileName);
                return false;
            }
            
            // Check if file is readable
            if (!Files.isReadable(file)) {
                logger.warn("File not readable: " + fileName);
                return false;
            }
            
            return true;
            
        } catch (IOException e) {
            logger.error("Error validating file: " + file, e);
            return false;
        }
    }
    
    /**
     * Archives files that meet certain criteria
     */
    private void archiveFile(Path file) throws IOException {
        Path archiveDir = baseDirectory.resolve("archive");
        if (!Files.exists(archiveDir)) {
            Files.createDirectories(archiveDir);
        }
        
        Path archivePath = archiveDir.resolve(file.getFileName());
        Files.move(file, archivePath, StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("Archived file: " + archivePath);
    }
    
    /**
     * Determines if a file should be compressed
     */
    private boolean shouldCompress(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".txt") || fileName.endsWith(".log") || fileName.endsWith(".csv");
    }
    
    /**
     * Determines if a file should be archived
     */
    private boolean shouldArchive(Path file) {
        try {
            long fileSize = Files.size(file);
            long fileAge = System.currentTimeMillis() - Files.getLastModifiedTime(file).toMillis();
            
            // Archive files older than 30 days or larger than 100MB
            return fileAge > (30L * 24 * 60 * 60 * 1000) || fileSize > (100L * 1024 * 1024);
        } catch (IOException e) {
            logger.warn("Could not determine archive criteria for: " + file, e);
            return false;
        }
    }
    
    /**
     * Searches for files based on criteria
     */
    public List<FileInfo> searchFiles(FileSearchCriteria criteria) {
        List<FileInfo> results = new ArrayList<>();
        
        for (FileInfo fileInfo : fileRegistry.values()) {
            if (matchesSearchCriteria(fileInfo, criteria)) {
                results.add(fileInfo);
            }
        }
        
        return results;
    }
    
    /**
     * Checks if a file info matches search criteria
     */
    private boolean matchesSearchCriteria(FileInfo fileInfo, FileSearchCriteria criteria) {
        // Check name pattern
        if (criteria.getNamePattern() != null && !criteria.getNamePattern().isEmpty()) {
            if (!fileInfo.getName().matches(criteria.getNamePattern())) {
                return false;
            }
        }
        
        // Check size range
        if (criteria.getMinSize() > 0 && fileInfo.getSize() < criteria.getMinSize()) {
            return false;
        }
        if (criteria.getMaxSize() > 0 && fileInfo.getSize() > criteria.getMaxSize()) {
            return false;
        }
        
        // Check date range
        if (criteria.getFromDate() != null && fileInfo.getProcessedDate().isBefore(criteria.getFromDate())) {
            return false;
        }
        if (criteria.getToDate() != null && fileInfo.getProcessedDate().isAfter(criteria.getToDate())) {
            return false;
        }
        
        // Check processing type
        if (criteria.getProcessingType() != null && !criteria.getProcessingType().isEmpty()) {
            if (!fileInfo.getProcessingType().equals(criteria.getProcessingType())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Generates a report of file processing activities
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== File Manager Report ===\n");
        report.append("Base Directory: ").append(baseDirectory).append("\n");
        report.append("Total Files Processed: ").append(filesProcessed).append("\n");
        report.append("Total Size Processed: ").append(formatFileSize(totalSizeProcessed)).append("\n");
        report.append("Registry Size: ").append(fileRegistry.size()).append("\n");
        report.append("Operation Log Entries: ").append(operationLog.size()).append("\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        // Add recent operations
        report.append("Recent Operations:\n");
        int startIndex = Math.max(0, operationLog.size() - 10);
        for (int i = startIndex; i < operationLog.size(); i++) {
            report.append("- ").append(operationLog.get(i)).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Formats file size in human-readable format
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * Clears all data and resets the manager
     */
    public void reset() {
        fileRegistry.clear();
        processedFiles.clear();
        operationLog.clear();
        totalSizeProcessed = 0;
        filesProcessed = 0;
        isInitialized = false;
        logger.info("File manager reset");
    }
    
    // Getters for testing
    public boolean isInitialized() { return isInitialized; }
    public Path getBaseDirectory() { return baseDirectory; }
    public int getFilesProcessed() { return filesProcessed; }
    public long getTotalSizeProcessed() { return totalSizeProcessed; }
    public int getRegistrySize() { return fileRegistry.size(); }
    public int getProcessedFilesCount() { return processedFiles.size(); }
    public List<String> getOperationLog() { return new ArrayList<>(operationLog); }
    public Map<String, FileInfo> getFileRegistry() { return new HashMap<>(fileRegistry); }
    
    // Data classes
    public static class FileInfo {
        private final String name;
        private final String path;
        private final long size;
        private final LocalDateTime processedDate;
        private final String processingType;
        
        public FileInfo(String name, String path, long size, LocalDateTime processedDate, String processingType) {
            this.name = name;
            this.path = path;
            this.size = size;
            this.processedDate = processedDate;
            this.processingType = processingType;
        }
        
        public String getName() { return name; }
        public String getPath() { return path; }
        public long getSize() { return size; }
        public LocalDateTime getProcessedDate() { return processedDate; }
        public String getProcessingType() { return processingType; }
    }
    
    public static class FileProcessingOptions {
        private boolean backupEnabled = false;
        private boolean compressEnabled = false;
        private boolean validateEnabled = false;
        private boolean archiveEnabled = false;
        private List<String> includeExtensions = new ArrayList<>();
        private long maxFileSize = 0;
        private long minFileSize = 0;
        private String filenamePattern = "";
        
        // Getters and setters
        public boolean isBackupEnabled() { return backupEnabled; }
        public void setBackupEnabled(boolean backupEnabled) { this.backupEnabled = backupEnabled; }
        
        public boolean isCompressEnabled() { return compressEnabled; }
        public void setCompressEnabled(boolean compressEnabled) { this.compressEnabled = compressEnabled; }
        
        public boolean isValidateEnabled() { return validateEnabled; }
        public void setValidateEnabled(boolean validateEnabled) { this.validateEnabled = validateEnabled; }
        
        public boolean isArchiveEnabled() { return archiveEnabled; }
        public void setArchiveEnabled(boolean archiveEnabled) { this.archiveEnabled = archiveEnabled; }
        
        public List<String> getIncludeExtensions() { return includeExtensions; }
        public void setIncludeExtensions(List<String> includeExtensions) { this.includeExtensions = includeExtensions; }
        
        public long getMaxFileSize() { return maxFileSize; }
        public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }
        
        public long getMinFileSize() { return minFileSize; }
        public void setMinFileSize(long minFileSize) { this.minFileSize = minFileSize; }
        
        public String getFilenamePattern() { return filenamePattern; }
        public void setFilenamePattern(String filenamePattern) { this.filenamePattern = filenamePattern; }
    }
    
    public static class FileSearchCriteria {
        private String namePattern = "";
        private long minSize = 0;
        private long maxSize = 0;
        private LocalDateTime fromDate = null;
        private LocalDateTime toDate = null;
        private String processingType = "";
        
        // Getters and setters
        public String getNamePattern() { return namePattern; }
        public void setNamePattern(String namePattern) { this.namePattern = namePattern; }
        
        public long getMinSize() { return minSize; }
        public void setMinSize(long minSize) { this.minSize = minSize; }
        
        public long getMaxSize() { return maxSize; }
        public void setMaxSize(long maxSize) { this.maxSize = maxSize; }
        
        public LocalDateTime getFromDate() { return fromDate; }
        public void setFromDate(LocalDateTime fromDate) { this.fromDate = fromDate; }
        
        public LocalDateTime getToDate() { return toDate; }
        public void setToDate(LocalDateTime toDate) { this.toDate = toDate; }
        
        public String getProcessingType() { return processingType; }
        public void setProcessingType(String processingType) { this.processingType = processingType; }
    }
    
    public static class ProcessingSummary {
        private final boolean success;
        private final String message;
        private final int processedCount;
        private final long totalSize;
        
        public ProcessingSummary(boolean success, String message, int processedCount, long totalSize) {
            this.success = success;
            this.message = message;
            this.processedCount = processedCount;
            this.totalSize = totalSize;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getProcessedCount() { return processedCount; }
        public long getTotalSize() { return totalSize; }
    }
    
    public static class FileProcessingResult {
        private final boolean success;
        private final String message;
        private final long fileSize;
        private final String processingType;
        
        public FileProcessingResult(boolean success, String message, long fileSize, String processingType) {
            this.success = success;
            this.message = message;
            this.fileSize = fileSize;
            this.processingType = processingType;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public long getFileSize() { return fileSize; }
        public String getProcessingType() { return processingType; }
    }
} 