package com.usermanagement.service;

import com.usermanagement.dto.UserRequest;
import com.usermanagement.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);
    
    UserResponse updateUser(Long id, UserRequest userRequest);
    
    UserResponse getUserById(Long id);
    
    UserResponse getUserByUsername(String username);
    
    UserResponse getUserByEmail(String email);
    
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    List<UserResponse> getActiveUsers();
    
    List<UserResponse> getInactiveUsers();
    
    List<UserResponse> searchUsersByName(String name);
    
    List<UserResponse> searchUsersByEmail(String email);
    
    List<UserResponse> getUsersByRole(String role);
    
    void deleteUser(Long id);
    
    void deactivateUser(Long id);
    
    void activateUser(Long id);
    
    void addRoleToUser(Long userId, String role);
    
    void removeRoleFromUser(Long userId, String role);
    
    long getActiveUserCount();
    
    long getInactiveUserCount();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
} 