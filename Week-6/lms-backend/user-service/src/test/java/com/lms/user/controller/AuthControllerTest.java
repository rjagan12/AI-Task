package com.lms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.user.dto.request.LoginRequest;
import com.lms.user.dto.request.RegisterRequest;
import com.lms.user.dto.response.AuthResponse;
import com.lms.user.entity.User;
import com.lms.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebFluxTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ValidRequest_ReturnsAuthResponse() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("John Doe");
        request.setRole("STUDENT");

        User user = new User("test@example.com", "encodedPassword", "John Doe", "STUDENT");
        user.setId(UUID.randomUUID());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("jwt-token");
        authResponse.setRefreshToken("refresh-token");
        authResponse.setUserId(user.getId().toString());
        authResponse.setEmail("test@example.com");
        authResponse.setFullName("John Doe");
        authResponse.setRole("STUDENT");
        authResponse.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(userService.register(any(RegisterRequest.class))).thenReturn(Mono.just(authResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.token").isEqualTo("jwt-token")
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.fullName").isEqualTo("John Doe");
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User("test@example.com", "encodedPassword", "John Doe", "STUDENT");
        user.setId(UUID.randomUUID());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("jwt-token");
        authResponse.setRefreshToken("refresh-token");
        authResponse.setUserId(user.getId().toString());
        authResponse.setEmail("test@example.com");
        authResponse.setFullName("John Doe");
        authResponse.setRole("STUDENT");
        authResponse.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(userService.login(any(LoginRequest.class))).thenReturn(Mono.just(authResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("jwt-token")
                .jsonPath("$.email").isEqualTo("test@example.com");
    }

    @Test
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("");

        // When & Then
        webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(userService.login(any(LoginRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Invalid credentials")));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }
} 