package com.collaboportal.common.oauth2.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CallbackContext;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.utils.Message;



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
    public void login(
            @RequestParam(value = "email", required = false) String emailFromForm,
            @RequestParam String code,
            @RequestParam String state,
            @CookieValue(name = Message.Cookie.AUTH_STATE, required = false) String authStateToken,
            @CookieValue(name = "MoveURL", required = false) String moveUrl) {

        logger.info("[認証コールバック] リクエストの処理を開始します。環境フラグ: {}", ConfigManager.getConfig().getEnvFlag());
        logger.debug("[認証コールバック] 受信パラメータ - メール: {}, 認証コード: {}, 状態: {}, 認証状態: {}, リダイレクトURL: {}",
                emailFromForm, code, state, authStateToken, moveUrl);
        BaseRequest request = CommonHolder.getRequest();
        BaseResponse response = CommonHolder.getResponse();
        CallbackContext context = CallbackContext.builder()
                .emailFromForm(emailFromForm)
                .code(code)
                .state(state)
                .request(request)
                .response(response)
                .build();
        Map<String, String> items = JwtTokenUtil.getItemsJwtToken(authStateToken);

        logger.debug("[認証コールバック] 認証状態: {}", items);
        context.setClientId(items.get(Message.ContextInfo.CLIENT_ID));
        context.setClientSecret(items.get(Message.ContextInfo.CLIENT_SECRET));
        context.setAudience(items.get(Message.ContextInfo.AUDIENCE));
        logger.debug("[認証コールバック] クライアントID: {}", context);
        String strategyKey = ("0".equals(ConfigManager.getConfig().getEnvFlag())) ? "test" : "prod";
        logger.debug("[認証コールバック] 使用するステージング: {}", strategyKey);

        try {
            logger.info("[認証コールバック] ログイン戦略の実行を開始します");
            loginStrategyRegistry.getStrategy(strategyKey).run(context);
            logger.info("[認証コールバック] ログイン戦略の実行が完了しました");
        } catch (Exception e) {
            logger.error("[認証コールバック] ログイン戦略の実行中に例外が発生しました: {}", e.getMessage(), e);
            throw e;
        }
    }

}
 