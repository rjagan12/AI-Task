package com.lms.user.controller;

import com.lms.user.entity.User;
import com.lms.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<User>> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/{userId}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable UUID userId, @RequestBody User updatedUser) {
        return userService.updateUser(userId, updatedUser)
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable UUID userId) {
        return userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/profile")
    public Mono<ResponseEntity<User>> getCurrentUserProfile(@RequestHeader("X-User-ID") String userId) {
        return userService.getUserById(UUID.fromString(userId))
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }
} 