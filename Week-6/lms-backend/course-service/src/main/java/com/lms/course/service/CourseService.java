package com.lms.course.service;

import com.lms.course.dto.request.CreateCourseRequest;
import com.lms.course.dto.response.CourseResponse;
import com.lms.course.entity.Course;
import com.lms.course.entity.CourseModule;
import com.lms.course.repository.CourseModuleRepository;
import com.lms.course.repository.CourseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;

    public CourseService(CourseRepository courseRepository, CourseModuleRepository courseModuleRepository) {
        this.courseRepository = courseRepository;
        this.courseModuleRepository = courseModuleRepository;
    }

    public Mono<CourseResponse> createCourse(CreateCourseRequest request) {
        Course course = new Course(request.getTitle(), request.getDescription(), 
                                 request.getInstructorId(), request.getStatus());
        
        return courseRepository.save(course)
                .flatMap(savedCourse -> courseModuleRepository.countByCourseId(savedCourse.getId())
                        .map(moduleCount -> mapToCourseResponse(savedCourse, moduleCount)));
    }

    public Mono<CourseResponse> getCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Course not found")))
                .flatMap(course -> courseModuleRepository.countByCourseId(courseId)
                        .map(moduleCount -> mapToCourseResponse(course, moduleCount)));
    }

    public Flux<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .flatMap(course -> courseModuleRepository.countByCourseId(course.getId())
                        .map(moduleCount -> mapToCourseResponse(course, moduleCount)));
    }

    public Flux<CourseResponse> getCoursesByInstructor(UUID instructorId) {
        return courseRepository.findByInstructorId(instructorId)
                .flatMap(course -> courseModuleRepository.countByCourseId(course.getId())
                        .map(moduleCount -> mapToCourseResponse(course, moduleCount)));
    }

    public Flux<CourseResponse> getCoursesByStatus(String status) {
        return courseRepository.findByStatus(status)
                .flatMap(course -> courseModuleRepository.countByCourseId(course.getId())
                        .map(moduleCount -> mapToCourseResponse(course, moduleCount)));
    }

    public Mono<CourseResponse> updateCourse(UUID courseId, Course updatedCourse) {
        return courseRepository.findById(courseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Course not found")))
                .flatMap(existingCourse -> {
                    existingCourse.setTitle(updatedCourse.getTitle());
                    existingCourse.setDescription(updatedCourse.getDescription());
                    existingCourse.setStatus(updatedCourse.getStatus());
                    existingCourse.setVersion(existingCourse.getVersion() + 1);
                    existingCourse.setUpdatedAt(LocalDateTime.now());
                    
                    return courseRepository.save(existingCourse)
                            .flatMap(savedCourse -> courseModuleRepository.countByCourseId(savedCourse.getId())
                                    .map(moduleCount -> mapToCourseResponse(savedCourse, moduleCount)));
                });
    }

    public Mono<Void> deleteCourse(UUID courseId) {
        return courseRepository.findById(courseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Course not found")))
                .flatMap(course -> courseRepository.deleteById(courseId))
                .then();
    }

    public Flux<CourseModule> getCourseModules(UUID courseId) {
        return courseModuleRepository.findByCourseIdOrderByOrderIndex(courseId);
    }

    public Mono<CourseModule> addModuleToCourse(UUID courseId, CourseModule module) {
        return courseRepository.findById(courseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Course not found")))
                .flatMap(course -> {
                    module.setCourseId(courseId);
                    return courseModuleRepository.save(module);
                });
    }

    public Mono<CourseModule> updateModule(UUID moduleId, CourseModule updatedModule) {
        return courseModuleRepository.findById(moduleId)
                .switchIfEmpty(Mono.error(new RuntimeException("Module not found")))
                .flatMap(existingModule -> {
                    existingModule.setTitle(updatedModule.getTitle());
                    existingModule.setContent(updatedModule.getContent());
                    existingModule.setOrderIndex(updatedModule.getOrderIndex());
                    return courseModuleRepository.save(existingModule);
                });
    }

    public Mono<Void> deleteModule(UUID moduleId) {
        return courseModuleRepository.findById(moduleId)
                .switchIfEmpty(Mono.error(new RuntimeException("Module not found")))
                .flatMap(module -> courseModuleRepository.deleteById(moduleId))
                .then();
    }

    public Flux<CourseResponse> searchCourses(String searchTerm) {
        String searchPattern = "%" + searchTerm + "%";
        return courseRepository.findByTitleOrDescriptionContaining(searchPattern, searchPattern)
                .flatMap(course -> courseModuleRepository.countByCourseId(course.getId())
                        .map(moduleCount -> mapToCourseResponse(course, moduleCount)));
    }

    private CourseResponse mapToCourseResponse(Course course, Long moduleCount) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getInstructorId(),
                null, // instructorName would be fetched from user service
                course.getStatus(),
                course.getVersion(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                moduleCount
        );
    }
} 