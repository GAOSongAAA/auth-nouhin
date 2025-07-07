// ========== 獨立的OAuth2登錄策略註冊表 ==========
package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.template.impl.DecoupledOAuth2LoginTemplate;
import com.collaboportal.common.strategy.LoginStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * 獨立的OAuth2登錄策略註冊表
 * 策略模式 - 管理多個OAuth2提供商的登錄策略
 * 職責：註冊和管理各個OAuth2提供商的具體登錄實現
 * 優勢：統一管理、動態選擇、易於擴展新提供商、完全解耦
 */
@Component("oauth2LoginStrategyRegistry")
public class OAuth2LoginStrategyRegistry {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginStrategyRegistry.class);

    // OAuth2登錄策略存儲映射
    private final Map<String, LoginStrategy> strategyMap = new HashMap<>();

    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;
    private final OAuth2ProviderSelectionStrategyComposer strategyComposer;
    private final DecoupledOAuth2LoginTemplate oauth2LoginTemplate;

    public OAuth2LoginStrategyRegistry(
            OAuth2ClientRegistrationFactory clientRegistrationFactory,
            OAuth2ProviderSelectionStrategyComposer strategyComposer,
            DecoupledOAuth2LoginTemplate oauth2LoginTemplate) {
        this.clientRegistrationFactory = clientRegistrationFactory;
        this.strategyComposer = strategyComposer;
        this.oauth2LoginTemplate = oauth2LoginTemplate;
    }

    @PostConstruct
    public void init() {
        logger.info("開始初始化OAuth2登錄策略註冊表");

        // 為每個OAuth2提供商註冊對應的登錄策略
        Map<String, OAuth2ClientRegistration> registrations = clientRegistrationFactory.getAllClientRegistrations();
        for (String providerId : registrations.keySet()) {
            register(providerId, createOAuth2LoginStrategy(providerId));
            logger.info("註冊OAuth2登錄策略: {}", providerId);
        }

        logger.info("OAuth2登錄策略註冊完成，總策略數量: {}", registrations.size());
    }

    /**
     * 註冊登錄策略
     * 
     * @param key      策略識別鍵
     * @param strategy 登錄策略
     */
    public void register(String key, LoginStrategy strategy) {
        strategyMap.put(key, strategy);
        logger.debug("策略註冊成功: {}", key);
    }

    /**
     * 獲取指定的登錄策略
     * 
     * @param key 策略識別鍵
     * @return 對應的登錄策略
     */
    public LoginStrategy getStrategy(String key) {
        LoginStrategy strategy = strategyMap.get(key);
        if (strategy == null) {
            logger.warn("未找到策略: {}", key);
        }
        return strategy;
    }

    /**
     * 創建OAuth2登錄策略
     */
    private LoginStrategy createOAuth2LoginStrategy(String providerId) {
        return (context) -> {
            logger.info("執行OAuth2登錄策略: {}", providerId);

            try {
                // 獲取提供商配置
                OAuth2ClientRegistration registration = clientRegistrationFactory.getClientRegistration(providerId);
                if (registration == null) {
                    logger.error("未找到提供商配置: {}", providerId);
                    throw new IllegalArgumentException("未找到提供商配置: " + providerId);
                }

                // 構建OAuth2上下文
                OAuth2ProviderContext oauth2Context = new OAuth2ProviderContext.Builder()
                        .request(context.getRequest())
                        .response(context.getResponse())
                        .selectedProviderId(providerId)
                        .build();

                // 集成OAuth2LoginTemplate執行完整的登錄流程
                oauth2LoginTemplate.executeLogin(oauth2Context);

                logger.info("OAuth2登錄策略 {} 執行成功", providerId);

            } catch (Exception e) {
                logger.error("OAuth2登錄策略 {} 執行失敗", providerId, e);
                throw new RuntimeException("OAuth2登錄失敗: " + e.getMessage(), e);
            }
        };
    }

    /**
     * 根據請求上下文動態選擇登錄策略
     */
    public LoginStrategy selectStrategy(OAuth2ProviderContext context) {
        String providerId = strategyComposer.selectProvider(context);
        LoginStrategy strategy = getStrategy(providerId);

        if (strategy == null) {
            logger.warn("未找到提供商 {} 的登錄策略，返回 null", providerId);
            return null;
        }

        return strategy;
    }

    /**
     * 獲取所有已註冊的策略鍵
     */
    public Set<String> getAllStrategyKeys() {
        return new HashSet<>(strategyMap.keySet());
    }

    /**
     * 檢查是否存在指定的策略
     */
    public boolean hasStrategy(String key) {
        return strategyMap.containsKey(key);
    }
}