package com.lms.course.repository;

import com.lms.course.entity.Course;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CourseRepository extends ReactiveCrudRepository<Course, UUID> {
    
    @Query("SELECT * FROM courses WHERE instructor_id = :instructorId ORDER BY created_at DESC")
    Flux<Course> findByInstructorId(UUID instructorId);
    
    @Query("SELECT * FROM courses WHERE status = :status ORDER BY created_at DESC")
    Flux<Course> findByStatus(String status);
    
    @Query("SELECT * FROM courses WHERE title ILIKE :title OR description ILIKE :description")
    Flux<Course> findByTitleOrDescriptionContaining(String title, String description);
    
    @Query("SELECT * FROM courses WHERE instructor_id = :instructorId AND status = :status")
    Flux<Course> findByInstructorIdAndStatus(UUID instructorId, String status);
    
    @Query("SELECT EXISTS(SELECT 1 FROM courses WHERE id = :courseId AND instructor_id = :instructorId)")
    Mono<Boolean> existsByIdAndInstructorId(UUID courseId, UUID instructorId);
} 