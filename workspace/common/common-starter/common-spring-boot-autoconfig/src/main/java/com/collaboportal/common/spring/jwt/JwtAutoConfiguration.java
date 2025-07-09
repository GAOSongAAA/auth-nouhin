package com.collaboportal.common.spring.jwt;

import com.collaboportal.common.jwt.utils.JwtTokenUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * JWT 自動配置類
 * 負責註冊 JWT 相關的核心組件
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.jwt"
})
public class JwtAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(JwtAutoConfiguration.class);

    public JwtAutoConfiguration() {
        logger.debug("JWT 自動配置初始化完成");
    }

    /**
     * JWT Token 工具類 Bean
     * 提供 JWT Token 的生成、解析和驗證功能
     * 
     * @return JwtTokenUtil 實例
     */
    @Bean
    @ConditionalOnMissingBean(JwtTokenUtil.class)
    public JwtTokenUtil jwtTokenUtil() {
        logger.debug("註冊 JwtTokenUtil Bean");
        return new JwtTokenUtil();
    }
}