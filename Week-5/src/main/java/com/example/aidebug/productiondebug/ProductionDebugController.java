package com.example.aidebug.productiondebug;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductionDebugController {
    @GetMapping("/api/productiondebug/ping")
    public String ping() {
        return "Production Debug module is up!";
    }
} 