package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.oauth2.controller.AuthorizationController;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * OAuth2 控制器自動配置類
 * 負責註冊 OAuth2 相關的控制器組件
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.oauth2.controller"
})
public class OAuth2ControllerAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ControllerAutoConfiguration.class);

    public OAuth2ControllerAutoConfiguration() {
        logger.debug("OAuth2 控制器自動配置初始化完成");
    }

    /**
     * 授權控制器 Bean
     * 處理 OAuth2 授權請求和回調
     * 
     * @param loginStrategyRegistry 登錄策略註冊表
     * @return AuthorizationController 實例
     */
    @Bean
    @ConditionalOnMissingBean(AuthorizationController.class)
    @ConditionalOnBean(LoginStrategyRegistry.class)
    public AuthorizationController authorizationController(
            @Autowired LoginStrategyRegistry loginStrategyRegistry) {
        logger.debug("註冊 AuthorizationController Bean");
        return new AuthorizationController(loginStrategyRegistry);
    }
}