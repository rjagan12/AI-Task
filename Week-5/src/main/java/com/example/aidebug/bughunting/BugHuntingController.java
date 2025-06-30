package com.example.aidebug.bughunting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BugHuntingController {
    @GetMapping("/api/bughunting/ping")
    public String ping() {
        return "Bug Hunting module is up!";
    }
} 