package com.lms.user.repository;

import com.lms.user.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    
    @Query("SELECT * FROM users WHERE email = :email")
    Mono<User> findByEmail(String email);
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);
    
    @Query("SELECT * FROM users WHERE role = :role")
    reactor.core.publisher.Flux<User> findByRole(String role);
    
    @Query("SELECT * FROM users WHERE id = :id AND role = :role")
    Mono<User> findByIdAndRole(UUID id, String role);
} 