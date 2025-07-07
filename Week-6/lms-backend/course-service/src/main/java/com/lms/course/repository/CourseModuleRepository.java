package com.lms.course.repository;

import com.lms.course.entity.CourseModule;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CourseModuleRepository extends ReactiveCrudRepository<CourseModule, UUID> {
    
    @Query("SELECT * FROM course_modules WHERE course_id = :courseId ORDER BY order_index ASC")
    Flux<CourseModule> findByCourseIdOrderByOrderIndex(UUID courseId);
    
    @Query("SELECT COUNT(*) FROM course_modules WHERE course_id = :courseId")
    reactor.core.publisher.Mono<Long> countByCourseId(UUID courseId);
} 