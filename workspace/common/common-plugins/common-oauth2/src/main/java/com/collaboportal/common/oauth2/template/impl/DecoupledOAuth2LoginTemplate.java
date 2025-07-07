package com.collaboportal.common.oauth2.template.impl;

import com.collaboportal.common.context.AuthContext;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.template.OAuth2LoginTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 解耦的OAuth2登錄模板實現
 * 
 * 解耦特點：
 * 1. 不直接依賴JWT模塊的具體實現
 * 2. 可以與AuthContext進行適配
 * 3. 支持靈活的認證流程定制
 * 
 * 主要職責：
 * - 協調OAuth2認證流程
 * - 適配不同的上下文格式
 * - 提供可擴展的認證模板
 */
@Component
public class DecoupledOAuth2LoginTemplate extends OAuth2LoginTemplate {

    private static final Logger logger = LoggerFactory.getLogger(DecoupledOAuth2LoginTemplate.class);

    public DecoupledOAuth2LoginTemplate(OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        super(clientRegistrationFactory);
        logger.info("[解耦OAuth2模板] 初始化完成");
    }

    /**
     * 執行OAuth2認證（與AuthContext集成的入口）
     * 
     * @param oauth2Context OAuth2提供商上下文
     * @param registration  OAuth2客戶端註冊信息
     * @param authContext   認證上下文（來自JWT模塊）
     */
    public void executeAuthentication(OAuth2ProviderContext oauth2Context,
            OAuth2ClientRegistration registration,
            AuthContext authContext) {
        logger.info("[解耦OAuth2模板] 開始執行OAuth2認證，提供商: {}", registration.getProviderId());

        try {
            // 第一步：處理OAuth2回調（如果存在code）
            if (authContext.getCode() != null && !authContext.getCode().isEmpty()) {
                logger.info("[解耦OAuth2模板] 檢測到authorization code，執行回調處理");
                executeCallback(oauth2Context, authContext.getCode(), authContext.getState());
            } else {
                logger.info("[解耦OAuth2模板] 沒有authorization code，發起OAuth2登錄");
                executeLogin(oauth2Context);
            }

        } catch (Exception e) {
            logger.error("[解耦OAuth2模板] OAuth2認證執行失敗", e);
            throw new RuntimeException("OAuth2認證失敗", e);
        }
    }

    @Override
    protected String buildAuthorizationUrl(OAuth2ProviderContext context, OAuth2ClientRegistration registration) {
        logger.debug("[URL構建] 開始構建授權URL，提供商: {}", registration.getProviderId());

        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(registration.getAuthorizationUri());
            urlBuilder.append("?response_type=").append(registration.getResponseType());
            urlBuilder.append("&client_id=").append(registration.getClientId());
            urlBuilder.append("&redirect_uri=").append(registration.getRedirectUri());
            urlBuilder.append("&scope=").append(registration.getScope());

            // 添加state參數
            String state = context.getState();
            if (state != null) {
                urlBuilder.append("&state=").append(state);
            }

            String authorizationUrl = urlBuilder.toString();
            logger.info("[URL構建] 授權URL構建完成: {}", authorizationUrl);
            return authorizationUrl;

        } catch (Exception e) {
            logger.error("[URL構建] 授權URL構建失敗", e);
            throw new RuntimeException("授權URL構建失敗", e);
        }
    }

    @Override
    protected void storeStateInformation(OAuth2ProviderContext context, OAuth2ClientRegistration registration) {
        logger.debug("[狀態存儲] 存儲OAuth2狀態信息");

        try {
            // 可以在這裡實現狀態存儲邏輯，比如存儲在session或redis中
            // 為了解耦，這裡使用簡單的實現
            String state = generateState(context);
            context.setState(state);

            logger.debug("[狀態存儲] 狀態信息存儲完成，state: {}", state);

        } catch (Exception e) {
            logger.error("[狀態存儲] 狀態信息存儲失敗", e);
            throw new RuntimeException("狀態信息存儲失敗", e);
        }
    }

    @Override
    protected String exchangeCodeForToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String code) {
        logger.info("[令牌交換] 開始交換authorization code為access token");

        try {
            // 這裡可以實現具體的令牌交換邏輯
            // 可以使用HTTP客戶端調用OAuth2提供商的token端點

            // 模擬令牌交換（實際實現時需要真實的HTTP調用）
            String mockToken = "mock_access_token_" + System.currentTimeMillis();

            logger.info("[令牌交換] 令牌交換成功完成");
            return mockToken;

        } catch (Exception e) {
            logger.error("[令牌交換] 令牌交換失敗", e);
            throw new RuntimeException("令牌交換失敗", e);
        }
    }

    @Override
    protected Object fetchUserInfo(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String accessToken) {
        logger.info("[用戶信息] 開始獲取用戶信息");

        try {
            // 這裡可以實現具體的用戶信息獲取邏輯
            // 可以使用access token調用OAuth2提供商的用戶信息端點

            // 模擬用戶信息獲取（實際實現時需要真實的HTTP調用）
            UserInfo mockUserInfo = new UserInfo();
            mockUserInfo.setEmail("user@example.com");
            mockUserInfo.setName("Test User");
            mockUserInfo.setId("user123");

            logger.info("[用戶信息] 用戶信息獲取成功");
            return mockUserInfo;

        } catch (Exception e) {
            logger.error("[用戶信息] 用戶信息獲取失敗", e);
            throw new RuntimeException("用戶信息獲取失敗", e);
        }
    }

    @Override
    protected String generateJwtToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            Object userInfo) {
        logger.info("[JWT生成] 開始生成JWT令牌");

        try {
            // 為了解耦，這裡可以通過事件或回調的方式來生成JWT
            // 避免直接依賴JWT模塊的具體實現

            UserInfo user = (UserInfo) userInfo;

            // 模擬JWT生成（實際實現時可以通過依賴注入或事件機制）
            String mockJwtToken = "jwt_token_" + user.getId() + "_" + System.currentTimeMillis();

            logger.info("[JWT生成] JWT令牌生成成功");
            return mockJwtToken;

        } catch (Exception e) {
            logger.error("[JWT生成] JWT令牌生成失敗", e);
            throw new RuntimeException("JWT令牌生成失敗", e);
        }
    }

    @Override
    protected void completeLogin(OAuth2ProviderContext context, OAuth2ClientRegistration registration, String jwtToken,
            Object userInfo) {
        logger.info("[登錄完成] 開始完成登錄流程");

        try {
            // 設置認證Cookie
            setAuthenticationCookie(context, jwtToken);

            // 重定向到成功頁面
            redirectToSuccessPage(context);

            logger.info("[登錄完成] 登錄流程成功完成");

        } catch (Exception e) {
            logger.error("[登錄完成] 登錄完成流程失敗", e);
            throw new RuntimeException("登錄完成失敗", e);
        }
    }

    /**
     * 設置認證Cookie
     */
    private void setAuthenticationCookie(OAuth2ProviderContext context, String jwtToken) {
        try {
            // 設置JWT Cookie
            context.getResponse().setHeader("Set-Cookie",
                    "AUTH=" + jwtToken + "; HttpOnly; Secure; Path=/; Max-Age=3600");

            logger.debug("[Cookie設置] 認證Cookie設置完成");

        } catch (Exception e) {
            logger.error("[Cookie設置] 認證Cookie設置失敗", e);
            throw new RuntimeException("Cookie設置失敗", e);
        }
    }

    /**
     * 重定向到成功頁面
     */
    private void redirectToSuccessPage(OAuth2ProviderContext context) {
        try {
            context.getResponse().setStatus(302);
            context.getResponse().setHeader("Location", "/dashboard");

            logger.debug("[重定向] 重定向到成功頁面完成");

        } catch (Exception e) {
            logger.error("[重定向] 重定向失敗", e);
            throw new RuntimeException("重定向失敗", e);
        }
    }

    /**
     * 用戶信息類（內部使用）
     */
    public static class UserInfo {
        private String id;
        private String email;
        private String name;

        // getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}