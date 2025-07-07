package com.collaboportal.kanjya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 納品システム Kanjya API 服務
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.collaboportal.kanjya", "com.collaboportal.common"})
public class KanjyaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanjyaApplication.class, args);
    }
} 