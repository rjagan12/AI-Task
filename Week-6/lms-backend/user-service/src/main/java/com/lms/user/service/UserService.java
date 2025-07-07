package com.lms.user.service;

import com.lms.user.dto.request.LoginRequest;
import com.lms.user.dto.request.RegisterRequest;
import com.lms.user.dto.response.AuthResponse;
import com.lms.user.entity.User;
import com.lms.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.secret:defaultSecretKey}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400}")
    private long jwtExpiration;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Email already exists"));
                    }
                    
                    String encodedPassword = passwordEncoder.encode(request.getPassword());
                    User user = new User(request.getEmail(), encodedPassword, 
                                       request.getFullName(), request.getRole());
                    
                    return userRepository.save(user)
                            .map(savedUser -> generateAuthResponse(savedUser));
                });
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                        return Mono.just(generateAuthResponse(user));
                    } else {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                });
    }

    public Mono<User> getUserById(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    public Mono<User> updateUser(UUID userId, User updatedUser) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(existingUser -> {
                    existingUser.setFullName(updatedUser.getFullName());
                    existingUser.setProfilePicture(updatedUser.getProfilePicture());
                    existingUser.setPreferences(updatedUser.getPreferences());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                });
    }

    public Mono<Void> deleteUser(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> userRepository.deleteById(userId))
                .then();
    }

    private AuthResponse generateAuthResponse(User user) {
        String token = generateJwtToken(user);
        String refreshToken = generateRefreshToken(user);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtExpiration);
        
        return new AuthResponse(token, refreshToken, user.getId().toString(), 
                              user.getEmail(), user.getFullName(), user.getRole(), expiresAt);
    }

    private String generateJwtToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (jwtExpiration * 1000)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (jwtExpiration * 24 * 1000))) // 24 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
} 