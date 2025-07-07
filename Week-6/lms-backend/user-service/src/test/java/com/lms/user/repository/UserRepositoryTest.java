package com.lms.user.repository;

import com.lms.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User("test@example.com", "encodedPassword", "John Doe", "STUDENT");
        testUser.setId(testUserId);
    }

    @Test
    void save_ValidUser_ReturnsSavedUser() {
        // When
        Mono<User> savedUser = userRepository.save(testUser);

        // Then
        StepVerifier.create(savedUser)
                .assertNext(user -> {
                    assertNotNull(user.getId());
                    assertEquals("test@example.com", user.getEmail());
                    assertEquals("John Doe", user.getFullName());
                    assertEquals("STUDENT", user.getRole());
                })
                .verifyComplete();
    }

    @Test
    void findById_ExistingUser_ReturnsUser() {
        // Given
        userRepository.save(testUser).block();

        // When
        Mono<User> foundUser = userRepository.findById(testUserId);

        // Then
        StepVerifier.create(foundUser)
                .assertNext(user -> {
                    assertEquals(testUserId, user.getId());
                    assertEquals("test@example.com", user.getEmail());
                    assertEquals("John Doe", user.getFullName());
                })
                .verifyComplete();
    }

    @Test
    void findById_NonExistingUser_ReturnsEmpty() {
        // When
        Mono<User> foundUser = userRepository.findById(UUID.randomUUID());

        // Then
        StepVerifier.create(foundUser)
                .verifyComplete();
    }

    @Test
    void findByEmail_ExistingUser_ReturnsUser() {
        // Given
        userRepository.save(testUser).block();

        // When
        Mono<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        StepVerifier.create(foundUser)
                .assertNext(user -> {
                    assertEquals("test@example.com", user.getEmail());
                    assertEquals("John Doe", user.getFullName());
                    assertEquals("STUDENT", user.getRole());
                })
                .verifyComplete();
    }

    @Test
    void findByEmail_NonExistingUser_ReturnsEmpty() {
        // When
        Mono<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        StepVerifier.create(foundUser)
                .verifyComplete();
    }

    @Test
    void existsByEmail_ExistingUser_ReturnsTrue() {
        // Given
        userRepository.save(testUser).block();

        // When
        Mono<Boolean> exists = userRepository.existsByEmail("test@example.com");

        // Then
        StepVerifier.create(exists)
                .assertNext(existsResult -> assertTrue(existsResult))
                .verifyComplete();
    }

    @Test
    void existsByEmail_NonExistingUser_ReturnsFalse() {
        // When
        Mono<Boolean> exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        StepVerifier.create(exists)
                .assertNext(existsResult -> assertFalse(existsResult))
                .verifyComplete();
    }

    @Test
    void deleteById_ExistingUser_DeletesUser() {
        // Given
        userRepository.save(testUser).block();

        // When
        Mono<Void> deleteResult = userRepository.deleteById(testUserId);

        // Then
        StepVerifier.create(deleteResult)
                .verifyComplete();

        // Verify user is deleted
        StepVerifier.create(userRepository.findById(testUserId))
                .verifyComplete();
    }

    @Test
    void deleteById_NonExistingUser_CompletesWithoutError() {
        // When
        Mono<Void> deleteResult = userRepository.deleteById(UUID.randomUUID());

        // Then
        StepVerifier.create(deleteResult)
                .verifyComplete();
    }

    @Test
    void save_UpdateExistingUser_UpdatesUser() {
        // Given
        userRepository.save(testUser).block();
        
        User updatedUser = new User("updated@example.com", "newPassword", "Jane Doe", "INSTRUCTOR");
        updatedUser.setId(testUserId);

        // When
        Mono<User> savedUser = userRepository.save(updatedUser);

        // Then
        StepVerifier.create(savedUser)
                .assertNext(user -> {
                    assertEquals(testUserId, user.getId());
                    assertEquals("updated@example.com", user.getEmail());
                    assertEquals("Jane Doe", user.getFullName());
                    assertEquals("INSTRUCTOR", user.getRole());
                })
                .verifyComplete();
    }
} 