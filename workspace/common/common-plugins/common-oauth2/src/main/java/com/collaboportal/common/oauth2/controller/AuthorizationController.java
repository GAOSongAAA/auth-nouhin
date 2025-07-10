package com.collaboportal.common.oauth2.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.annotation.IpRestricted;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final LoginStrategyRegistry loginStrategyRegistry;

    Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    public AuthorizationController(LoginStrategyRegistry loginStrategyRegistry) {
        this.loginStrategyRegistry = loginStrategyRegistry;
        logger.debug("AuthorizationControllerの初期化が完了し、リクエストの監視を開始しました");
    }

    @GetMapping("/callback")
    @IpRestricted(allowedIps = {
            "127.0.0.1", // 本地IP
            "192.168.1.0/24", // 內網CIDR範圍
            "10.0.0.1-10.0.0.100" // IP範圍
    }, message = "此API僅限於指定IP地址存取")
    public void login(
            @RequestParam(value = "email", required = false) String emailFromForm,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String code,
            @RequestParam String state,
            @CookieValue(name = Message.Cookie.AUTH_STATE, required = false) String authState,
            @CookieValue(name = "MoveURL", required = false) String moveUrl) {

        logger.info("[認証コールバック] リクエストの処理を開始します。環境フラグ: {}", ConfigManager.getConfig().getEnvFlag());
        logger.debug("[認証コールバック] 受信パラメータ - メール: {}, 認証コード: {}, 状態: {}, 認証状態: {}, リダイレクトURL: {}",
                emailFromForm, code, state, authState, moveUrl);

        OAuth2ProviderContext context = OAuth2ProviderContext.builder()
                .email(emailFromForm)
                .code(code)
                .state(state)
                .request(request)
                .response(response)
                .build();
        Map<String, String> items = JwtTokenUtil.getItemsJwtToken(authState);

        logger.debug("[認証コールバック] 認証状態: {}", items);
        context.setClientId(items.get(Message.ContextInfo.AUTH_STATE));
        context.setClientSecret(items.get(Message.ContextInfo.AUTH_STATE));
        context.setAudience(items.get(Message.ContextInfo.AUDIENCE));
        context.setScope(items.get(Message.ContextInfo.SCOPE));
        context.setToken(items.get(Message.ContextInfo.TOKEN));

        String strategyKey = ("0".equals(ConfigManager.getConfig().getEnvFlag())) ? "test" : "prod";
        logger.debug("[認証コールバック] 使用するステージング: {}", strategyKey);

        try {
            logger.info("[認証コールバック] ログイン戦略の実行を開始します");
            loginStrategyRegistry.getStrategy(strategyKey).login(context);
            logger.info("[認証コールバック] ログイン戦略の実行が完了しました");
        } catch (Exception e) {
            logger.error("[認証コールバック] ログイン戦略の実行中に例外が発生しました: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 管理員專用API - 僅限特定IP存取
     * 展示IP限制功能的使用範例
     */
    @GetMapping("/admin/status")
    @IpRestricted(allowedIps = {
            "192.168.1.100", // 管理員工作站IP
            "10.0.0.0/8" // 內部網路
    }, message = "管理員API僅限內部網路存取", enabled = true)
    public Map<String, Object> getAdminStatus(HttpServletRequest request) {
        logger.info("管理員狀態API被存取，客戶端IP: {}",
                request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For")
                        : request.getRemoteAddr());

        return Map.of(
                "status", "success",
                "message", "管理員API存取成功",
                "timestamp", java.time.Instant.now().toString(),
                "clientIp", request.getRemoteAddr());
    }

    /**
     * 展示停用IP限制的範例
     */
    @GetMapping("/public/info")
    @IpRestricted(allowedIps = { "192.168.1.1" }, // 設定了IP但被停用
            enabled = false // 停用IP限制
    )
    public Map<String, Object> getPublicInfo() {
        logger.info("公開API被存取");
        return Map.of(
                "status", "success",
                "message", "這是公開API，IP限制已停用",
                "timestamp", java.time.Instant.now().toString());
    }

}
