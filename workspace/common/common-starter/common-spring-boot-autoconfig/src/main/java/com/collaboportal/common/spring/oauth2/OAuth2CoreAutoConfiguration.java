package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.processor.impl.AuthProcessorImpl;
import com.collaboportal.common.oauth2.factory.UserInfoServiceFactory;
import com.collaboportal.common.oauth2.utils.APIClient;
import com.collaboportal.common.ConfigManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * OAuth2 核心自動配置類
 * 負責註冊 OAuth2 相關的核心組件
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.oauth2.factory",
        "com.collaboportal.common.oauth2.processor",
        "com.collaboportal.common.oauth2.utils"
})
public class OAuth2CoreAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2CoreAutoConfiguration.class);

    public OAuth2CoreAutoConfiguration() {
        logger.debug("OAuth2 核心自動配置初始化完成");
    }

    /**
     * OAuth2 客戶端註冊工廠 Bean
     * 管理 OAuth2 提供者的配置信息
     * 
     * @return OAuth2ClientRegistrationFactory 實例
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2ClientRegistrationFactory.class)
    public OAuth2ClientRegistrationFactory oAuth2ClientRegistrationFactory() {
        logger.debug("註冊 OAuth2ClientRegistrationFactory Bean");
        return new OAuth2ClientRegistrationFactory();
    }

    /**
     * 認證處理器 Bean
     * 處理 OAuth2 認證流程
     * 
     * @return AuthProcessor 實例
     */
    @Bean
    @ConditionalOnMissingBean(AuthProcessor.class)
    public AuthProcessor authProcessor() {
        logger.debug("註冊 AuthProcessor Bean");
        return new AuthProcessorImpl();
    }

    /**
     * API 客戶端 Bean
     * 用於與 OAuth2 提供者的 API 通信
     * 
     * @return APIClient 實例
     */
    @Bean
    @ConditionalOnMissingBean(APIClient.class)
    public APIClient apiClient() {
        String baseUrl = ConfigManager.getConfig().getCollaboidBaseurl();
        logger.debug("註冊 APIClient Bean，baseUrl: {}", baseUrl);
        return new APIClient(baseUrl);
    }

    /**
     * 用戶信息服務工廠 Bean
     * 註意：UserInfoServiceFactory 是靜態工廠類，這裡主要是為了確保它被初始化
     * 
     * @return UserInfoServiceFactory 實例
     */
    @Bean
    @ConditionalOnMissingBean(UserInfoServiceFactory.class)
    public UserInfoServiceFactory userInfoServiceFactory() {
        logger.debug("初始化 UserInfoServiceFactory");
        return new UserInfoServiceFactory();
    }

    /**
     * OAuth2 配置屬性 Bean
     * 綁定外部配置文件中的 OAuth2 相關屬性
     * 
     * @return OAuth2ConfigurationProperties 實例
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2ConfigurationProperties.class)
    public OAuth2ConfigurationProperties oAuth2ConfigurationProperties() {
        logger.debug("註冊 OAuth2ConfigurationProperties Bean");
        return new OAuth2ConfigurationProperties();
    }
}