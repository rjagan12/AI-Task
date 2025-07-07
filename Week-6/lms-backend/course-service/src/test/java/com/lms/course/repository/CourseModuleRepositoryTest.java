package com.lms.course.repository;

import com.lms.course.entity.CourseModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@ActiveProfiles("test")
class CourseModuleRepositoryTest {

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    private CourseModule testModule;
    private UUID testModuleId;
    private UUID testCourseId;

    @BeforeEach
    void setUp() {
        testModuleId = UUID.randomUUID();
        testCourseId = UUID.randomUUID();
        testModule = new CourseModule();
        testModule.setId(testModuleId);
        testModule.setCourseId(testCourseId);
        testModule.setTitle("Test Module");
        testModule.setDescription("Test Module Description");
        testModule.setOrderIndex(1);
        testModule.setStatus("ACTIVE");
        testModule.setCreatedAt(LocalDateTime.now());
        testModule.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void save_ValidModule_ReturnsSavedModule() {
        // When
        Mono<CourseModule> savedModule = courseModuleRepository.save(testModule);

        // Then
        StepVerifier.create(savedModule)
                .assertNext(module -> {
                    assertNotNull(module.getId());
                    assertEquals(testCourseId, module.getCourseId());
                    assertEquals("Test Module", module.getTitle());
                    assertEquals("Test Module Description", module.getDescription());
                    assertEquals(1, module.getOrderIndex());
                    assertEquals("ACTIVE", module.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void findById_ExistingModule_ReturnsModule() {
        // Given
        courseModuleRepository.save(testModule).block();

        // When
        Mono<CourseModule> foundModule = courseModuleRepository.findById(testModuleId);

        // Then
        StepVerifier.create(foundModule)
                .assertNext(module -> {
                    assertEquals(testModuleId, module.getId());
                    assertEquals("Test Module", module.getTitle());
                    assertEquals("Test Module Description", module.getDescription());
                })
                .verifyComplete();
    }

    @Test
    void findById_NonExistingModule_ReturnsEmpty() {
        // When
        Mono<CourseModule> foundModule = courseModuleRepository.findById(UUID.randomUUID());

        // Then
        StepVerifier.create(foundModule)
                .verifyComplete();
    }

    @Test
    void findByCourseId_ExistingModules_ReturnsModules() {
        // Given
        courseModuleRepository.save(testModule).block();
        
        CourseModule secondModule = new CourseModule();
        secondModule.setId(UUID.randomUUID());
        secondModule.setCourseId(testCourseId);
        secondModule.setTitle("Second Module");
        secondModule.setDescription("Second Module Description");
        secondModule.setOrderIndex(2);
        secondModule.setStatus("ACTIVE");
        secondModule.setCreatedAt(LocalDateTime.now());
        secondModule.setUpdatedAt(LocalDateTime.now());
        courseModuleRepository.save(secondModule).block();

        // When
        Flux<CourseModule> foundModules = courseModuleRepository.findByCourseId(testCourseId);

        // Then
        StepVerifier.create(foundModules)
                .assertNext(module -> {
                    assertEquals(testCourseId, module.getCourseId());
                    assertTrue(module.getTitle().equals("Test Module") || module.getTitle().equals("Second Module"));
                })
                .assertNext(module -> {
                    assertEquals(testCourseId, module.getCourseId());
                    assertTrue(module.getTitle().equals("Test Module") || module.getTitle().equals("Second Module"));
                })
                .verifyComplete();
    }

    @Test
    void findByCourseId_NoModules_ReturnsEmpty() {
        // When
        Flux<CourseModule> foundModules = courseModuleRepository.findByCourseId(UUID.randomUUID());

        // Then
        StepVerifier.create(foundModules)
                .verifyComplete();
    }

    @Test
    void findByCourseIdOrderByOrderIndex_ReturnsOrderedModules() {
        // Given
        CourseModule firstModule = new CourseModule();
        firstModule.setId(UUID.randomUUID());
        firstModule.setCourseId(testCourseId);
        firstModule.setTitle("First Module");
        firstModule.setDescription("First Module Description");
        firstModule.setOrderIndex(1);
        firstModule.setStatus("ACTIVE");
        firstModule.setCreatedAt(LocalDateTime.now());
        firstModule.setUpdatedAt(LocalDateTime.now());
        courseModuleRepository.save(firstModule).block();

        CourseModule secondModule = new CourseModule();
        secondModule.setId(UUID.randomUUID());
        secondModule.setCourseId(testCourseId);
        secondModule.setTitle("Second Module");
        secondModule.setDescription("Second Module Description");
        secondModule.setOrderIndex(2);
        secondModule.setStatus("ACTIVE");
        secondModule.setCreatedAt(LocalDateTime.now());
        secondModule.setUpdatedAt(LocalDateTime.now());
        courseModuleRepository.save(secondModule).block();

        // When
        Flux<CourseModule> foundModules = courseModuleRepository.findByCourseIdOrderByOrderIndex(testCourseId);

        // Then
        StepVerifier.create(foundModules)
                .assertNext(module -> {
                    assertEquals("First Module", module.getTitle());
                    assertEquals(1, module.getOrderIndex());
                })
                .assertNext(module -> {
                    assertEquals("Second Module", module.getTitle());
                    assertEquals(2, module.getOrderIndex());
                })
                .verifyComplete();
    }

    @Test
    void deleteById_ExistingModule_DeletesModule() {
        // Given
        courseModuleRepository.save(testModule).block();

        // When
        Mono<Void> deleteResult = courseModuleRepository.deleteById(testModuleId);

        // Then
        StepVerifier.create(deleteResult)
                .verifyComplete();

        // Verify module is deleted
        StepVerifier.create(courseModuleRepository.findById(testModuleId))
                .verifyComplete();
    }

    @Test
    void deleteById_NonExistingModule_CompletesWithoutError() {
        // When
        Mono<Void> deleteResult = courseModuleRepository.deleteById(UUID.randomUUID());

        // Then
        StepVerifier.create(deleteResult)
                .verifyComplete();
    }

    @Test
    void save_UpdateExistingModule_UpdatesModule() {
        // Given
        courseModuleRepository.save(testModule).block();
        
        CourseModule updatedModule = new CourseModule();
        updatedModule.setId(testModuleId);
        updatedModule.setCourseId(testCourseId);
        updatedModule.setTitle("Updated Module");
        updatedModule.setDescription("Updated Module Description");
        updatedModule.setOrderIndex(3);
        updatedModule.setStatus("INACTIVE");
        updatedModule.setCreatedAt(LocalDateTime.now());
        updatedModule.setUpdatedAt(LocalDateTime.now());

        // When
        Mono<CourseModule> savedModule = courseModuleRepository.save(updatedModule);

        // Then
        StepVerifier.create(savedModule)
                .assertNext(module -> {
                    assertEquals(testModuleId, module.getId());
                    assertEquals("Updated Module", module.getTitle());
                    assertEquals("Updated Module Description", module.getDescription());
                    assertEquals(3, module.getOrderIndex());
                    assertEquals("INACTIVE", module.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void countByCourseId_ExistingModules_ReturnsCount() {
        // Given
        courseModuleRepository.save(testModule).block();
        
        CourseModule secondModule = new CourseModule();
        secondModule.setId(UUID.randomUUID());
        secondModule.setCourseId(testCourseId);
        secondModule.setTitle("Second Module");
        secondModule.setDescription("Second Module Description");
        secondModule.setOrderIndex(2);
        secondModule.setStatus("ACTIVE");
        secondModule.setCreatedAt(LocalDateTime.now());
        secondModule.setUpdatedAt(LocalDateTime.now());
        courseModuleRepository.save(secondModule).block();

        // When
        Mono<Long> count = courseModuleRepository.countByCourseId(testCourseId);

        // Then
        StepVerifier.create(count)
                .assertNext(countResult -> assertEquals(2L, countResult))
                .verifyComplete();
    }

    @Test
    void countByCourseId_NoModules_ReturnsZero() {
        // When
        Mono<Long> count = courseModuleRepository.countByCourseId(UUID.randomUUID());

        // Then
        StepVerifier.create(count)
                .assertNext(countResult -> assertEquals(0L, countResult))
                .verifyComplete();
    }
} 