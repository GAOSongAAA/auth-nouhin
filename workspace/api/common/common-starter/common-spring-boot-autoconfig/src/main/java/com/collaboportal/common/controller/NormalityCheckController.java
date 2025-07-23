package com.collaboportal.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class NormalityCheckController {

    @GetMapping("/normality-check")
    public ResponseEntity<String> normalityCheck() {
        return ResponseEntity.ok("Normality check successful");
    }

    @GetMapping("/normality-check-2")
    public ResponseEntity<String> normalityCheck2() {
        return ResponseEntity.ok("Normality check successful 2");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login successful");
    }

}
