package com.example.aidebug.workflows;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowsController {
    @GetMapping("/api/workflows/ping")
    public String ping() {
        return "Workflows module is up!";
    }
} 