// ========== 具体策略 - 基于域名的提供商选择 ==========
package com.collaboportal.common.oauth2.strategy.impl;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.strategy.OAuth2ProviderSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于域名的提供商选择策略
 * 支持多域名部署，不同域名使用不同的OAuth2提供商
 */
@Component("domainBasedProviderStrategy")
public class DomainBasedProviderSelectionStrategy implements OAuth2ProviderSelectionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DomainBasedProviderSelectionStrategy.class);

    // 域名到提供商的映射（可以从配置文件读取）
    private final Map<String, String> domainToProviderMap = new HashMap<>();

    public DomainBasedProviderSelectionStrategy(
            @Value("${oauth2.domain-mappings.providerA:app-a.example.com}") String domainA,
            @Value("${oauth2.domain-mappings.providerB:app-b.example.com}") String domainB,
            @Value("${oauth2.domain-mappings.providerC:app-c.example.com}") String domainC) {

        domainToProviderMap.put(domainA, "providerA");
        domainToProviderMap.put(domainB, "providerB");
        domainToProviderMap.put(domainC, "providerC");

        logger.info("[域名策略] 初始化域名映射: {}", domainToProviderMap);
    }

    @Override
    public String selectProvider(OAuth2ProviderContext context) {
        String requestHost = context.getRequestHost();
        logger.debug("[域名策略] 分析请求域名: {}", requestHost);

        if (requestHost == null || requestHost.isEmpty()) {
            logger.warn("[域名策略] 请求域名为空");
            return null;
        }

        String providerId = domainToProviderMap.get(requestHost);
        if (providerId != null) {
            logger.info("[域名策略] 域名 {} 匹配到提供商: {}", requestHost, providerId);
        } else {
            logger.debug("[域名策略] 域名 {} 未匹配到任何提供商", requestHost);
        }

        return providerId;
    }
}