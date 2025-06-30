package com.example.bank;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * User management system with multiple security and performance issues
 */
public class UserManager {
    // Bad: storing passwords in plain text
    private static Map<String, String> userPasswords = new HashMap<>();
    
    // Bad: storing sensitive data in memory
    private static Map<String, String> userSSNs = new HashMap<>();
    
    // Bad: global variables
    private static List<String> loggedInUsers = new ArrayList<>();
    private static int failedLoginAttempts = 0;
    
    // Bad: hardcoded admin credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    
    public UserManager() {
        // Bad: initializing with hardcoded data
        userPasswords.put("user1", "password123");
        userPasswords.put("user2", "password456");
        userSSNs.put("user1", "123-45-6789");
        userSSNs.put("user2", "987-65-4321");
    }
    
    // Bad: method with multiple responsibilities and security issues
    public boolean authenticateUser(String username, String password, String ipAddress, 
                                  String userAgent, String sessionId, boolean rememberMe) {
        try {
            // Bad: SQL injection vulnerability
            String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            
            // Bad: logging sensitive information
            System.out.println("Login attempt - Username: " + username + ", Password: " + password + ", IP: " + ipAddress);
            
            // Bad: simple password comparison
            if (userPasswords.containsKey(username)) {
                if (userPasswords.get(username).equals(password)) {
                    loggedInUsers.add(username);
                    failedLoginAttempts = 0;
                    
                    // Bad: command injection vulnerability
                    if (rememberMe) {
                        String command = "echo 'Remember me set for " + username + "' >> /var/log/auth.log";
                        Runtime.getRuntime().exec(command);
                    }
                    
                    return true;
                }
            }
            
            failedLoginAttempts++;
            
            // Bad: blocking after too many attempts (DoS vulnerability)
            if (failedLoginAttempts > 5) {
                Thread.sleep(30000); // Block for 30 seconds
            }
            
            return false;
            
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            return false;
        }
    }
    
    // Bad: method that exposes sensitive data
    public String getUserSSN(String username) {
        return userSSNs.get(username);
    }
    
    // Bad: method that allows password changes without validation
    public void changePassword(String username, String newPassword) {
        userPasswords.put(username, newPassword);
        System.out.println("Password changed for user: " + username);
    }
    
    // Bad: method with performance issues
    public List<String> searchUsers(String searchTerm) {
        List<String> results = new ArrayList<>();
        
        // Bad: inefficient search algorithm
        for (String username : userPasswords.keySet()) {
            if (username.contains(searchTerm)) {
                results.add(username);
            }
        }
        
        // Bad: unnecessary sorting
        Collections.sort(results);
        
        return results;
    }
    
    // Bad: method that creates resource leaks
    public void backupUserData(String backupPath) {
        try {
            // Bad: hardcoded file path
            File backupFile = new File("/tmp/user_backup.txt");
            FileWriter writer = new FileWriter(backupFile);
            
            // Bad: writing sensitive data to file
            for (Map.Entry<String, String> entry : userPasswords.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            
            for (Map.Entry<String, String> entry : userSSNs.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            
            writer.close();
            
            // Bad: command injection vulnerability
            String command = "cp /tmp/user_backup.txt " + backupPath;
            Runtime.getRuntime().exec(command);
            
        } catch (Exception e) {
            System.out.println("Backup error: " + e.getMessage());
        }
    }
    
    // Bad: method that violates encapsulation
    public Map<String, String> getAllPasswords() {
        return userPasswords;
    }
    
    // Bad: method that allows direct manipulation
    public void addUser(String username, String password, String ssn) {
        userPasswords.put(username, password);
        userSSNs.put(username, ssn);
    }
} 