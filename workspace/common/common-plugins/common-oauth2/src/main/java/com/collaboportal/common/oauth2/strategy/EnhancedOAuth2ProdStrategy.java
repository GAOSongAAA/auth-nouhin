package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.AuthContext;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.template.impl.DecoupledOAuth2LoginTemplate;
import com.collaboportal.common.strategy.LoginStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 增強的OAuth2產品環境策略
 * 
 * 核心功能：
 * 1. 集成 OAuth2 多提供商認證
 * 2. 替代傳統的prod策略，提供更豐富的認證選項
 * 3. 支持動態提供商選擇和故障轉移
 * 
 * 設計亮點：
 * - 完全解耦：不依賴JWT模塊的具體實現
 * - 智能路由：根據上下文自動選擇最適合的OAuth2提供商
 * - 故障恢復：當OAuth2失敗時，可以降級到基礎prod策略
 */
@Component
public class EnhancedOAuth2ProdStrategy implements LoginStrategy {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedOAuth2ProdStrategy.class);

    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;
    private final OAuth2ProviderSelectionStrategyComposer providerSelectionComposer;
    private final DecoupledOAuth2LoginTemplate oauth2LoginTemplate;

    // 故障轉移配置
    private static final boolean ENABLE_FALLBACK = true;
    private static final String FALLBACK_PROVIDER = "default";

    public EnhancedOAuth2ProdStrategy(
            OAuth2ClientRegistrationFactory clientRegistrationFactory,
            OAuth2ProviderSelectionStrategyComposer providerSelectionComposer,
            DecoupledOAuth2LoginTemplate oauth2LoginTemplate) {
        this.clientRegistrationFactory = clientRegistrationFactory;
        this.providerSelectionComposer = providerSelectionComposer;
        this.oauth2LoginTemplate = oauth2LoginTemplate;

        logger.info("[增強OAuth2策略] 初始化完成，支持 {} 個提供商",
                clientRegistrationFactory.getAllClientRegistrations().size());
    }

    @Override
    public void login(AuthContext context) {
        logger.info("[增強OAuth2策略] 開始執行增強型OAuth2產品認證流程");

        // 轉換上下文格式
        OAuth2ProviderContext oauth2Context = buildOAuth2Context(context);

        try {
            // 第一步：智能選擇OAuth2提供商
            String selectedProvider = selectOptimalProvider(oauth2Context);

            if (selectedProvider == null) {
                logger.warn("[增強OAuth2策略] 無法選擇OAuth2提供商，使用故障轉移");
                handleProviderSelectionFailure(oauth2Context);
                return;
            }

            oauth2Context.setSelectedProviderId(selectedProvider);
            logger.info("[增強OAuth2策略] 選定OAuth2提供商: {}", selectedProvider);

            // 第二步：驗證提供商配置
            OAuth2ClientRegistration registration = validateProviderConfiguration(selectedProvider);

            if (registration == null) {
                logger.error("[增強OAuth2策略] 提供商配置無效: {}", selectedProvider);
                handleConfigurationError(oauth2Context, selectedProvider);
                return;
            }

            // 第三步：執行OAuth2認證流程
            executeOAuth2Authentication(oauth2Context, registration, context);

            logger.info("[增強OAuth2策略] OAuth2認證流程成功完成");

        } catch (Exception e) {
            logger.error("[增強OAuth2策略] OAuth2認證過程中發生異常", e);
            handleAuthenticationException(oauth2Context, e);
        }
    }

    /**
     * 構建OAuth2上下文
     */
    private OAuth2ProviderContext buildOAuth2Context(AuthContext authContext) {
        return new OAuth2ProviderContext.Builder()
                .request(authContext.getRequest())
                .response(authContext.getResponse())
                .build();
    }

    /**
     * 智能選擇最優OAuth2提供商
     */
    private String selectOptimalProvider(OAuth2ProviderContext context) {
        logger.debug("[提供商選擇] 開始智能提供商選擇流程");

        try {
            // 使用策略組合器進行智能選擇
            String provider = providerSelectionComposer.selectProvider(context);

            if (provider != null) {
                logger.info("[提供商選擇] 策略組合器選擇提供商: {}", provider);
                return provider;
            }

            // 如果策略組合器無法選擇，使用默認邏輯
            return selectDefaultProvider();

        } catch (Exception e) {
            logger.warn("[提供商選擇] 提供商選擇過程中發生異常，使用默認提供商", e);
            return selectDefaultProvider();
        }
    }

    /**
     * 選擇默認提供商
     */
    private String selectDefaultProvider() {
        var registrations = clientRegistrationFactory.getAllClientRegistrations();

        if (registrations.isEmpty()) {
            logger.warn("[提供商選擇] 沒有可用的OAuth2提供商配置");
            return null;
        }

        // 優先選擇第一個可用的提供商
        String defaultProvider = registrations.keySet().iterator().next();
        logger.info("[提供商選擇] 使用默認提供商: {}", defaultProvider);
        return defaultProvider;
    }

    /**
     * 驗證提供商配置
     */
    private OAuth2ClientRegistration validateProviderConfiguration(String providerId) {
        OAuth2ClientRegistration registration = clientRegistrationFactory.getClientRegistration(providerId);

        if (registration == null) {
            logger.error("[配置驗證] 提供商配置不存在: {}", providerId);
            return null;
        }

        // 驗證必要的配置項
        if (registration.getClientId() == null || registration.getClientSecret() == null) {
            logger.error("[配置驗證] 提供商配置不完整: {}", providerId);
            return null;
        }

        logger.debug("[配置驗證] 提供商配置驗證通過: {}", providerId);
        return registration;
    }

    /**
     * 執行OAuth2認證流程
     */
    private void executeOAuth2Authentication(OAuth2ProviderContext oauth2Context,
            OAuth2ClientRegistration registration,
            AuthContext authContext) {
        logger.info("[OAuth2認證] 開始執行OAuth2認證流程");

        try {
            // 使用解耦的OAuth2登錄模板執行認證
            oauth2LoginTemplate.executeAuthentication(oauth2Context, registration, authContext);

        } catch (Exception e) {
            logger.error("[OAuth2認證] OAuth2認證執行失敗", e);
            throw e;
        }
    }

    /**
     * 處理提供商選擇失敗
     */
    private void handleProviderSelectionFailure(OAuth2ProviderContext context) {
        logger.warn("[故障處理] OAuth2提供商選擇失敗，啟動故障轉移流程");

        if (ENABLE_FALLBACK) {
            // 這裡可以實現降級到基礎認證策略
            redirectToErrorPage(context, "無可用的OAuth2提供商");
        } else {
            redirectToErrorPage(context, "認證服務暫時不可用");
        }
    }

    /**
     * 處理配置錯誤
     */
    private void handleConfigurationError(OAuth2ProviderContext context, String providerId) {
        logger.error("[故障處理] OAuth2提供商配置錯誤: {}", providerId);
        redirectToErrorPage(context, "認證配置錯誤");
    }

    /**
     * 處理認證異常
     */
    private void handleAuthenticationException(OAuth2ProviderContext context, Exception e) {
        logger.error("[故障處理] OAuth2認證異常", e);
        redirectToErrorPage(context, "認證過程發生錯誤");
    }

    /**
     * 重定向到錯誤頁面
     */
    private void redirectToErrorPage(OAuth2ProviderContext context, String message) {
        try {
            String errorUrl = ConfigManager.getConfig().getIndexPage() + "#/error?message=" + message;
            context.getResponse().setStatus(302);
            context.getResponse().setHeader("Location", errorUrl);
        } catch (Exception e) {
            logger.error("[錯誤處理] 重定向到錯誤頁面失敗", e);
        }
    }
}