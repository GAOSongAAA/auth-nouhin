// ========== 具体策略 - 基于请求参数的提供商选择 ==========
package com.collaboportal.common.oauth2.strategy.impl;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.strategy.OAuth2ProviderSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 基于请求参数的提供商选择策略
 * 支持通过URL参数指定提供商：?provider=providerA
 */
@Component("paramBasedProviderStrategy")
public class ParamBasedProviderSelectionStrategy implements OAuth2ProviderSelectionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ParamBasedProviderSelectionStrategy.class);
    private static final String PROVIDER_PARAM = "provider";

    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public ParamBasedProviderSelectionStrategy(OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.clientRegistrationFactory = clientRegistrationFactory;
    }

    @Override
    public String selectProvider(OAuth2ProviderContext context) {
        String providerParam = context.getRequest().getParameter(PROVIDER_PARAM);
        logger.debug("[参数策略] 检查请求参数 {}: {}", PROVIDER_PARAM, providerParam);

        if (providerParam == null || providerParam.isEmpty()) {
            logger.debug("[参数策略] 未找到提供商参数");
            return null;
        }

        // 验证提供商是否存在
        if (clientRegistrationFactory.getClientRegistration(providerParam) != null) {
            logger.info("[参数策略] 通过参数选择提供商: {}", providerParam);
            return providerParam;
        } else {
            logger.warn("[参数策略] 参数指定的提供商不存在: {}", providerParam);
            return null;
        }
    }
}