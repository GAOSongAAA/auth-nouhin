package com.collaboportal.kanjya.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 監視管理控制器
 */
@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "kanjya-api");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/items")
    public Map<String, Object> getMonitoringItems() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", "item-001");
        item1.put("name", "示例監視項目 1");
        item1.put("status", "ACTIVE");
        item1.put("priority", 1);
        items.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", "item-002");
        item2.put("name", "示例監視項目 2");
        item2.put("status", "MONITORING");
        item2.put("priority", 2);
        items.add(item2);
        
        response.put("items", items);
        response.put("total", items.size());
        return response;
    }
} 