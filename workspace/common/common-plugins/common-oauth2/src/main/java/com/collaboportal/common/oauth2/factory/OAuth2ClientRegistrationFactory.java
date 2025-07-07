// ========== 工厂模式 - OAuth2客户端注册工厂 ==========
package com.collaboportal.common.oauth2.factory;

import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工厂模式 - OAuth2客户端注册工厂
 * 职责：创建和管理多个OAuth2ClientRegistration实例
 * 优势：集中管理客户端配置，易于扩展新的提供商
 */
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2ClientRegistrationFactory {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientRegistrationFactory.class);

    // 存储所有提供商配置
    private Map<String, Map<String, Object>> providers = new HashMap<>();

    // 缓存已创建的客户端注册
    private final Map<String, OAuth2ClientRegistration> clientRegistrations = new ConcurrentHashMap<>();

    // 路径模式到提供商ID的映射
    private final Map<String, String> pathToProviderMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("OAuth2客户端注册工厂初始化开始，提供商数量: {}", providers.size());

        for (Map.Entry<String, Map<String, Object>> entry : providers.entrySet()) {
            String providerId = entry.getKey();
            Map<String, Object> config = entry.getValue();

            try {
                OAuth2ClientRegistration registration = createClientRegistration(providerId, config);
                clientRegistrations.put(providerId, registration);

                // 建立路径模式映射
                List<String> pathPatterns = (List<String>) config.get("path-patterns");
                if (pathPatterns != null) {
                    for (String pattern : pathPatterns) {
                        pathToProviderMap.put(pattern, providerId);
                    }
                }

                logger.info("成功创建OAuth2客户端注册: {} - {}", providerId, registration.getDisplayName());
            } catch (Exception e) {
                logger.error("创建OAuth2客户端注册失败: {}", providerId, e);
            }
        }

        logger.info("OAuth2客户端注册工厂初始化完成，成功注册提供商: {}", clientRegistrations.keySet());
    }

    /**
     * 创建OAuth2客户端注册
     */
    private OAuth2ClientRegistration createClientRegistration(String providerId, Map<String, Object> config) {
        return new OAuth2ClientRegistration.Builder()
                .providerId(providerId)
                .clientId((String) config.get("client-id"))
                .clientSecret((String) config.get("client-secret"))
                .authorizationUri((String) config.get("authorization-uri"))
                .tokenUri((String) config.get("token-uri"))
                .userInfoUri((String) config.get("user-info-uri"))
                .redirectUri((String) config.get("redirect-uri"))
                .scope((String) config.get("scope"))
                .responseType((String) config.getOrDefault("response-type", "code"))
                .grantType((String) config.getOrDefault("grant-type", "authorization_code"))
                .pathPatterns((List<String>) config.get("path-patterns"))
                .userNameAttribute((String) config.getOrDefault("user-name-attribute", "email"))
                .displayName((String) config.get("display-name"))
                .build();
    }

    /**
     * 根据提供商ID获取客户端注册
     */
    public OAuth2ClientRegistration getClientRegistration(String providerId) {
        return clientRegistrations.get(providerId);
    }

    /**
     * 获取所有客户端注册
     */
    public Map<String, OAuth2ClientRegistration> getAllClientRegistrations() {
        return Collections.unmodifiableMap(clientRegistrations);
    }

    /**
     * 根据请求路径匹配提供商
     */
    public String findProviderByPath(String requestPath) {
        for (Map.Entry<String, String> entry : pathToProviderMap.entrySet()) {
            String pattern = entry.getKey();
            String providerId = entry.getValue();

            // 简单的通配符匹配
            if (isPathMatch(requestPath, pattern)) {
                logger.debug("路径 {} 匹配到提供商: {}", requestPath, providerId);
                return providerId;
            }
        }

        logger.debug("路径 {} 未匹配到任何提供商", requestPath);
        return null;
    }

    /**
     * 简单的路径匹配逻辑
     */
    private boolean isPathMatch(String requestPath, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return requestPath.startsWith(prefix);
        } else if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return requestPath.startsWith(prefix) &&
                    requestPath.indexOf('/', prefix.length()) == -1;
        } else {
            return requestPath.equals(pattern);
        }
    }

    // Spring Boot配置属性注入
    public void setProviders(Map<String, Map<String, Object>> providers) {
        this.providers = providers;
    }

    public Map<String, Map<String, Object>> getProviders() {
        return providers;
    }
}