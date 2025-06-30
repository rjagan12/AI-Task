package com.example.aidebug.errorhandling;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandlingController {
    @GetMapping("/api/errorhandling/ping")
    public String ping() {
        return "Error Handling module is up!";
    }
} 