package com.collaboportal.common.jwt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.AuthContext;
import com.collaboportal.common.strategy.AuthenticationStrategyRegistry;
import com.collaboportal.common.strategy.LoginStrategy;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationStrategyRegistry globalStrategyRegistry;
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationStrategyRegistry globalStrategyRegistry) {
        this.globalStrategyRegistry = globalStrategyRegistry;
        logger.debug("[認證控制器] 初始化完成，開始監聽請求");
    }

    @GetMapping("/callback")
    public void collaboLogin(
            @RequestParam(value = "email", required = false) String emailFromForm,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String code,
            @RequestParam String state,
            @CookieValue(name = Message.Cookie.APP_VER, required = false) String appVer,
            @CookieValue(name = "MoveURL", required = false) String moveUrl) {

        logger.info("[認証コールバック] リクエストの処理を開始します。環境フラグ: {}", ConfigManager.getConfig().getEnvFlag());
        logger.debug("[認証コールバック] 受信パラメータ - メール: {}, 認証コード: {}, 状態: {}, アプリバージョン: {}, リダイレクトURL: {}",
                emailFromForm, code, state, appVer, moveUrl);

        AuthContext context = AuthContext.builder()
                .email(emailFromForm)
                .code(code)
                .state(state)
                .moveUrl(moveUrl)
                .request(request)
                .response(response)
                .build();

        String strategyKey = ("0".equals(ConfigManager.getConfig().getEnvFlag())) ? "test" : "prod";
        logger.debug("[認証ステージング] 使用するステージング: {}", strategyKey);

        try {
            logger.info("[認證流程] 開始執行登錄策略");
            LoginStrategy strategy = globalStrategyRegistry.getStrategy(strategyKey);
            if (strategy == null) {
                logger.error("[認證錯誤] 未找到策略: {}", strategyKey);
                throw new IllegalStateException("未找到認證策略: " + strategyKey);
            }
            strategy.login(context);
            logger.info("[認證流程] 登錄流程完成");
        } catch (Exception e) {
            logger.error("[認證錯誤] 登錄流程中發生異常: {}", e.getMessage(), e);
            throw e;
        }
    }

}
