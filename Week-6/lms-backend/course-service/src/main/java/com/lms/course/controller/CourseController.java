package com.lms.course.controller;

import com.lms.course.dto.request.CreateCourseRequest;
import com.lms.course.dto.response.CourseResponse;
import com.lms.course.entity.Course;
import com.lms.course.entity.CourseModule;
import com.lms.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public Mono<ResponseEntity<CourseResponse>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return courseService.createCourse(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{courseId}")
    public Mono<ResponseEntity<CourseResponse>> getCourseById(@PathVariable UUID courseId) {
        return courseService.getCourseById(courseId)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping
    public Flux<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/instructor/{instructorId}")
    public Flux<CourseResponse> getCoursesByInstructor(@PathVariable UUID instructorId) {
        return courseService.getCoursesByInstructor(instructorId);
    }

    @GetMapping("/status/{status}")
    public Flux<CourseResponse> getCoursesByStatus(@PathVariable String status) {
        return courseService.getCoursesByStatus(status);
    }

    @GetMapping("/search")
    public Flux<CourseResponse> searchCourses(@RequestParam String q) {
        return courseService.searchCourses(q);
    }

    @PutMapping("/{courseId}")
    public Mono<ResponseEntity<CourseResponse>> updateCourse(@PathVariable UUID courseId, 
                                                           @RequestBody Course updatedCourse) {
        return courseService.updateCourse(courseId, updatedCourse)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{courseId}")
    public Mono<ResponseEntity<Void>> deleteCourse(@PathVariable UUID courseId) {
        return courseService.deleteCourse(courseId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/{courseId}/modules")
    public Flux<CourseModule> getCourseModules(@PathVariable UUID courseId) {
        return courseService.getCourseModules(courseId);
    }

    @PostMapping("/{courseId}/modules")
    public Mono<ResponseEntity<CourseModule>> addModuleToCourse(@PathVariable UUID courseId, 
                                                               @RequestBody CourseModule module) {
        return courseService.addModuleToCourse(courseId, module)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/modules/{moduleId}")
    public Mono<ResponseEntity<CourseModule>> updateModule(@PathVariable UUID moduleId, 
                                                          @RequestBody CourseModule updatedModule) {
        return courseService.updateModule(moduleId, updatedModule)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/modules/{moduleId}")
    public Mono<ResponseEntity<Void>> deleteModule(@PathVariable UUID moduleId) {
        return courseService.deleteModule(moduleId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }
} 