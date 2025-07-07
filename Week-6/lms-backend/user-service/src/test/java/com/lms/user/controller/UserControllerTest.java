package com.lms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.user.entity.User;
import com.lms.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebFluxTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;
    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testUserId = UUID.randomUUID();
        testUser = new User("test@example.com", "encodedPassword", "John Doe", "STUDENT");
        testUser.setId(testUserId);
    }

    @Test
    void getUserById_ValidId_ReturnsUser() throws Exception {
        // Given
        when(userService.getUserById(testUserId)).thenReturn(Mono.just(testUser));

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void getUserById_InvalidId_ReturnsNotFound() throws Exception {
        // Given
        when(userService.getUserById(any(UUID.class)))
                .thenReturn(Mono.error(new RuntimeException("User not found")));

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_ValidEmail_ReturnsUser() throws Exception {
        // Given
        String email = "test@example.com";
        when(userService.getUserByEmail(email)).thenReturn(Mono.just(testUser));

        // When & Then
        mockMvc.perform(get("/api/v1/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void getUserByEmail_InvalidEmail_ReturnsNotFound() throws Exception {
        // Given
        String email = "nonexistent@example.com";
        when(userService.getUserByEmail(email))
                .thenReturn(Mono.error(new RuntimeException("User not found")));

        // When & Then
        mockMvc.perform(get("/api/v1/users/email/{email}", email))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ValidRequest_ReturnsUpdatedUser() throws Exception {
        // Given
        User updatedUser = new User("updated@example.com", "newPassword", "Jane Doe", "INSTRUCTOR");
        updatedUser.setId(testUserId);
        
        when(userService.updateUser(eq(testUserId), any(User.class)))
                .thenReturn(Mono.just(updatedUser));

        // When & Then
        mockMvc.perform(put("/api/v1/users/{userId}", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    void updateUser_InvalidId_ReturnsNotFound() throws Exception {
        // Given
        when(userService.updateUser(any(UUID.class), any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("User not found")));

        // When & Then
        mockMvc.perform(put("/api/v1/users/{userId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ValidId_ReturnsNoContent() throws Exception {
        // Given
        when(userService.deleteUser(testUserId)).thenReturn(Mono.empty());

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}", testUserId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_InvalidId_ReturnsNotFound() throws Exception {
        // Given
        when(userService.deleteUser(any(UUID.class)))
                .thenReturn(Mono.error(new RuntimeException("User not found")));

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCurrentUserProfile_ValidUserId_ReturnsUser() throws Exception {
        // Given
        when(userService.getUserById(testUserId)).thenReturn(Mono.just(testUser));

        // When & Then
        mockMvc.perform(get("/api/v1/users/profile")
                .header("X-User-ID", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getCurrentUserProfile_InvalidUserId_ReturnsNotFound() throws Exception {
        // Given
        when(userService.getUserById(any(UUID.class)))
                .thenReturn(Mono.error(new RuntimeException("User not found")));

        // When & Then
        mockMvc.perform(get("/api/v1/users/profile")
                .header("X-User-ID", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCurrentUserProfile_MissingHeader_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isNotFound());
    }
} 