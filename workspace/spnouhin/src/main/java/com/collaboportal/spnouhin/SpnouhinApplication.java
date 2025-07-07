package com.collaboportal.spnouhin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 納品システム Spnouhin API 服務
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.collaboportal.spnouhin", "com.collaboportal.common"})
public class SpnouhinApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpnouhinApplication.class, args);
    }
} 