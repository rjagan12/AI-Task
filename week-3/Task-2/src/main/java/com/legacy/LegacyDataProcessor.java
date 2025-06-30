package com.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LegacyDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(LegacyDataProcessor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final List<String> processedItems = new ArrayList<>();
    private final Set<String> errorLog = new HashSet<>();
    
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private int processedCount = 0;
    private boolean isInitialized = false;
    
    public ProcessingResult processData(String rawData) {
        if (rawData == null || rawData.trim().isEmpty()) {
            logger.warn("Empty or null data received");
            return new ProcessingResult(false, "Invalid input data", null);
        }
        try {
            String[] lines = rawData.split("\n");
            List<DataRecord> records = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                try {
                    DataRecord record = parseDataLine(line, i + 1);
                    if (record != null) {
                        records.add(record);
                        processedItems.add(record.getId());
                    }
                } catch (Exception e) {
                    String errorMsg = "Error processing line " + (i + 1) + ": " + e.getMessage();
                    errorLog.add(errorMsg);
                    logger.error(errorMsg, e);
                }
            }
            BigDecimal total = calculateTotal(records);
            Map<String, Object> statistics = generateStatistics(records);
            String cacheKey = generateCacheKey(rawData);
            cache.put(cacheKey, statistics);
            processedCount += records.size();
            totalAmount = totalAmount.add(total);
            return new ProcessingResult(true, "Successfully processed " + records.size() + " records", statistics);
        } catch (Exception e) {
            String errorMsg = "Critical error during data processing: " + e.getMessage();
            logger.error(errorMsg, e);
            return new ProcessingResult(false, errorMsg, null);
        }
    }
    
    private DataRecord parseDataLine(String line, int lineNumber) {
        String[] parts = line.split(",");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid data format at line " + lineNumber);
        }
        String id = parts[0].trim();
        String name = parts[1].trim();
        String valueStr = parts[2].trim();
        if (!id.matches("[A-Z]{2}\\d{6}")) {
            throw new IllegalArgumentException("Invalid ID format: " + id);
        }
        BigDecimal value;
        try {
            value = new BigDecimal(valueStr);
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Value cannot be negative: " + value);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + valueStr);
        }
        LocalDateTime timestamp = null;
        if (parts.length > 3) {
            try {
                timestamp = LocalDateTime.parse(parts[3].trim(), DATE_FORMATTER);
            } catch (Exception e) {
                logger.warn("Invalid timestamp format at line " + lineNumber + ", using current time");
                timestamp = LocalDateTime.now();
            }
        } else {
            timestamp = LocalDateTime.now();
        }
        return new DataRecord(id, name, value, timestamp);
    }
    
    private BigDecimal calculateTotal(List<DataRecord> records) {
        if (records.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = records.stream()
                .map(DataRecord::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Apply business rules (exclusive discounts)
        boolean overThousand = total.compareTo(new BigDecimal("1000")) > 0;
        boolean bulk = records.size() > 10;
        if (overThousand) {
            // Apply 5% discount for large amounts
            BigDecimal discount = total.multiply(new BigDecimal("0.05"));
            total = total.subtract(discount);
            logger.info("Applied 5% discount: " + discount);
        } else if (bulk) {
            // Apply bulk discount
            BigDecimal bulkDiscount = total.multiply(new BigDecimal("0.02"));
            total = total.subtract(bulkDiscount);
            logger.info("Applied bulk discount: " + bulkDiscount);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
    
    private Map<String, Object> generateStatistics(List<DataRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        if (records.isEmpty()) {
            stats.put("count", 0);
            stats.put("total", BigDecimal.ZERO);
            stats.put("average", BigDecimal.ZERO);
            stats.put("min", BigDecimal.ZERO);
            stats.put("max", BigDecimal.ZERO);
            return stats;
        }
        stats.put("count", records.size());
        BigDecimal total = records.stream()
                .map(DataRecord::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("total", total);
        BigDecimal average = total.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
        stats.put("average", average);
        BigDecimal min = records.stream()
                .map(DataRecord::getValue)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        stats.put("min", min);
        BigDecimal max = records.stream()
                .map(DataRecord::getValue)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        stats.put("max", max);
        Map<String, Long> nameFrequency = records.stream()
                .collect(Collectors.groupingBy(DataRecord::getName, Collectors.counting()));
        stats.put("nameFrequency", nameFrequency);
        List<BigDecimal> values = records.stream()
                .map(DataRecord::getValue)
                .sorted()
                .collect(Collectors.toList());
        stats.put("sortedValues", values);
        LocalDateTime earliest = records.stream()
                .map(DataRecord::getTimestamp)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        stats.put("earliestTimestamp", earliest);
        LocalDateTime latest = records.stream()
                .map(DataRecord::getTimestamp)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        stats.put("latestTimestamp", latest);
        return stats;
    }
    
    private String generateCacheKey(String data) {
        return "data_" + data.hashCode() + "_" + System.currentTimeMillis();
    }
    
    public ValidationResult validateData(String data) {
        if (data == null) {
            return new ValidationResult(false, "Data cannot be null");
        }
        if (data.trim().isEmpty()) {
            return new ValidationResult(false, "Data cannot be empty");
        }
        String[] lines = data.split("\n");
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length < 3) {
                errors.add("Line " + (i + 1) + ": Insufficient data fields");
                continue;
            }
            String id = parts[0].trim();
            if (!id.matches("[A-Z]{2}\\d{6}")) {
                errors.add("Line " + (i + 1) + ": Invalid ID format");
            }
            String name = parts[1].trim();
            if (name.length() < 2 || name.length() > 50) {
                errors.add("Line " + (i + 1) + ": Name length must be between 2 and 50 characters");
            }
            try {
                BigDecimal value = new BigDecimal(parts[2].trim());
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Line " + (i + 1) + ": Value cannot be negative");
                }
            } catch (NumberFormatException e) {
                errors.add("Line " + (i + 1) + ": Invalid numeric value");
            }
        }
        return new ValidationResult(errors.isEmpty(), errors.isEmpty() ? "Valid" : String.join("; ", errors));
    }
    
    public String exportToJson() {
        try {
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("processedCount", processedCount);
            exportData.put("totalAmount", totalAmount);
            exportData.put("processedItems", processedItems);
            exportData.put("errorLog", new ArrayList<>(errorLog));
            exportData.put("cacheSize", cache.size());
            exportData.put("exportTimestamp", LocalDateTime.now().format(DATE_FORMATTER));
            return objectMapper.writeValueAsString(exportData);
        } catch (IOException e) {
            logger.error("Error exporting to JSON", e);
            return "{\"error\": \"Export failed: " + e.getMessage() + "\"}";
        }
    }
    
    public void reset() {
        cache.clear();
        processedItems.clear();
        errorLog.clear();
        totalAmount = BigDecimal.ZERO;
        processedCount = 0;
        isInitialized = false;
        logger.info("Processor state reset");
    }
    
    public boolean initialize(Map<String, Object> config) {
        try {
            if (config == null || config.isEmpty()) {
                logger.warn("Empty configuration provided");
                return false;
            }
            if (!config.containsKey("maxCacheSize") || !config.containsKey("enableLogging")) {
                logger.error("Missing required configuration parameters");
                return false;
            }
            isInitialized = true;
            logger.info("Processor initialized successfully");
            return true;
        } catch (Exception e) {
            logger.error("Initialization failed", e);
            return false;
        }
    }
    
    public int getProcessedCount() { return processedCount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public boolean isInitialized() { return isInitialized; }
    public int getCacheSize() { return cache.size(); }
    public int getErrorCount() { return errorLog.size(); }
    public List<String> getProcessedItems() { return new ArrayList<>(processedItems); }
    public Set<String> getErrorLog() { return new HashSet<>(errorLog); }
    
    public static class DataRecord {
        private final String id;
        private final String name;
        private final BigDecimal value;
        private final LocalDateTime timestamp;
        public DataRecord(String id, String name, BigDecimal value, LocalDateTime timestamp) {
            this.id = id;
            this.name = name;
            this.value = value;
            this.timestamp = timestamp;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public BigDecimal getValue() { return value; }
        public LocalDateTime getTimestamp() { return timestamp; }
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
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
} 