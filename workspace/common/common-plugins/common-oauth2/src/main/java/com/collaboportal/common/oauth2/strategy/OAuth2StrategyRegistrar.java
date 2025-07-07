package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.strategy.AuthenticationStrategyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;

/**
 * oauth2策略註冊器
 * 
 * 職責：
 * 1. 向全局策略註冊中心註冊OAuth2相關的認證策略
 * 2. 管理OAuth2策略的生命週期
 * 3. 支持配置化的策略註冊
 * 
 * 解耦設計：
 * - 通過全局註冊中心與其他模塊交互
 * - 不直接依賴JWT模塊
 * - 支持策略的動態註冊和替換
 */
@Component
public class OAuth2StrategyRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2StrategyRegistrar.class);

    private final AuthenticationStrategyRegistry globalStrategyRegistry;
    private final EnhancedOAuth2ProdStrategy enhancedOAuth2ProdStrategy;

    // 配置是否替換默認的prod策略
    @Value("${oauth2.strategy.replace-prod:true}")
    private boolean replaceProdStrategy;

    // 配置是否註冊OAuth2專用策略
    @Value("${oauth2.strategy.register-oauth2-specific:true}")
    private boolean registerOAuth2SpecificStrategies;

    public OAuth2StrategyRegistrar(
            AuthenticationStrategyRegistry globalStrategyRegistry,
            EnhancedOAuth2ProdStrategy enhancedOAuth2ProdStrategy) {
        this.globalStrategyRegistry = globalStrategyRegistry;
        this.enhancedOAuth2ProdStrategy = enhancedOAuth2ProdStrategy;
    }

    @PostConstruct
    public void registerOAuth2Strategies() {
        logger.info("[OAuth2策略註冊] 開始註冊OAuth2認證策略");

        try {
            // 1. 註冊增強的OAuth2產品策略（可選擇是否替換默認prod策略）
            if (replaceProdStrategy) {
                registerEnhancedProdStrategy();
            }

            // 2. 註冊OAuth2專用策略
            if (registerOAuth2SpecificStrategies) {
                registerOAuth2SpecificStrategies();
            }

            // 3. 註冊提供商特定策略
            registerProviderSpecificStrategies();

            logger.info("[OAuth2策略註冊] OAuth2策略註冊完成");

        } catch (Exception e) {
            logger.error("[OAuth2策略註冊] OAuth2策略註冊失敗", e);
            throw new RuntimeException("OAuth2策略註冊失敗", e);
        }
    }

    /**
     * 註冊增強的OAuth2產品策略
     */
    private void registerEnhancedProdStrategy() {
        logger.info("[策略註冊] 註冊增強的OAuth2產品策略");

        try {
            // 檢查是否已存在prod策略
            if (globalStrategyRegistry.hasStrategy("prod")) {
                logger.warn("[策略註冊] 檢測到已存在的prod策略，將被OAuth2增強策略替換");
            }

            // 註冊增強的OAuth2產品策略
            globalStrategyRegistry.registerStrategy("prod", enhancedOAuth2ProdStrategy);

            logger.info("[策略註冊] 增強的OAuth2產品策略註冊成功，已替換默認prod策略");

        } catch (Exception e) {
            logger.error("[策略註冊] 增強OAuth2產品策略註冊失敗", e);
            throw e;
        }
    }

    /**
     * 註冊OAuth2專用策略
     */
    private void registerOAuth2SpecificStrategies() {
        logger.info("[策略註冊] 註冊OAuth2專用策略");

        try {
            // 註冊oauth2策略（作為prod策略的別名）
            globalStrategyRegistry.registerStrategy("oauth2", enhancedOAuth2ProdStrategy);
            logger.info("[策略註冊] oauth2策略註冊成功");

            // 註冊oauth2-prod策略（明確的OAuth2產品策略）
            globalStrategyRegistry.registerStrategy("oauth2-prod", enhancedOAuth2ProdStrategy);
            logger.info("[策略註冊] oauth2-prod策略註冊成功");

        } catch (Exception e) {
            logger.error("[策略註冊] OAuth2專用策略註冊失敗", e);
            throw e;
        }
    }

    /**
     * 註冊提供商特定策略
     */
    private void registerProviderSpecificStrategies() {
        logger.info("[策略註冊] 註冊提供商特定策略");

        try {
            // 這裡可以根據配置的OAuth2提供商動態註冊策略
            // 例如：google、github、microsoft等特定提供商策略

            // 為了示例，註冊一些常見的提供商策略
            registerProviderStrategy("google");
            registerProviderStrategy("github");
            registerProviderStrategy("microsoft");

            logger.info("[策略註冊] 提供商特定策略註冊完成");

        } catch (Exception e) {
            logger.error("[策略註冊] 提供商特定策略註冊失敗", e);
            // 不拋出異常，允許部分提供商策略註冊失敗
            logger.warn("[策略註冊] 部分提供商策略註冊失敗，但不影響整體功能");
        }
    }

    /**
     * 註冊單個提供商策略
     */
    private void registerProviderStrategy(String providerId) {
        try {
            // 為每個提供商創建專用的策略實例
            // 這裡使用相同的增強策略，但可以根據需要創建不同的實現
            globalStrategyRegistry.registerStrategy(providerId, enhancedOAuth2ProdStrategy);

            logger.debug("[策略註冊] 提供商策略註冊成功: {}", providerId);

        } catch (Exception e) {
            logger.warn("[策略註冊] 提供商策略註冊失敗: {}", providerId, e);
        }
    }

    /**
     * 獲取當前註冊的策略統計信息
     */
    public void logStrategyStatistics() {
        try {
            String[] registeredKeys = globalStrategyRegistry.getRegisteredStrategyKeys();
            logger.info("[策略統計] 當前已註冊的策略數量: {}", registeredKeys.length);
            logger.debug("[策略統計] 已註冊的策略列表: {}", String.join(", ", registeredKeys));

        } catch (Exception e) {
            logger.warn("[策略統計] 無法獲取策略統計信息", e);
        }
    }

    /**
     * 清理OAuth2策略（在模塊卸載時調用）
     */
    public void cleanupOAuth2Strategies() {
        logger.info("[策略清理] 開始清理OAuth2策略");

        try {
            // 移除OAuth2相關的策略
            globalStrategyRegistry.removeStrategy("oauth2");
            globalStrategyRegistry.removeStrategy("oauth2-prod");

            // 如果替換了prod策略，這裡可以考慮是否需要恢復原策略
            if (replaceProdStrategy) {
                logger.warn("[策略清理] 注意：增強的OAuth2 prod策略已被移除，需要確保有其他prod策略可用");
            }

            logger.info("[策略清理] OAuth2策略清理完成");

        } catch (Exception e) {
            logger.error("[策略清理] OAuth2策略清理失敗", e);
        }
    }
}