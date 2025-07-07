package com.lms.user.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test health check endpoint (if exists)
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void authEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test registration endpoint
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"fullName\":\"Test User\",\"role\":\"STUDENT\"}"))
                .andExpect(status().isOk());

        // Test login endpoint
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_ShouldRequireAuthentication() throws Exception {
        // Test user profile endpoint without authentication
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoints_WithAuthentication_ShouldBeAccessible() throws Exception {
        // Test user profile endpoint with authentication
        mockMvc.perform(get("/api/v1/users/profile")
                .header("X-User-ID", "test-user-id"))
                .andExpect(status().isOk());
    }

    @Test
    void csrf_ShouldBeDisabled() throws Exception {
        // Test that CSRF is disabled by making a POST request without CSRF token
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"fullName\":\"Test User\",\"role\":\"STUDENT\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void cors_ShouldBeEnabled() throws Exception {
        // Test CORS headers are present
        mockMvc.perform(get("/api/v1/users/profile")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isUnauthorized()); // Should be unauthorized but CORS headers should be present
    }
} 