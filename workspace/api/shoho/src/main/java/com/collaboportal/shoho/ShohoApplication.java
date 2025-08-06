// 文件路径: workspace/api/shoho/src/main/java/com/collaboportal/shoho/ShohoApplication.java

package com.collaboportal.shoho;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 主启动类
 */
@MapperScan("com.collaboportal")
@SpringBootApplication(scanBasePackages = {
        "com.collaboportal.shoho",
        "com.collaboportal.common"
})
public class ShohoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShohoApplication.class, args);
    }
}