package com.lms.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/api/v1/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/users/(?<segment>.*)", "/api/v1/users/${segment}")
                                .rewritePath("/api/v1/auth/(?<segment>.*)", "/api/v1/auth/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://user-service"))
                
                // Course Service Routes
                .route("course-service", r -> r
                        .path("/api/v1/courses/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/courses/(?<segment>.*)", "/api/v1/courses/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://course-service"))
                
                // Enrollment Service Routes
                .route("enrollment-service", r -> r
                        .path("/api/v1/enrollments/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/enrollments/(?<segment>.*)", "/api/v1/enrollments/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://enrollment-service"))
                
                // Assessment Service Routes
                .route("assessment-service", r -> r
                        .path("/api/v1/assessments/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/assessments/(?<segment>.*)", "/api/v1/assessments/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://assessment-service"))
                
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/notifications/(?<segment>.*)", "/api/v1/notifications/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://notification-service"))
                
                // WebSocket Routes
                .route("websocket-service", r -> r
                        .path("/ws/**")
                        .uri("lb://notification-service"))
                
                .build();
    }
} 