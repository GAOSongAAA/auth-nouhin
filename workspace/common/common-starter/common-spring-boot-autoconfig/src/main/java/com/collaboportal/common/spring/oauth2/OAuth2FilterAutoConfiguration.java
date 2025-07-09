package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.filter.JwtAuthFilter;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.template.ext.JwtValidationTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * OAuth2 過濾器自動配置類
 * 負責註冊 JWT 驗證過濾器和相關組件
 */
@AutoConfiguration
public class OAuth2FilterAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2FilterAutoConfiguration.class);

    public OAuth2FilterAutoConfiguration() {
        logger.debug("OAuth2 過濾器自動配置初始化完成");
    }

    /**
     * JWT 驗證模板 Bean
     * 提供 JWT 驗證的核心邏輯
     * 
     * @param jwtTokenUtil              JWT 工具類
     * @param clientRegistrationFactory OAuth2 客戶端註冊工廠
     * @return JwtValidationTemplate 實例
     */
    @Bean
    @ConditionalOnMissingBean(JwtValidationTemplate.class)
    @ConditionalOnBean({ JwtTokenUtil.class, OAuth2ClientRegistrationFactory.class })
    public JwtValidationTemplate jwtValidationTemplate(
            @Autowired JwtTokenUtil jwtTokenUtil,
            @Autowired OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        logger.debug("註冊 JwtValidationTemplate Bean");
        return new JwtValidationTemplate(jwtTokenUtil, clientRegistrationFactory);
    }

    /**
     * JWT 驗證鏈 Bean
     * 構建 JWT 驗證處理鏈
     * 
     * @param jwtValidationTemplate JWT 驗證模板
     * @return JwtValidationChain 實例
     */
    @Bean
    @ConditionalOnMissingBean(JwtValidationChain.class)
    @ConditionalOnBean(JwtValidationTemplate.class)
    @DependsOn("jwtValidationTemplate")
    public JwtValidationChain jwtValidationChain(
            @Autowired JwtValidationTemplate jwtValidationTemplate) {
        logger.debug("註冊 JwtValidationChain Bean");
        return jwtValidationTemplate.buildValidationChain();
    }

    /**
     * JWT 認證過濾器 Bean
     * 主要的 JWT 認證過濾器
     * 
     * @param jwtTokenUtil              JWT 工具類
     * @param clientRegistrationFactory OAuth2 客戶端註冊工廠
     * @return JwtAuthFilter 實例
     */
    @Bean
    @ConditionalOnMissingBean(JwtAuthFilter.class)
    @ConditionalOnBean({ JwtTokenUtil.class, OAuth2ClientRegistrationFactory.class })
    public JwtAuthFilter jwtAuthFilter(
            @Autowired JwtTokenUtil jwtTokenUtil,
            @Autowired OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        logger.debug("註冊 JwtAuthFilter Bean");
        return new JwtAuthFilter(jwtTokenUtil, clientRegistrationFactory);
    }
}