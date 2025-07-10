package com.collaboportal.common.oauth2.template.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.jwt.model.OauthTokenResult;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.entity.DTO.IUserInfoDto;
import com.collaboportal.common.oauth2.factory.UserInfoServiceFactory;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.template.OAuth2LoginTemplate;
import com.collaboportal.common.oauth2.utils.CookieUtil;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.HttpServletResponse;

public class CallbackLoginTemplate extends OAuth2LoginTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CallbackLoginTemplate.class);
    private final LoginStrategyRegistry strategyRegistry;
    private final AuthProcessor authProcessor;

    public CallbackLoginTemplate(AuthProcessor authProcessor) {
        this.strategyRegistry = new LoginStrategyRegistry();
        this.authProcessor = authProcessor;
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

            redirect(response, context.getHomePage());
            logger.info("テスト環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (Exception e) {
            logger.error("テスト環境ログインに失敗しました", e);
            CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");
            redirect(response, context.getHomePage());
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

            OauthTokenResult tokenResult = authProcessor.getOauthTokenFromEndpoint(
                    "authorization_code",
                    context.getCode(),
                    context.getRedirectUri(),
                    context.getAudience(),
                    context.getClientId(),
                    context.getClientSecret(),
                    response);

            if (!tokenResult.isSuccess()) {
                logger.error("OAuthトークンの取得に失敗しました");
                CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");

                return;
            }

            IUserInfoDto user = UserInfoServiceFactory.loadUserByEmail(context.getSelectedProviderId(),
                    tokenResult.getEmail());
            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", tokenResult.getEmail());

            String token = JwtTokenUtil.generateTokenFromObject(user);
            logger.debug("JWTトークンの生成に成功しました");

            CookieUtil.setSameSiteCookie(response, Message.Cookie.AUTH, token);
            logger.debug("認証用Cookieの設定に成功しました");

            redirect(response, context.getHomePage());
            logger.info("本番環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (Exception e) {
            logger.error("本番環境ログインに失敗しました", e);
        }
    }

    /**
     * リダイレクト処理
     * 
     * @param response HTTPレスポンス
     * @param url      リダイレクト先URL
     */
    private void redirect(HttpServletResponse response, String url) {
        logger.debug("リダイレクトを実行します。URL: {}", url);
        response.setHeader("Location", url);
        response.setStatus(302);
    }

}