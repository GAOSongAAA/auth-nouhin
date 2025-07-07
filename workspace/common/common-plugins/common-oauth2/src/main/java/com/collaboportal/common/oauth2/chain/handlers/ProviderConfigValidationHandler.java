// ========== 具体处理器 - 提供商配置验证处理器 ==========
package com.collaboportal.common.oauth2.chain.handlers;

import com.collaboportal.common.oauth2.chain.OAuth2CallbackHandler;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 提供商配置验证处理器
 * 责任链具体实现 - 验证选择的OAuth2提供商配置是否存在和有效
 */
@Component
public class ProviderConfigValidationHandler implements OAuth2CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProviderConfigValidationHandler.class);

    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public ProviderConfigValidationHandler(OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.clientRegistrationFactory = clientRegistrationFactory;
    }

    @Override
    public boolean handle(OAuth2ProviderContext context) {
        logger.debug("[提供商配置验证] 开始验证提供商配置");

        String providerId = context.getSelectedProviderId();
        if (providerId == null || providerId.isEmpty()) {
            logger.error("[提供商配置验证] 提供商ID为空");
            return false;
        }

        OAuth2ClientRegistration registration = clientRegistrationFactory.getClientRegistration(providerId);
        if (registration == null) {
            logger.error("[提供商配置验证] 未找到提供商配置: {}", providerId);
            return false;
        }

        // 验证关键配置项
        if (registration.getClientId() == null || registration.getClientSecret() == null) {
            logger.error("[提供商配置验证] 提供商配置不完整: {}", providerId);
            return false;
        }

        logger.debug("[提供商配置验证] 提供商配置验证成功: {}", providerId);
        return true;
    }
}