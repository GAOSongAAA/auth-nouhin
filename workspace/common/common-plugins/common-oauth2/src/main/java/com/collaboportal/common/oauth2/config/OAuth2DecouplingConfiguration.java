package com.collaboportal.common.oauth2.config;

import com.collaboportal.common.oauth2.service.TokenService;
import com.collaboportal.common.oauth2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * OAuth2 解耦配置類
 * 提供默認實現和條件裝配
 */
@Configuration
public class OAuth2DecouplingConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2DecouplingConfiguration.class);

    /**
     * 默認的 Token 服務實現
     * 當沒有其他實現時使用
     */
    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService defaultTokenService() {
        logger.warn("未找到 TokenService 實現，使用默認的 NoOp 實現");
        return new DefaultTokenService();
    }

    /**
     * 默認的用戶服務實現
     * 當沒有其他實現時使用
     */
    @Bean
    @ConditionalOnMissingBean(UserService.class)
    public UserService defaultUserService() {
        logger.warn("未找到 UserService 實現，使用默認的 NoOp 實現");
        return new DefaultUserService();
    }

    /**
     * 默認的 Token 服務實現（NoOp）
     */
    private static class DefaultTokenService implements TokenService {
        private static final Logger logger = LoggerFactory.getLogger(DefaultTokenService.class);

        @Override
        public String generateAuthToken(Map<String, Object> userInfo) {
            logger.warn("使用默認 TokenService，生成 Token 功能未實現");
            throw new UnsupportedOperationException("TokenService 未正確配置");
        }

        @Override
        public boolean validateToken(String token) {
            logger.warn("使用默認 TokenService，驗證 Token 功能未實現");
            return false;
        }

        @Override
        public Map<String, Object> extractUserInfo(String token) {
            logger.warn("使用默認 TokenService，提取用戶信息功能未實現");
            throw new UnsupportedOperationException("TokenService 未正確配置");
        }

        @Override
        public String refreshToken(String token) {
            logger.warn("使用默認 TokenService，刷新 Token 功能未實現");
            throw new UnsupportedOperationException("TokenService 未正確配置");
        }
    }

    /**
     * 默認的用戶服務實現（NoOp）
     */
    private static class DefaultUserService implements UserService {
        private static final Logger logger = LoggerFactory.getLogger(DefaultUserService.class);

        @Override
        public Map<String, Object> getUserByEmail(String email) {
            logger.warn("使用默認 UserService，獲取用戶功能未實現");
            return null;
        }

        @Override
        public Map<String, Object> createOrUpdateUser(Map<String, Object> userInfo) {
            logger.warn("使用默認 UserService，創建用戶功能未實現");
            throw new UnsupportedOperationException("UserService 未正確配置");
        }

        @Override
        public boolean userExists(String email) {
            logger.warn("使用默認 UserService，檢查用戶存在功能未實現");
            return false;
        }

        @Override
        public String getUserType(String email) {
            logger.warn("使用默認 UserService，獲取用戶類型功能未實現");
            return "0";
        }
    }
}