package com.example.usermanagement;

import java.util.Optional;

/**
 * Enumeration of user roles in the system.
 * Provides type safety and prevents invalid role assignments.
 */
public enum UserRole {
    USER("user"),
    ADMIN("admin"),
    MODERATOR("moderator");
    
    private final String value;
    
    UserRole(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Converts a string value to UserRole enum.
     * @param value the string value to convert
     * @return Optional containing the UserRole if valid, empty otherwise
     */
    public static Optional<UserRole> fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }
        
        for (UserRole role : values()) {
            if (role.value.equalsIgnoreCase(value.trim())) {
                return Optional.of(role);
            }
        }
        return Optional.empty();
    }
} 