package com.collaboportal.common.jwt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.logic.callbacklogin.LoginStrategyRegistry;
import com.collaboportal.common.jwt.context.AuthContext;
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

    private final LoginStrategyRegistry loginStrategyRegistry;
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(LoginStrategyRegistry loginStrategyRegistry) {
        this.loginStrategyRegistry = loginStrategyRegistry;
        logger.debug("AuthControllerの初期化が完了し、リクエストの監視を開始しました");
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
            logger.info("[認証プロセス] ログイン戦略の実行を開始します");
            loginStrategyRegistry.getStrategy(strategyKey).login(context);
            logger.info("[認証プロセス] ログインプロセスが完了しました");
        } catch (Exception e) {
            logger.error("[認証エラー] ログインプロセス中に例外が発生しました: {}", e.getMessage(), e);
            throw e;
        }
    }

}
