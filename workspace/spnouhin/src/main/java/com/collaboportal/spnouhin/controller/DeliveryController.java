package com.collaboportal.spnouhin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "spnouhin-api");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/projects")
    public Map<String, Object> getProjects() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> projects = new ArrayList<>();
        
        Map<String, Object> project1 = new HashMap<>();
        project1.put("id", "proj-001");
        project1.put("name", "示例納品項目 1");
        project1.put("status", "ACTIVE");
        projects.add(project1);
        
        Map<String, Object> project2 = new HashMap<>();
        project2.put("id", "proj-002");
        project2.put("name", "示例納品項目 2");
        project2.put("status", "DRAFT");
        projects.add(project2);
        
        response.put("projects", projects);
        response.put("total", projects.size());
        return response;
    }
} 