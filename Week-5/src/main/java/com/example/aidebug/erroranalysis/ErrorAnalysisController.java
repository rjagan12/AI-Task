package com.example.aidebug.erroranalysis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorAnalysisController {
    @GetMapping("/api/erroranalysis/ping")
    public String ping() {
        return "Error Analysis module is up!";
    }
} 