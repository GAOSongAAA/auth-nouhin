package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * OAuth2 策略自動配置類
 * 負責註冊各種策略註冊表和模板類
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal"
})
public class OAuth2StrategyAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2StrategyAutoConfiguration.class);

    public OAuth2StrategyAutoConfiguration() {
        logger.debug("OAuth2 策略自動配置初始化完成");
    }

    /**
     * 登錄策略註冊表 Bean
     * 管理不同的登錄策略
     * 
     * @return LoginStrategyRegistry 實例
     */
    @Bean
    @ConditionalOnMissingBean(LoginStrategyRegistry.class)
    public LoginStrategyRegistry loginStrategyRegistry() {
        logger.debug("註冊 LoginStrategyRegistry Bean");
        return new LoginStrategyRegistry();
    }

    /**
     * JWT Token 策略註冊表 Bean
     * 管理不同的 JWT Token 提取策略
     * 
     * @return JwtTokenStrategyRegistry 實例
     */
    @Bean
    @ConditionalOnMissingBean(JwtTokenStrategyRegistry.class)
    public JwtTokenStrategyRegistry jwtTokenStrategyRegistry() {
        logger.debug("註冊 JwtTokenStrategyRegistry Bean");
        return new JwtTokenStrategyRegistry();
    }


}