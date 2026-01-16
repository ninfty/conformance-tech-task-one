package com.raidiam.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

@RestController
public class ApiController {

    private Random random = new Random(System.currentTimeMillis());

    @GetMapping(value = "/api/now", produces = "application/json")
    public Map<String, String> now() {
        return Map.of(
                "time", String.valueOf(Instant.now())
        );
    }

    @GetMapping(value = "/api/random", produces = "application/json")
    public Map<String, String> random() {
        return Map.of(
                "random", String.valueOf(random.nextLong())
        );
    }

    @GetMapping(value = "/health", produces = "application/json")
    public Map<String, String> health() {
        return Map.of(
            "status", "Ok"
        );
    }

}
