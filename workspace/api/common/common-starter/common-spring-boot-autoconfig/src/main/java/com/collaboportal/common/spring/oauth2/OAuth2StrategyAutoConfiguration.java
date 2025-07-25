package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.template.ext.CallbackLoginTemplate;
import com.collaboportal.common.oauth2.processor.AuthProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * OAuth2 策略自動配置類
 * 負責註冊各種策略註冊表和模板類
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.oauth2.registry",
        "com.collaboportal.common.oauth2.strategy"
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

    // OAuth2LoginTemplate 是基礎模板類，不需要直接註冊為 Bean
    // 由具體的子類實現來使用，如 CallbackLoginTemplate, JwtValidationTemplate 等

    /**
     * 回調登錄模板 Bean
     * 處理 OAuth2 回調的登錄模板
     * 
     * @param authProcessor 認證處理器
     * @return CallbackLoginTemplate 實例
     */
    @Bean
    @ConditionalOnMissingBean(CallbackLoginTemplate.class)
    @ConditionalOnBean(AuthProcessor.class)
    public CallbackLoginTemplate callbackLoginTemplate(
            @Autowired AuthProcessor authProcessor,
            @Autowired LoginStrategyRegistry strategyRegistry,
            @Autowired com.collaboportal.common.jwt.utils.JwtTokenUtil jwtTokenUtil) {
        logger.debug("註冊 CallbackLoginTemplate Bean");
        return new CallbackLoginTemplate(authProcessor, strategyRegistry, jwtTokenUtil);
    }
}