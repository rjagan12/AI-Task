package com.lms.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.course.dto.request.CreateCourseRequest;
import com.lms.course.dto.response.CourseResponse;
import com.lms.course.entity.Course;
import com.lms.course.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void createCourse_ValidRequest_ReturnsCourseResponse() throws Exception {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Test Course");
        request.setDescription("Test Description");
        request.setInstructorId(1L);
        request.setCategory("Programming");
        request.setLevel("BEGINNER");

        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        course.setInstructorId(1L);
        course.setCategory("Programming");
        course.setLevel("BEGINNER");

        CourseResponse response = new CourseResponse();
        response.setId(1L);
        response.setTitle("Test Course");
        response.setDescription("Test Description");
        response.setInstructorId(1L);
        response.setCategory("Programming");
        response.setLevel("BEGINNER");

        when(courseService.createCourse(any(CreateCourseRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Course"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.instructorId").value(1))
                .andExpect(jsonPath("$.category").value("Programming"))
                .andExpect(jsonPath("$.level").value("BEGINNER"));
    }

    @Test
    void getCourse_ValidId_ReturnsCourseResponse() throws Exception {
        // Given
        CourseResponse response = new CourseResponse();
        response.setId(1L);
        response.setTitle("Test Course");
        response.setDescription("Test Description");
        response.setInstructorId(1L);
        response.setCategory("Programming");
        response.setLevel("BEGINNER");

        when(courseService.getCourseById(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void getAllCourses_ReturnsCourseList() throws Exception {
        // Given
        CourseResponse course1 = new CourseResponse();
        course1.setId(1L);
        course1.setTitle("Course 1");
        course1.setDescription("Description 1");

        CourseResponse course2 = new CourseResponse();
        course2.setId(2L);
        course2.setTitle("Course 2");
        course2.setDescription("Description 2");

        List<CourseResponse> courses = Arrays.asList(course1, course2);

        when(courseService.getAllCourses()).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Course 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Course 2"));
    }

    @Test
    void updateCourse_ValidRequest_ReturnsUpdatedCourse() throws Exception {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Updated Course");
        request.setDescription("Updated Description");
        request.setInstructorId(1L);
        request.setCategory("Programming");
        request.setLevel("INTERMEDIATE");

        CourseResponse response = new CourseResponse();
        response.setId(1L);
        response.setTitle("Updated Course");
        response.setDescription("Updated Description");
        response.setInstructorId(1L);
        response.setCategory("Programming");
        response.setLevel("INTERMEDIATE");

        when(courseService.updateCourse(anyLong(), any(CreateCourseRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Course"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void deleteCourse_ValidId_ReturnsNoContent() throws Exception {
        // Given
        when(courseService.deleteCourse(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCoursesByInstructor_ValidInstructorId_ReturnsCourseList() throws Exception {
        // Given
        CourseResponse course1 = new CourseResponse();
        course1.setId(1L);
        course1.setTitle("Course 1");
        course1.setInstructorId(1L);

        CourseResponse course2 = new CourseResponse();
        course2.setId(2L);
        course2.setTitle("Course 2");
        course2.setInstructorId(1L);

        List<CourseResponse> courses = Arrays.asList(course1, course2);

        when(courseService.getCoursesByInstructor(1L)).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/courses/instructor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Course 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Course 2"));
    }
} 