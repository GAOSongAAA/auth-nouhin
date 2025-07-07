
// ========== 启动配置建议 ==========
package com.collaboportal.common.oauth2.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2自动配置类
 * 确保所有OAuth2相关组件被正确扫描和注册
 */
@Configuration
@ComponentScan({
        "com.collaboportal.common.oauth2.factory",
        "com.collaboportal.common.oauth2.strategy",
        "com.collaboportal.common.oauth2.template",
        "com.collaboportal.common.oauth2.chain",
        "com.collaboportal.common.oauth2.controller"
})
public class OAuth2AutoConfiguration {
    // 自动配置类，确保组件扫描
}

// ========== 主应用程序启动建议 ==========
/*
 * 在你的主Application类中添加以下注解：
 * 
 * @SpringBootApplication
 * 
 * @Import(OAuth2AutoConfiguration.class)
 * 
 * @EnableConfigurationProperties(OAuth2ClientRegistrationFactory.class)
 * public class CollaboportalApplication {
 * public static void main(String[] args) {
 * SpringApplication.run(CollaboportalApplication.class, args);
 * }
 * }
 */