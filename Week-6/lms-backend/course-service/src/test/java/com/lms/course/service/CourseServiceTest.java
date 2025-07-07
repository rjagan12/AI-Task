package com.lms.course.service;

import com.lms.course.dto.request.CreateCourseRequest;
import com.lms.course.dto.response.CourseResponse;
import com.lms.course.entity.Course;
import com.lms.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private CreateCourseRequest createCourseRequest;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setInstructorId(1L);
        testCourse.setCategory("Programming");
        testCourse.setLevel("BEGINNER");

        createCourseRequest = new CreateCourseRequest();
        createCourseRequest.setTitle("Test Course");
        createCourseRequest.setDescription("Test Description");
        createCourseRequest.setInstructorId(1L);
        createCourseRequest.setCategory("Programming");
        createCourseRequest.setLevel("BEGINNER");
    }

    @Test
    void createCourse_ValidRequest_ReturnsCourseResponse() {
        // Given
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        CourseResponse response = courseService.createCourse(createCourseRequest);

        // Then
        assertNotNull(response);
        assertEquals(testCourse.getId(), response.getId());
        assertEquals(testCourse.getTitle(), response.getTitle());
        assertEquals(testCourse.getDescription(), response.getDescription());
        assertEquals(testCourse.getInstructorId(), response.getInstructorId());
        assertEquals(testCourse.getCategory(), response.getCategory());
        assertEquals(testCourse.getLevel(), response.getLevel());
    }

    @Test
    void getCourseById_ValidId_ReturnsCourseResponse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        CourseResponse response = courseService.getCourseById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testCourse.getId(), response.getId());
        assertEquals(testCourse.getTitle(), response.getTitle());
    }

    @Test
    void getCourseById_InvalidId_ThrowsException() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> courseService.getCourseById(999L));
    }

    @Test
    void getAllCourses_ReturnsCourseList() {
        // Given
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");
        course1.setDescription("Description 1");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Course 2");
        course2.setDescription("Description 2");

        List<Course> courses = Arrays.asList(course1, course2);
        when(courseRepository.findAll()).thenReturn(courses);

        // When
        List<CourseResponse> response = courseService.getAllCourses();

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Course 1", response.get(0).getTitle());
        assertEquals("Course 2", response.get(1).getTitle());
    }

    @Test
    void updateCourse_ValidRequest_ReturnsUpdatedCourse() {
        // Given
        CreateCourseRequest updateRequest = new CreateCourseRequest();
        updateRequest.setTitle("Updated Course");
        updateRequest.setDescription("Updated Description");
        updateRequest.setInstructorId(1L);
        updateRequest.setCategory("Programming");
        updateRequest.setLevel("INTERMEDIATE");

        Course updatedCourse = new Course();
        updatedCourse.setId(1L);
        updatedCourse.setTitle("Updated Course");
        updatedCourse.setDescription("Updated Description");
        updatedCourse.setInstructorId(1L);
        updatedCourse.setCategory("Programming");
        updatedCourse.setLevel("INTERMEDIATE");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // When
        CourseResponse response = courseService.updateCourse(1L, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("Updated Course", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertEquals("INTERMEDIATE", response.getLevel());
    }

    @Test
    void updateCourse_InvalidId_ThrowsException() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> courseService.updateCourse(999L, createCourseRequest));
    }

    @Test
    void deleteCourse_ValidId_ReturnsTrue() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        boolean result = courseService.deleteCourse(1L);

        // Then
        assertTrue(result);
    }

    @Test
    void deleteCourse_InvalidId_ReturnsFalse() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = courseService.deleteCourse(999L);

        // Then
        assertFalse(result);
    }

    @Test
    void getCoursesByInstructor_ValidInstructorId_ReturnsCourseList() {
        // Given
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");
        course1.setInstructorId(1L);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Course 2");
        course2.setInstructorId(1L);

        List<Course> courses = Arrays.asList(course1, course2);
        when(courseRepository.findByInstructorId(1L)).thenReturn(courses);

        // When
        List<CourseResponse> response = courseService.getCoursesByInstructor(1L);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getInstructorId());
        assertEquals(1L, response.get(1).getInstructorId());
    }
} 