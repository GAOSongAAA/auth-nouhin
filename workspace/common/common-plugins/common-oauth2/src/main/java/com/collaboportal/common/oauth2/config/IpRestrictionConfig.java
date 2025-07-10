package com.collaboportal.common.oauth2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.collaboportal.common.oauth2.interceptor.IpRestrictionInterceptor;

/**
 * IP限制配置類
 * 自動註冊IP驗證攔截器到Spring MVC攔截器鏈中
 */
@Configuration
public class IpRestrictionConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(IpRestrictionConfig.class);

    @Autowired
    private IpRestrictionInterceptor ipRestrictionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("註冊IP限制攔截器到Spring MVC攔截器鏈");

        registry.addInterceptor(ipRestrictionInterceptor)
                .addPathPatterns("/**") // 攔截所有路徑
                .excludePathPatterns( // 排除一些常見的靜態資源路徑
                        "/static/**",
                        "/public/**",
                        "/resources/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/error",
                        "/actuator/**" // Spring Boot Actuator端點
                );

        logger.info("IP限制攔截器註冊完成");
    }
}