package com.usermanagement.service;

import com.usermanagement.dto.AuthRequest;
import com.usermanagement.dto.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest authRequest);
    
    AuthResponse refreshToken(String refreshToken);
    
    void logout(String token);
} 