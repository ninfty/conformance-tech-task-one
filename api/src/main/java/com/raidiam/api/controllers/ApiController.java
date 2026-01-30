package com.raidiam.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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

    @RequestMapping(method = RequestMethod.POST, path = "/echo", produces = "application/json", consumes = {"application/json", "application/x-www-form-urlencoded;charset=UTF-8"})
    public ResponseEntity<?> echo(@RequestBody Map<String, Object> params) {
            return ResponseEntity.ok(params);
    }
}
