package com.collaboportal.common.oauth2.template.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.model.OauthTokenResult;
import com.collaboportal.common.jwt.service.AuthService;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.entity.DTO.IUserInfoDto;
import com.collaboportal.common.oauth2.factory.UserInfoServiceFactory;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.service.IUserInfoService;
import com.collaboportal.common.oauth2.utils.CookieUtil;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.HttpServletResponse;

public class CallbackLoginTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CallbackLoginTemplate.class);
    private final LoginStrategyRegistry strategyRegistry;
    private final AuthService authService;

    public CallbackLoginTemplate(AuthService authService) {
        this.strategyRegistry = new LoginStrategyRegistry();
        this.authService = authService;
        registerDefaultStrategies();
    }

    private void registerDefaultStrategies() {
        strategyRegistry.register("test", this::handleTestLogin);
        strategyRegistry.register("prod", this::handleProdLogin);
        logger.debug("デフォルトのログイン策略を登録しました: test, prod");
    }

    /**
     * テスト環境ログイン処理
     * 
     * @param context 認証コンテキスト
     */
    private void handleTestLogin(OAuth2ProviderContext context) {
        HttpServletResponse response = context.getResponse();
        try {
            String email = context.getEmail();
            logger.info("テスト環境ログインプロセスを開始します。メールアドレス: {}", email);

            IUserInfoDto user = UserInfoServiceFactory.loadUserByEmail(context.getSelectedProviderId(),
                    context.getEmail());
            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", email);
            String token = JwtTokenUtil.generateTokenFromObject(user);
            logger.debug("JWTトークンの生成に成功しました");

            CookieUtil.setSameSiteCookie(response, Message.Cookie.AUTH, token);
            logger.debug("認証用Cookieの設定に成功しました");

            // redirect(response, ConfigManager.getConfig().getIndexPage()); TODO: リダイレクト処理
            logger.info("テスト環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (Exception e) {
            logger.error("テスト環境ログインに失敗しました", e);
            CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");
            // redirect(response, ConfigManager.getConfig().getIndexPage()); TODO: リダイレクト処理
        }
    }

    /**
     * 本番環境ログイン処理
     * 
     * @param context 認証コンテキスト
     */
    private void handleProdLogin(OAuth2ProviderContext context) {
        HttpServletResponse response = context.getResponse();
        try {

            OauthTokenResult tokenResult = authService.getOauthTokenFromColaboportalApi(
                    "authorization_code",
                    context.getCode(),
                    context.getRedirectUri(),
                    context.getAudience(),
                    context.getClientId(),
                    context.getClientSecret(),
                    response);

            logger.info("本番環境ログインプロセスを開始します。メールアドレス: {}", email);
        } catch (Exception e) {
            logger.error("本番環境ログインに失敗しました", e);
        }
    }

}
