// ========== 具体策略 - 基于路径前缀的提供商选择 ==========
package com.collaboportal.common.oauth2.strategy.impl;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.strategy.OAuth2ProviderSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 基于路径前缀的提供商选择策略
 * 策略模式具体实现 - 根据请求路径选择OAuth2提供商
 */
@Component("pathBasedProviderStrategy")
public class PathBasedProviderSelectionStrategy implements OAuth2ProviderSelectionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PathBasedProviderSelectionStrategy.class);

    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public PathBasedProviderSelectionStrategy(OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.clientRegistrationFactory = clientRegistrationFactory;
    }

    @Override
    public String selectProvider(OAuth2ProviderContext context) {
        String requestPath = context.getRequestPath();
        logger.debug("[路径策略] 开始分析请求路径: {}", requestPath);

        if (requestPath == null || requestPath.isEmpty()) {
            logger.warn("[路径策略] 请求路径为空，无法选择提供商");
            return null;
        }

        String providerId = clientRegistrationFactory.findProviderByPath(requestPath);
        if (providerId != null) {
            logger.info("[路径策略] 路径 {} 匹配到提供商: {}", requestPath, providerId);
        } else {
            logger.debug("[路径策略] 路径 {} 未匹配到任何提供商", requestPath);
        }

        return providerId;
    }
}