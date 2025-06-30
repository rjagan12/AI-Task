package com.observer.pattern;

import java.time.format.DateTimeFormatter;

/**
 * LogEventListener is a concrete implementation of Observer
 * that handles logging events when they occur.
 */
public class LogEventListener implements Observer {
    
    private final String observerId;
    private final String logLevel;
    private final String logFilePath;
    
    /**
     * Constructor for LogEventListener.
     * 
     * @param logLevel the log level for this listener
     * @param logFilePath the file path for logging
     */
    public LogEventListener(String logLevel, String logFilePath) {
        this.logLevel = logLevel;
        this.logFilePath = logFilePath;
        this.observerId = "LogEventListener-" + logLevel + "-" + logFilePath;
    }
    
    /**
     * Constructor for LogEventListener with default log level.
     * 
     * @param logFilePath the file path for logging
     */
    public LogEventListener(String logFilePath) {
        this("INFO", logFilePath);
    }
    
    @Override
    public void update(Event event) {
        System.out.println("📝 Log event recorded in " + logFilePath + 
                          " for event: " + event.getType() + 
                          " - " + event.getMessage());
        
        // In a real implementation, this would write to an actual log file
        // using a logging framework like Log4j, SLF4J, or similar
        writeToLog(event);
    }
    
    @Override
    public String getObserverId() {
        return observerId;
    }
    
    /**
     * Simulates writing an event to a log file.
     * 
     * @param event the event to log
     */
    private void writeToLog(Event event) {
        // Simulate log writing process
        String logEntry = String.format(
            "[%s] %s - Event: %s, Message: %s, ID: %s, Data: %s",
            logLevel,
            event.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            event.getType(),
            event.getMessage(),
            event.getId(),
            event.getData() != null ? event.getData().toString() : "null"
        );
        
        // In a real implementation, this would write to an actual log file
        System.out.println("📝 Log Entry:");
        System.out.println("  File: " + logFilePath);
        System.out.println("  Entry: " + logEntry);
    }
    
    /**
     * Gets the log level for this listener.
     * 
     * @return the log level
     */
    public String getLogLevel() {
        return logLevel;
    }
    
    /**
     * Gets the log file path for this listener.
     * 
     * @return the log file path
     */
    public String getLogFilePath() {
        return logFilePath;
    }
} 