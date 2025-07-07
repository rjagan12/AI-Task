package com.lms.user.service;

import com.lms.user.dto.request.LoginRequest;
import com.lms.user.dto.request.RegisterRequest;
import com.lms.user.dto.response.AuthResponse;
import com.lms.user.entity.User;
import com.lms.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("test@example.com", "encodedPassword", "John Doe", "STUDENT");
        testUser.setId(UUID.randomUUID());
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("John Doe");
        registerRequest.setRole("STUDENT");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        // Set JWT secret for tests using reflection
        java.lang.reflect.Field jwtSecretField = UserService.class.getDeclaredField("jwtSecret");
        jwtSecretField.setAccessible(true);
        jwtSecretField.set(userService, "test-jwt-secret-key-for-unit-tests-only");
        
        java.lang.reflect.Field jwtExpirationField = UserService.class.getDeclaredField("jwtExpiration");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(userService, 86400L);
    }

    @Test
    void register_ValidRequest_ReturnsAuthResponse() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

        // When
        Mono<AuthResponse> result = userService.register(registerRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getToken() != null &&
                    response.getRefreshToken() != null &&
                    response.getEmail().equals("test@example.com") &&
                    response.getFullName().equals("John Doe") &&
                    response.getRole().equals("STUDENT"))
                .verifyComplete();
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));

        // When
        Mono<AuthResponse> result = userService.register(registerRequest);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        Mono<AuthResponse> result = userService.login(loginRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getToken() != null &&
                    response.getRefreshToken() != null &&
                    response.getEmail().equals("test@example.com") &&
                    response.getFullName().equals("John Doe") &&
                    response.getRole().equals("STUDENT"))
                .verifyComplete();
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When
        Mono<AuthResponse> result = userService.login(loginRequest);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        // When
        Mono<AuthResponse> result = userService.login(loginRequest);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getUserById_ValidId_ReturnsUser() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));

        // When
        Mono<User> result = userService.getUserById(userId);

        // Then
        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void getUserById_InvalidId_ThrowsException() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        // When
        Mono<User> result = userService.getUserById(userId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getUserByEmail_ValidEmail_ReturnsUser() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(testUser));

        // When
        Mono<User> result = userService.getUserByEmail("test@example.com");

        // Then
        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void getUserByEmail_InvalidEmail_ThrowsException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        // When
        Mono<User> result = userService.getUserByEmail("nonexistent@example.com");

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void updateUser_ValidUser_ReturnsUpdatedUser() {
        // Given
        UUID userId = UUID.randomUUID();
        User updatedUser = new User("updated@example.com", "password", "Updated Name", "INSTRUCTOR");
        updatedUser.setId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));

        // When
        Mono<User> result = userService.updateUser(userId, updatedUser);

        // Then
        StepVerifier.create(result)
                .expectNext(updatedUser)
                .verifyComplete();
    }

    @Test
    void deleteUser_ValidId_ReturnsVoid() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        when(userRepository.deleteById(userId)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = userService.deleteUser(userId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }
} 