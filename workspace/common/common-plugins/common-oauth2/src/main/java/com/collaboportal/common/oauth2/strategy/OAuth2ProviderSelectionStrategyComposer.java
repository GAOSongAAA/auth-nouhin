// ========== 策略组合器 - 责任链模式组合多个策略 ==========
package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OAuth2提供商选择策略组合器
 * 责任链模式 - 按优先级尝试多个选择策略
 * 职责：协调多个策略，按优先级顺序执行
 * 优势：灵活组合策略，支持降级和备选方案
 */
@Component
public class OAuth2ProviderSelectionStrategyComposer {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ProviderSelectionStrategyComposer.class);

    private final List<OAuth2ProviderSelectionStrategy> strategies;
    private final String defaultProvider;
    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public OAuth2ProviderSelectionStrategyComposer(
            List<OAuth2ProviderSelectionStrategy> strategies,
            @Value("${app.default-provider:providerA}") String defaultProvider,
            OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.strategies = strategies;
        this.defaultProvider = defaultProvider;
        this.clientRegistrationFactory = clientRegistrationFactory;
        logger.info("OAuth2提供商选择策略组合器初始化，策略数量: {}, 默认提供商: {}",
                strategies.size(), defaultProvider);
    }

    /**
     * 按责任链模式选择提供商
     * 依次尝试各个策略，直到找到合适的提供商
     */
    public String selectProvider(OAuth2ProviderContext context) {
        logger.debug("开始执行提供商选择责任链，策略数量: {}", strategies.size());

        for (int i = 0; i < strategies.size(); i++) {
            OAuth2ProviderSelectionStrategy strategy = strategies.get(i);
            String providerId = strategy.selectProvider(context);

            if (providerId != null && !providerId.isEmpty()) {
                logger.info("策略 {} 成功选择提供商: {}", strategy.getClass().getSimpleName(), providerId);
                context.setSelectedProviderId(providerId);
                return providerId;
            }

            logger.debug("策略 {} 未能选择提供商，继续下一个策略", strategy.getClass().getSimpleName());
        }

        logger.warn("所有策略都未能选择提供商，使用默认提供商: {}", defaultProvider);
        context.setSelectedProviderId(defaultProvider);
        return defaultProvider;
    }

    /**
     * 获取可用的提供商列表 - 为控制器提供支持
     */
    public Object getAvailableProviders() {
        return clientRegistrationFactory.getAllClientRegistrations()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Map.of(
                                "id", entry.getKey(),
                                "name", entry.getValue().getDisplayName(),
                                "paths", entry.getValue().getPathPatterns())));
    }
}