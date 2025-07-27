// 文件路径: workspace/api/shoho/src/main/java/com/collaboportal/shoho/ShohoApplication.java

package com.collaboportal.shoho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 主启动类
 */
@SpringBootApplication(
    // 关键修正：明确、完整地列出所有需要扫描的基础包路径
    scanBasePackages = {
        "com.collaboportal.shoho",  
		"com.collaboportal.common",                // 扫描 shoho 业务模块自身
        "com.collaboportal.common.spring.common",   // 扫描通用配置类 (例如 CommonAuthConfiguration)
        "com.collaboportal.common.filter",          // 扫描过滤器 (如果它们被定义为Bean)
        "com.collaboportal.common.login.strategy",  // **确保扫描到 DatabaseAuthStrategy**
        "com.collaboportal.common.spring.registry", // 扫描策略注册表
        "com.collaboportal.common.jwt",             // 扫描 JWT 工具类
        // 根据您的项目结构，可以添加其他必要的 common 包
        "com.collaboportal.common.controller",      // 扫描通用控制器 (例如 CustomErrorController)
        "com.collaboportal.common.spring.exception" // 扫描全局异常处理器
    }
)
public class ShohoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShohoApplication.class, args);
    }
}