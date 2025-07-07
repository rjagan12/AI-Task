package com.lms.course.repository;

import com.lms.course.entity.Course;
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
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    private Course testCourse;
    private UUID testCourseId;
    private UUID testInstructorId;

    @BeforeEach
    void setUp() {
        testCourseId = UUID.randomUUID();
        testInstructorId = UUID.randomUUID();
        testCourse = new Course();
        testCourse.setId(testCourseId);
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setInstructorId(testInstructorId);
        testCourse.setStatus("ACTIVE");
        testCourse.setCreatedAt(LocalDateTime.now());
        testCourse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void save_ValidCourse_ReturnsSavedCourse() {
        // When
        Mono<Course> savedCourse = courseRepository.save(testCourse);

        // Then
        StepVerifier.create(savedCourse)
                .assertNext(course -> {
                    assertNotNull(course.getId());
                    assertEquals("Test Course", course.getTitle());
                    assertEquals("Test Description", course.getDescription());
                    assertEquals(testInstructorId, course.getInstructorId());
                    assertEquals("ACTIVE", course.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void findById_ExistingCourse_ReturnsCourse() {
        // Given
        courseRepository.save(testCourse).block();

        // When
        Mono<Course> foundCourse = courseRepository.findById(testCourseId);

        // Then
        StepVerifier.create(foundCourse)
                .assertNext(course -> {
                    assertEquals(testCourseId, course.getId());
                    assertEquals("Test Course", course.getTitle());
                    assertEquals("Test Description", course.getDescription());
                })
                .verifyComplete();
    }

    @Test
    void findById_NonExistingCourse_ReturnsEmpty() {
        // When
        Mono<Course> foundCourse = courseRepository.findById(UUID.randomUUID());

        // Then
        StepVerifier.create(foundCourse)
                .verifyComplete();
    }

    @Test
    void findByInstructorId_ExistingCourses_ReturnsCourses() {
        // Given
        courseRepository.save(testCourse).block();
        
        Course secondCourse = new Course();
        secondCourse.setId(UUID.randomUUID());
        secondCourse.setTitle("Second Course");
        secondCourse.setDescription("Second Description");
        secondCourse.setInstructorId(testInstructorId);
        secondCourse.setStatus("ACTIVE");
        secondCourse.setCreatedAt(LocalDateTime.now());
        secondCourse.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(secondCourse).block();

        // When
        Flux<Course> foundCourses = courseRepository.findByInstructorId(testInstructorId);

        // Then
        StepVerifier.create(foundCourses)
                .assertNext(course -> {
                    assertEquals(testInstructorId, course.getInstructorId());
                    assertTrue(course.getTitle().equals("Test Course") || course.getTitle().equals("Second Course"));
                })
                .assertNext(course -> {
                    assertEquals(testInstructorId, course.getInstructorId());
                    assertTrue(course.getTitle().equals("Test Course") || course.getTitle().equals("Second Course"));
                })
                .verifyComplete();
    }

    @Test
    void findByInstructorId_NoCourses_ReturnsEmpty() {
        // When
        Flux<Course> foundCourses = courseRepository.findByInstructorId(UUID.randomUUID());

        // Then
        StepVerifier.create(foundCourses)
                .verifyComplete();
    }

    @Test
    void findByStatus_ActiveCourses_ReturnsCourses() {
        // Given
        courseRepository.save(testCourse).block();

        // When
        Flux<Course> foundCourses = courseRepository.findByStatus("ACTIVE");

        // Then
        StepVerifier.create(foundCourses)
                .assertNext(course -> {
                    assertEquals("ACTIVE", course.getStatus());
                    assertEquals("Test Course", course.getTitle());
                })
                .verifyComplete();
    }

    @Test
    void findByStatus_NoActiveCourses_ReturnsEmpty() {
        // When
        Flux<Course> foundCourses = courseRepository.findByStatus("ACTIVE");

        // Then
        StepVerifier.create(foundCourses)
                .verifyComplete();
    }

    @Test
    void deleteById_ExistingCourse_DeletesCourse() {
        // Given
        courseRepository.save(testCourse).block();

        // When
        Mono<Void> deleteResult = courseRepository.deleteById(testCourseId);

        // Then
        StepVerifier.create(deleteResult)
                .verifyComplete();

        // Verify course is deleted
        StepVerifier.create(courseRepository.findById(testCourseId))
                .verifyComplete();
    }

    @Test
    void deleteById_NonExistingCourse_CompletesWithoutError() {
        // When
        Mono<Void> deleteResult = courseRepository.deleteById(UUID.randomUUID());

        // Then
        StepVerifier.create(deleteResult)
                .verifyComplete();
    }

    @Test
    void save_UpdateExistingCourse_UpdatesCourse() {
        // Given
        courseRepository.save(testCourse).block();
        
        Course updatedCourse = new Course();
        updatedCourse.setId(testCourseId);
        updatedCourse.setTitle("Updated Course");
        updatedCourse.setDescription("Updated Description");
        updatedCourse.setInstructorId(testInstructorId);
        updatedCourse.setStatus("INACTIVE");
        updatedCourse.setCreatedAt(LocalDateTime.now());
        updatedCourse.setUpdatedAt(LocalDateTime.now());

        // When
        Mono<Course> savedCourse = courseRepository.save(updatedCourse);

        // Then
        StepVerifier.create(savedCourse)
                .assertNext(course -> {
                    assertEquals(testCourseId, course.getId());
                    assertEquals("Updated Course", course.getTitle());
                    assertEquals("Updated Description", course.getDescription());
                    assertEquals("INACTIVE", course.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void findAll_ReturnsAllCourses() {
        // Given
        courseRepository.save(testCourse).block();
        
        Course secondCourse = new Course();
        secondCourse.setId(UUID.randomUUID());
        secondCourse.setTitle("Second Course");
        secondCourse.setDescription("Second Description");
        secondCourse.setInstructorId(UUID.randomUUID());
        secondCourse.setStatus("ACTIVE");
        secondCourse.setCreatedAt(LocalDateTime.now());
        secondCourse.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(secondCourse).block();

        // When
        Flux<Course> allCourses = courseRepository.findAll();

        // Then
        StepVerifier.create(allCourses)
                .expectNextCount(2)
                .verifyComplete();
    }
} 