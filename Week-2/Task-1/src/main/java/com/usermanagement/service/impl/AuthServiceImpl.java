package com.usermanagement.service.impl;

import com.usermanagement.dto.AuthRequest;
import com.usermanagement.dto.AuthResponse;
import com.usermanagement.entity.User;
import com.usermanagement.exception.AuthenticationException;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.security.JwtTokenUtil;
import com.usermanagement.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        logger.info("Authenticating user: {}", authRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setRefreshToken(refreshToken);
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRoles(user.getRoles());

            logger.info("User authenticated successfully: {}", authRequest.getUsername());
            return response;

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            throw new AuthenticationException("Invalid username or password");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refreshing token");

        try {
            if (jwtTokenUtil.validateToken(refreshToken)) {
                String username = jwtTokenUtil.extractUsername(refreshToken);
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new AuthenticationException("User not found"));

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream()
                                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                .collect(java.util.stream.Collectors.toList()))
                        .build();

                String newToken = jwtTokenUtil.generateToken(userDetails);
                String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

                AuthResponse response = new AuthResponse();
                response.setToken(newToken);
                response.setRefreshToken(newRefreshToken);
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setEmail(user.getEmail());
                response.setFullName(user.getFullName());
                response.setRoles(user.getRoles());

                logger.info("Token refreshed successfully for user: {}", username);
                return response;
            } else {
                throw new AuthenticationException("Invalid refresh token");
            }
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw new AuthenticationException("Token refresh failed");
        }
    }

    @Override
    public void logout(String token) {
        logger.info("Logging out user");
        // In a real application, you might want to blacklist the token
        // For now, we'll just log the logout action
        logger.info("User logged out successfully");
    }
} 