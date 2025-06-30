package com.example.bank.security;

import com.example.bank.dto.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Service responsible for security validations and fraud detection.
 * 
 * This class follows the Single Responsibility Principle by focusing solely
 * on security-related concerns and implements multiple security layers.
 * 
 * @author Refactored Banking System
 * @version 2.0
 */
public class SecurityService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    
    // Security patterns
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    private static final Pattern SUSPICIOUS_IP_PATTERN = Pattern.compile(
        "^(10\\.|172\\.(1[6-9]|2[0-9]|3[01])\\.|192\\.168\\.)");
    
    // Security thresholds
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    
    /**
     * Validates a transaction request for security concerns.
     * 
     * @param request the transaction request to validate
     * @throws SecurityException if security validation fails
     */
    public void validateTransactionRequest(TransactionRequest request) {
        logger.debug("Validating transaction request for security: {}", request.getTransactionId());
        
        validateIpAddress(request.getIpAddress());
        validateUserAgent(request.getUserAgent());
        validateSession(request.getSessionId());
        validateTransactionAmount(request);
        validateApprovalCode(request);
        checkForSuspiciousActivity(request);
    }
    
    /**
     * Validates the IP address for security concerns.
     * 
     * @param ipAddress the IP address to validate
     * @throws SecurityException if IP address is suspicious
     */
    private void validateIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new SecurityException("IP address is required for security validation");
        }
        
        if (!IP_ADDRESS_PATTERN.matcher(ipAddress).matches()) {
            throw new SecurityException("Invalid IP address format: " + ipAddress);
        }
        
        // Check for suspicious IP ranges
        if (SUSPICIOUS_IP_PATTERN.matcher(ipAddress).matches()) {
            logger.warn("Transaction from private IP range: {}", ipAddress);
            // In production, this might trigger additional verification
        }
        
        // Check for known malicious IPs (simplified)
        if (isKnownMaliciousIp(ipAddress)) {
            throw new SecurityException("IP address is flagged as malicious: " + ipAddress);
        }
    }
    
    /**
     * Validates the user agent string.
     * 
     * @param userAgent the user agent to validate
     * @throws SecurityException if user agent is suspicious
     */
    private void validateUserAgent(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            throw new SecurityException("User agent is required for security validation");
        }
        
        // Check for suspicious user agents
        String lowerUserAgent = userAgent.toLowerCase();
        if (lowerUserAgent.contains("bot") || 
            lowerUserAgent.contains("crawler") || 
            lowerUserAgent.contains("spider")) {
            throw new SecurityException("Automated access detected: " + userAgent);
        }
        
        // Check for common browser patterns
        if (!isValidBrowserUserAgent(userAgent)) {
            logger.warn("Unusual user agent detected: {}", userAgent);
        }
    }
    
    /**
     * Validates the session ID.
     * 
     * @param sessionId the session ID to validate
     * @throws SecurityException if session is invalid
     */
    private void validateSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new SecurityException("Session ID is required for security validation");
        }
        
        // Check if session is expired
        if (isSessionExpired(sessionId)) {
            throw new SecurityException("Session has expired");
        }
        
        // Check if session is valid
        if (!isValidSession(sessionId)) {
            throw new SecurityException("Invalid session ID");
        }
    }
    
    /**
     * Validates the transaction amount for security concerns.
     * 
     * @param request the transaction request
     * @throws SecurityException if amount is suspicious
     */
    private void validateTransactionAmount(TransactionRequest request) {
        // Check for unusually large amounts
        if (request.getAmount().compareTo(java.math.BigDecimal.valueOf(50000)) > 0) {
            logger.warn("Large transaction amount detected: {}", request.getAmount());
            // In production, this would trigger additional verification
        }
        
        // Check for suspicious amount patterns (e.g., round numbers)
        if (isSuspiciousAmount(request.getAmount())) {
            logger.warn("Suspicious transaction amount pattern: {}", request.getAmount());
        }
    }
    
    /**
     * Validates the approval code if required.
     * 
     * @param request the transaction request
     * @throws SecurityException if approval code is invalid
     */
    private void validateApprovalCode(TransactionRequest request) {
        if (request.requiresApprovalCode()) {
            if (request.getApprovalCode() == null || request.getApprovalCode().trim().isEmpty()) {
                throw new SecurityException("Approval code required for large urgent transactions");
            }
            
            if (!isValidApprovalCode(request.getApprovalCode())) {
                throw new SecurityException("Invalid approval code");
            }
        }
    }
    
    /**
     * Checks for suspicious activity patterns.
     * 
     * @param request the transaction request
     * @throws SecurityException if suspicious activity is detected
     */
    private void checkForSuspiciousActivity(TransactionRequest request) {
        // Check for rapid successive transactions
        if (hasRapidTransactions(request.getUserId())) {
            throw new SecurityException("Rapid transaction pattern detected");
        }
        
        // Check for unusual transaction times
        if (isUnusualTransactionTime()) {
            logger.warn("Transaction at unusual time for user: {}", request.getUserId());
        }
        
        // Check for geographic anomalies
        if (hasGeographicAnomaly(request.getIpAddress(), request.getUserId())) {
            logger.warn("Geographic anomaly detected for user: {}", request.getUserId());
        }
    }
    
    /**
     * Checks if an IP address is known to be malicious.
     * 
     * @param ipAddress the IP address to check
     * @return true if the IP is malicious
     */
    private boolean isKnownMaliciousIp(String ipAddress) {
        // In production, this would query a threat intelligence service
        // For demo purposes, we'll use a simple check
        return "192.168.1.100".equals(ipAddress) || "10.0.0.50".equals(ipAddress);
    }
    
    /**
     * Checks if a user agent string represents a valid browser.
     * 
     * @param userAgent the user agent to check
     * @return true if it's a valid browser user agent
     */
    private boolean isValidBrowserUserAgent(String userAgent) {
        String lowerUserAgent = userAgent.toLowerCase();
        return lowerUserAgent.contains("mozilla") || 
               lowerUserAgent.contains("chrome") || 
               lowerUserAgent.contains("safari") || 
               lowerUserAgent.contains("firefox") || 
               lowerUserAgent.contains("edge");
    }
    
    /**
     * Checks if a session has expired.
     * 
     * @param sessionId the session ID to check
     * @return true if the session has expired
     */
    private boolean isSessionExpired(String sessionId) {
        // In production, this would check against a session store
        // For demo purposes, we'll use a simple check
        return sessionId.length() < 10;
    }
    
    /**
     * Checks if a session ID is valid.
     * 
     * @param sessionId the session ID to check
     * @return true if the session is valid
     */
    private boolean isValidSession(String sessionId) {
        // In production, this would validate against a session store
        // For demo purposes, we'll use a simple check
        return sessionId.matches("^[a-zA-Z0-9]{20,}$");
    }
    
    /**
     * Checks if an amount has a suspicious pattern.
     * 
     * @param amount the amount to check
     * @return true if the amount is suspicious
     */
    private boolean isSuspiciousAmount(java.math.BigDecimal amount) {
        // Check for round numbers (common in test transactions)
        return amount.remainder(java.math.BigDecimal.valueOf(1000))
                    .compareTo(java.math.BigDecimal.ZERO) == 0;
    }
    
    /**
     * Checks if an approval code is valid.
     * 
     * @param approvalCode the approval code to check
     * @return true if the approval code is valid
     */
    private boolean isValidApprovalCode(String approvalCode) {
        // In production, this would validate against an approval system
        // For demo purposes, we'll use a simple check
        return approvalCode.matches("^[A-Z0-9]{6,}$");
    }
    
    /**
     * Checks if a user has made rapid successive transactions.
     * 
     * @param userId the user ID to check
     * @return true if rapid transactions are detected
     */
    private boolean hasRapidTransactions(String userId) {
        // In production, this would check transaction history
        // For demo purposes, we'll use a simple check
        return false;
    }
    
    /**
     * Checks if the current time is unusual for transactions.
     * 
     * @return true if the time is unusual
     */
    private boolean isUnusualTransactionTime() {
        // In production, this would check against user's normal activity patterns
        // For demo purposes, we'll use a simple check
        return false;
    }
    
    /**
     * Checks for geographic anomalies in user activity.
     * 
     * @param ipAddress the IP address
     * @param userId the user ID
     * @return true if a geographic anomaly is detected
     */
    private boolean hasGeographicAnomaly(String ipAddress, String userId) {
        // In production, this would check against user's normal geographic patterns
        // For demo purposes, we'll use a simple check
        return false;
    }
} 