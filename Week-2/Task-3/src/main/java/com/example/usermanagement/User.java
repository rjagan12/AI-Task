package com.example.usermanagement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Modern User entity using Java records for immutability and better encapsulation.
 * Replaces the legacy UserData class with proper data modeling.
 */
public record User(
    Long id,
    String name,
    String email,
    String passwordHash,
    String address,
    String phone,
    LocalDate birthDate,
    UserRole role,
    boolean isActive,
    LocalDateTime createdDate,
    Optional<LocalDateTime> lastLoginDate,
    int loginCount,
    Optional<String> preferences,
    Optional<String> notes
) {
    
    /**
     * Creates a new user with default values for optional fields.
     */
    public static User createNew(String name, String email, String passwordHash, 
                               String address, String phone, LocalDate birthDate, UserRole role) {
        return new User(
            null, // ID will be set by database
            name,
            email,
            passwordHash,
            address,
            phone,
            birthDate,
            role,
            true, // Default to active
            LocalDateTime.now(),
            Optional.empty(),
            0,
            Optional.empty(),
            Optional.empty()
        );
    }
    
    /**
     * Updates the user's last login information.
     */
    public User withLoginUpdate() {
        return new User(
            id, name, email, passwordHash, address, phone, birthDate, role, isActive,
            createdDate, Optional.of(LocalDateTime.now()), loginCount + 1, preferences, notes
        );
    }
    
    /**
     * Updates the user's profile information.
     */
    public User withProfileUpdate(String name, String address, String phone) {
        return new User(
            id, name, email, passwordHash, address, phone, birthDate, role, isActive,
            createdDate, lastLoginDate, loginCount, preferences, notes
        );
    }
} 