package com.lms.user.controller;

import com.lms.user.dto.request.LoginRequest;
import com.lms.user.dto.request.RegisterRequest;
import com.lms.user.dto.response.AuthResponse;
import com.lms.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        // Implementation for token refresh
        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestHeader("Authorization") String token) {
        // Implementation for logout (blacklist token)
        return Mono.just(ResponseEntity.ok().build());
    }
} 