package com.observer.pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for concrete observer implementations.
 * Tests EmailNotificationListener, SMSNotificationListener, and LogEventListener.
 */
@DisplayName("Observer Implementations Tests")
public class ObserverImplementationsTest {
    
    @Test
    @DisplayName("EmailNotificationListener should have correct observer ID")
    void testEmailNotificationListenerObserverId() {
        EmailNotificationListener emailListener = new EmailNotificationListener("test@example.com");
        
        assertEquals("EmailNotificationListener-test@example.com", emailListener.getObserverId());
    }
    
    @Test
    @DisplayName("EmailNotificationListener should return correct email address")
    void testEmailNotificationListenerEmailAddress() {
        EmailNotificationListener emailListener = new EmailNotificationListener("test@example.com");
        
        assertEquals("test@example.com", emailListener.getEmailAddress());
    }
    
    @Test
    @DisplayName("EmailNotificationListener should handle update correctly")
    void testEmailNotificationListenerUpdate() {
        EmailNotificationListener emailListener = new EmailNotificationListener("test@example.com");
        Event testEvent = new Event("TEST_EVENT", "Test message", "Test data");
        
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            emailListener.update(testEvent);
        });
    }
    
    @Test
    @DisplayName("SMSNotificationListener should have correct observer ID")
    void testSMSNotificationListenerObserverId() {
        SMSNotificationListener smsListener = new SMSNotificationListener("+1234567890");
        
        assertEquals("SMSNotificationListener-+1234567890", smsListener.getObserverId());
    }
    
    @Test
    @DisplayName("SMSNotificationListener should return correct phone number")
    void testSMSNotificationListenerPhoneNumber() {
        SMSNotificationListener smsListener = new SMSNotificationListener("+1234567890");
        
        assertEquals("+1234567890", smsListener.getPhoneNumber());
    }
    
    @Test
    @DisplayName("SMSNotificationListener should handle update correctly")
    void testSMSNotificationListenerUpdate() {
        SMSNotificationListener smsListener = new SMSNotificationListener("+1234567890");
        Event testEvent = new Event("TEST_EVENT", "Test message", "Test data");
        
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            smsListener.update(testEvent);
        });
    }
    
    @Test
    @DisplayName("LogEventListener should have correct observer ID with log level")
    void testLogEventListenerObserverIdWithLogLevel() {
        LogEventListener logListener = new LogEventListener("ERROR", "/var/log/test.log");
        
        assertEquals("LogEventListener-ERROR-/var/log/test.log", logListener.getObserverId());
    }
    
    @Test
    @DisplayName("LogEventListener should have correct observer ID without log level")
    void testLogEventListenerObserverIdWithoutLogLevel() {
        LogEventListener logListener = new LogEventListener("/var/log/test.log");
        
        assertEquals("LogEventListener-INFO-/var/log/test.log", logListener.getObserverId());
    }
    
    @Test
    @DisplayName("LogEventListener should return correct log level")
    void testLogEventListenerLogLevel() {
        LogEventListener logListener = new LogEventListener("WARN", "/var/log/test.log");
        
        assertEquals("WARN", logListener.getLogLevel());
    }
    
    @Test
    @DisplayName("LogEventListener should return correct log file path")
    void testLogEventListenerLogFilePath() {
        LogEventListener logListener = new LogEventListener("ERROR", "/var/log/test.log");
        
        assertEquals("/var/log/test.log", logListener.getLogFilePath());
    }
    
    @Test
    @DisplayName("LogEventListener should handle update correctly")
    void testLogEventListenerUpdate() {
        LogEventListener logListener = new LogEventListener("INFO", "/var/log/test.log");
        Event testEvent = new Event("TEST_EVENT", "Test message", "Test data");
        
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            logListener.update(testEvent);
        });
    }
    
    @Test
    @DisplayName("LogEventListener should use default INFO log level when not specified")
    void testLogEventListenerDefaultLogLevel() {
        LogEventListener logListener = new LogEventListener("/var/log/test.log");
        
        assertEquals("INFO", logListener.getLogLevel());
    }
} 