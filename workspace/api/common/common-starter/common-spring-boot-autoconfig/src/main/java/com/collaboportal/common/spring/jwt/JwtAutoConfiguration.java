package com.collaboportal.common.spring.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import org.springframework.context.annotation.ComponentScan;

/**
 * JWT 自動配置類
 * 負責註冊 JWT 相關的核心組件
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.jwt",
        "com.collaboportal.common.login",
        "com.collaboportal.common.oauth2"
})
public class JwtAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(JwtAutoConfiguration.class);

    public JwtAutoConfiguration() {
        logger.debug("JWT 自動配置初始化完成");
    }

}