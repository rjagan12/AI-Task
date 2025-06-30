package com.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Legacy System Facade - Provides a clean interface to the legacy system components.
 * This facade wraps the complex legacy classes and provides simplified methods for testing.
 */
public class LegacySystemFacade {
    private static final Logger logger = LoggerFactory.getLogger(LegacySystemFacade.class);
    
    private final LegacyDataProcessor dataProcessor;
    private final LegacyFileManager fileManager;
    private final Map<String, Object> systemState = new ConcurrentHashMap<>();
    private final List<SystemEvent> eventLog = new ArrayList<>();
    
    private boolean isSystemReady = false;
    private LocalDateTime lastOperationTime;
    
    public LegacySystemFacade() {
        this.dataProcessor = new LegacyDataProcessor();
        this.fileManager = new LegacyFileManager();
    }
    
    /**
     * Initializes the entire legacy system
     */
    public SystemInitializationResult initializeSystem(SystemConfiguration config) {
        try {
            logger.info("Initializing legacy system...");
            
            // Initialize data processor
            Map<String, Object> processorConfig = new HashMap<>();
            processorConfig.put("maxCacheSize", config.getMaxCacheSize());
            processorConfig.put("enableLogging", config.isEnableLogging());
            
            boolean processorInitialized = dataProcessor.initialize(processorConfig);
            if (!processorInitialized) {
                return new SystemInitializationResult(false, "Failed to initialize data processor");
            }
            
            // Initialize file manager
            boolean fileManagerInitialized = fileManager.initialize(config.getBaseDirectory());
            if (!fileManagerInitialized) {
                return new SystemInitializationResult(false, "Failed to initialize file manager");
            }
            
            // Set system state
            systemState.put("initialized", true);
            systemState.put("config", config);
            systemState.put("startTime", LocalDateTime.now());
            
            isSystemReady = true;
            lastOperationTime = LocalDateTime.now();
            
            logEvent("SYSTEM_INITIALIZED", "System initialized successfully");
            
            return new SystemInitializationResult(true, "System initialized successfully");
            
        } catch (Exception e) {
            logger.error("System initialization failed", e);
            return new SystemInitializationResult(false, "Initialization failed: " + e.getMessage());
        }
    }
    
    /**
     * Processes data and files in a coordinated manner
     */
    public ProcessingResult processDataAndFiles(String data, String directoryPath, ProcessingOptions options) {
        if (!isSystemReady) {
            return new ProcessingResult(false, "System not initialized", null);
        }
        
        try {
            logger.info("Starting coordinated processing...");
            
            // Process data first
            LegacyDataProcessor.ProcessingResult dataResult = dataProcessor.processData(data);
            if (!dataResult.isSuccess()) {
                return new ProcessingResult(false, "Data processing failed: " + dataResult.getMessage(), null);
            }
            
            // Process files
            LegacyFileManager.FileProcessingOptions fileOptions = createFileProcessingOptions(options);
            LegacyFileManager.ProcessingSummary fileResult = fileManager.processDirectory(directoryPath, fileOptions);
            if (!fileResult.isSuccess()) {
                return new ProcessingResult(false, "File processing failed: " + fileResult.getMessage(), null);
            }
            
            // Combine results
            Map<String, Object> combinedResults = new HashMap<>();
            combinedResults.put("dataProcessing", dataResult.getData());
            combinedResults.put("fileProcessing", createFileProcessingSummary(fileResult));
            combinedResults.put("systemMetrics", generateSystemMetrics());
            
            lastOperationTime = LocalDateTime.now();
            logEvent("PROCESSING_COMPLETED", "Data and file processing completed successfully");
            
            return new ProcessingResult(true, "Processing completed successfully", combinedResults);
            
        } catch (Exception e) {
            logger.error("Coordinated processing failed", e);
            logEvent("PROCESSING_ERROR", "Processing failed: " + e.getMessage());
            return new ProcessingResult(false, "Processing failed: " + e.getMessage(), null);
        }
    }
    
    /**
     * Validates data and file system state
     */
    public ValidationResult validateSystem() {
        if (!isSystemReady) {
            return new ValidationResult(false, "System not initialized");
        }
        
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validate data processor state
        if (!dataProcessor.isInitialized()) {
            errors.add("Data processor not initialized");
        }
        
        if (dataProcessor.getErrorCount() > 0) {
            warnings.add("Data processor has " + dataProcessor.getErrorCount() + " errors in log");
        }
        
        // Validate file manager state
        if (!fileManager.isInitialized()) {
            errors.add("File manager not initialized");
        }
        
        if (fileManager.getRegistrySize() == 0) {
            warnings.add("File manager has no files in registry");
        }
        
        // Validate system state
        if (lastOperationTime == null) {
            warnings.add("No operations have been performed");
        }
        
        boolean isValid = errors.isEmpty();
        String message = isValid ? "System validation passed" : "System validation failed";
        
        if (!warnings.isEmpty()) {
            message += " (Warnings: " + String.join(", ", warnings) + ")";
        }
        
        logEvent("SYSTEM_VALIDATION", message);
        
        return new ValidationResult(isValid, message, errors, warnings);
    }
    
    /**
     * Performs system maintenance operations
     */
    public MaintenanceResult performMaintenance(MaintenanceOptions options) {
        if (!isSystemReady) {
            return new MaintenanceResult(false, "System not initialized");
        }
        
        try {
            logger.info("Starting system maintenance...");
            
            int operationsPerformed = 0;
            List<String> operations = new ArrayList<>();
            
            // Clear caches if requested
            if (options.isClearCaches()) {
                dataProcessor.reset();
                operations.add("Data processor cache cleared");
                operationsPerformed++;
            }
            
            // Clean up file registry if requested
            if (options.isCleanupFileRegistry()) {
                // This would involve more complex logic in a real system
                operations.add("File registry cleanup initiated");
                operationsPerformed++;
            }
            
            // Generate reports if requested
            if (options.isGenerateReports()) {
                String dataReport = dataProcessor.exportToJson();
                String fileReport = fileManager.generateReport();
                
                systemState.put("lastDataReport", dataReport);
                systemState.put("lastFileReport", fileReport);
                
                operations.add("System reports generated");
                operationsPerformed++;
            }
            
            // Update system state
            systemState.put("lastMaintenance", LocalDateTime.now());
            systemState.put("maintenanceOperations", operations);
            
            lastOperationTime = LocalDateTime.now();
            logEvent("MAINTENANCE_COMPLETED", "Maintenance completed with " + operationsPerformed + " operations");
            
            return new MaintenanceResult(true, 
                "Maintenance completed successfully. Operations: " + String.join(", ", operations));
                
        } catch (Exception e) {
            logger.error("Maintenance failed", e);
            logEvent("MAINTENANCE_ERROR", "Maintenance failed: " + e.getMessage());
            return new MaintenanceResult(false, "Maintenance failed: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves system statistics and metrics
     */
    public SystemMetrics getSystemMetrics() {
        if (!isSystemReady) {
            return new SystemMetrics(false, "System not initialized");
        }
        
        try {
            SystemMetrics metrics = new SystemMetrics();
            metrics.setSystemReady(true);
            metrics.setDataProcessorMetrics(createDataProcessorMetrics());
            metrics.setFileManagerMetrics(createFileManagerMetrics());
            metrics.setSystemStateMetrics(createSystemStateMetrics());
            metrics.setLastOperationTime(lastOperationTime);
            metrics.setEventLogSize(eventLog.size());
            
            return metrics;
            
        } catch (Exception e) {
            logger.error("Error generating system metrics", e);
            return new SystemMetrics(false, "Error generating metrics: " + e.getMessage());
        }
    }
    
    /**
     * Creates file processing options from general processing options
     */
    private LegacyFileManager.FileProcessingOptions createFileProcessingOptions(ProcessingOptions options) {
        LegacyFileManager.FileProcessingOptions fileOptions = new LegacyFileManager.FileProcessingOptions();
        fileOptions.setBackupEnabled(options.isBackupFiles());
        fileOptions.setCompressEnabled(options.isCompressFiles());
        fileOptions.setValidateEnabled(options.isValidateFiles());
        fileOptions.setArchiveEnabled(options.isArchiveFiles());
        fileOptions.setIncludeExtensions(options.getFileExtensions());
        fileOptions.setMaxFileSize(options.getMaxFileSize());
        fileOptions.setMinFileSize(options.getMinFileSize());
        return fileOptions;
    }
    
    /**
     * Creates file processing summary from file manager result
     */
    private Map<String, Object> createFileProcessingSummary(LegacyFileManager.ProcessingSummary fileResult) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("success", fileResult.isSuccess());
        summary.put("message", fileResult.getMessage());
        summary.put("processedCount", fileResult.getProcessedCount());
        summary.put("totalSize", fileResult.getTotalSize());
        return summary;
    }
    
    /**
     * Generates system metrics
     */
    private Map<String, Object> generateSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("systemReady", isSystemReady);
        metrics.put("lastOperationTime", lastOperationTime);
        metrics.put("eventLogSize", eventLog.size());
        metrics.put("systemStateSize", systemState.size());
        return metrics;
    }
    
    /**
     * Creates data processor metrics
     */
    private Map<String, Object> createDataProcessorMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("initialized", dataProcessor.isInitialized());
        metrics.put("processedCount", dataProcessor.getProcessedCount());
        metrics.put("totalAmount", dataProcessor.getTotalAmount());
        metrics.put("cacheSize", dataProcessor.getCacheSize());
        metrics.put("errorCount", dataProcessor.getErrorCount());
        return metrics;
    }
    
    /**
     * Creates file manager metrics
     */
    private Map<String, Object> createFileManagerMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("initialized", fileManager.isInitialized());
        metrics.put("filesProcessed", fileManager.getFilesProcessed());
        metrics.put("totalSizeProcessed", fileManager.getTotalSizeProcessed());
        metrics.put("registrySize", fileManager.getRegistrySize());
        metrics.put("processedFilesCount", fileManager.getProcessedFilesCount());
        return metrics;
    }
    
    /**
     * Creates system state metrics
     */
    private Map<String, Object> createSystemStateMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("stateSize", systemState.size());
        metrics.put("initialized", systemState.get("initialized"));
        metrics.put("startTime", systemState.get("startTime"));
        metrics.put("lastMaintenance", systemState.get("lastMaintenance"));
        return metrics;
    }
    
    /**
     * Logs system events
     */
    private void logEvent(String eventType, String message) {
        SystemEvent event = new SystemEvent(eventType, message, LocalDateTime.now());
        eventLog.add(event);
        logger.info("System Event: {} - {}", eventType, message);
    }
    
    /**
     * Shuts down the system gracefully
     */
    public void shutdown() {
        logger.info("Shutting down legacy system...");
        
        // Perform cleanup operations
        dataProcessor.reset();
        fileManager.reset();
        
        systemState.clear();
        eventLog.clear();
        
        isSystemReady = false;
        lastOperationTime = null;
        
        logger.info("Legacy system shutdown complete");
    }
    
    // Getters for testing
    public boolean isSystemReady() { return isSystemReady; }
    public LocalDateTime getLastOperationTime() { return lastOperationTime; }
    public int getEventLogSize() { return eventLog.size(); }
    public int getSystemStateSize() { return systemState.size(); }
    public List<SystemEvent> getEventLog() { return new ArrayList<>(eventLog); }
    public Map<String, Object> getSystemState() { return new HashMap<>(systemState); }
    
    // Data classes
    public static class SystemConfiguration {
        private String baseDirectory = "./data";
        private int maxCacheSize = 1000;
        private boolean enableLogging = true;
        private boolean enableBackup = true;
        private boolean enableCompression = false;
        
        // Getters and setters
        public String getBaseDirectory() { return baseDirectory; }
        public void setBaseDirectory(String baseDirectory) { this.baseDirectory = baseDirectory; }
        
        public int getMaxCacheSize() { return maxCacheSize; }
        public void setMaxCacheSize(int maxCacheSize) { this.maxCacheSize = maxCacheSize; }
        
        public boolean isEnableLogging() { return enableLogging; }
        public void setEnableLogging(boolean enableLogging) { this.enableLogging = enableLogging; }
        
        public boolean isEnableBackup() { return enableBackup; }
        public void setEnableBackup(boolean enableBackup) { this.enableBackup = enableBackup; }
        
        public boolean isEnableCompression() { return enableCompression; }
        public void setEnableCompression(boolean enableCompression) { this.enableCompression = enableCompression; }
    }
    
    public static class ProcessingOptions {
        private boolean backupFiles = true;
        private boolean compressFiles = false;
        private boolean validateFiles = true;
        private boolean archiveFiles = false;
        private List<String> fileExtensions = new ArrayList<>();
        private long maxFileSize = 0;
        private long minFileSize = 0;
        
        // Getters and setters
        public boolean isBackupFiles() { return backupFiles; }
        public void setBackupFiles(boolean backupFiles) { this.backupFiles = backupFiles; }
        
        public boolean isCompressFiles() { return compressFiles; }
        public void setCompressFiles(boolean compressFiles) { this.compressFiles = compressFiles; }
        
        public boolean isValidateFiles() { return validateFiles; }
        public void setValidateFiles(boolean validateFiles) { this.validateFiles = validateFiles; }
        
        public boolean isArchiveFiles() { return archiveFiles; }
        public void setArchiveFiles(boolean archiveFiles) { this.archiveFiles = archiveFiles; }
        
        public List<String> getFileExtensions() { return fileExtensions; }
        public void setFileExtensions(List<String> fileExtensions) { this.fileExtensions = fileExtensions; }
        
        public long getMaxFileSize() { return maxFileSize; }
        public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }
        
        public long getMinFileSize() { return minFileSize; }
        public void setMinFileSize(long minFileSize) { this.minFileSize = minFileSize; }
    }
    
    public static class MaintenanceOptions {
        private boolean clearCaches = false;
        private boolean cleanupFileRegistry = false;
        private boolean generateReports = true;
        
        // Getters and setters
        public boolean isClearCaches() { return clearCaches; }
        public void setClearCaches(boolean clearCaches) { this.clearCaches = clearCaches; }
        
        public boolean isCleanupFileRegistry() { return cleanupFileRegistry; }
        public void setCleanupFileRegistry(boolean cleanupFileRegistry) { this.cleanupFileRegistry = cleanupFileRegistry; }
        
        public boolean isGenerateReports() { return generateReports; }
        public void setGenerateReports(boolean generateReports) { this.generateReports = generateReports; }
    }
    
    public static class SystemEvent {
        private final String eventType;
        private final String message;
        private final LocalDateTime timestamp;
        
        public SystemEvent(String eventType, String message, LocalDateTime timestamp) {
            this.eventType = eventType;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getEventType() { return eventType; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public static class SystemInitializationResult {
        private final boolean success;
        private final String message;
        
        public SystemInitializationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class ProcessingResult {
        private final boolean success;
        private final String message;
        private final Map<String, Object> data;
        
        public ProcessingResult(boolean success, String message, Map<String, Object> data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return data; }
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final List<String> errors;
        private final List<String> warnings;
        
        public ValidationResult(boolean valid, String message) {
            this(valid, message, new ArrayList<>(), new ArrayList<>());
        }
        
        public ValidationResult(boolean valid, String message, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.message = message;
            this.errors = errors;
            this.warnings = warnings;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
    }
    
    public static class MaintenanceResult {
        private final boolean success;
        private final String message;
        
        public MaintenanceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class SystemMetrics {
        private boolean systemReady = false;
        private String errorMessage = "";
        private Map<String, Object> dataProcessorMetrics = new HashMap<>();
        private Map<String, Object> fileManagerMetrics = new HashMap<>();
        private Map<String, Object> systemStateMetrics = new HashMap<>();
        private LocalDateTime lastOperationTime = null;
        private int eventLogSize = 0;
        
        public SystemMetrics() {}
        
        public SystemMetrics(boolean systemReady, String errorMessage) {
            this.systemReady = systemReady;
            this.errorMessage = errorMessage;
        }
        
        // Getters and setters
        public boolean isSystemReady() { return systemReady; }
        public void setSystemReady(boolean systemReady) { this.systemReady = systemReady; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public Map<String, Object> getDataProcessorMetrics() { return dataProcessorMetrics; }
        public void setDataProcessorMetrics(Map<String, Object> dataProcessorMetrics) { this.dataProcessorMetrics = dataProcessorMetrics; }
        
        public Map<String, Object> getFileManagerMetrics() { return fileManagerMetrics; }
        public void setFileManagerMetrics(Map<String, Object> fileManagerMetrics) { this.fileManagerMetrics = fileManagerMetrics; }
        
        public Map<String, Object> getSystemStateMetrics() { return systemStateMetrics; }
        public void setSystemStateMetrics(Map<String, Object> systemStateMetrics) { this.systemStateMetrics = systemStateMetrics; }
        
        public LocalDateTime getLastOperationTime() { return lastOperationTime; }
        public void setLastOperationTime(LocalDateTime lastOperationTime) { this.lastOperationTime = lastOperationTime; }
        
        public int getEventLogSize() { return eventLogSize; }
        public void setEventLogSize(int eventLogSize) { this.eventLogSize = eventLogSize; }
    }
} 