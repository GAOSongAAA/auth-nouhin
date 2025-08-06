package com.collaboportal.common.oauth2.template.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.oauth2.CallbackContext;

import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.constants.JwtConstants;

import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.CookieUtil;

import com.collaboportal.common.oauth2.entity.DTO.UserMasterCollabo;
import com.collaboportal.common.oauth2.exception.OAuth2AuthenticationException;
import com.collaboportal.common.oauth2.exception.OAuth2AuthorizationException;
import com.collaboportal.common.oauth2.exception.OAuth2UserException;

import com.collaboportal.common.oauth2.model.OauthTokenResult;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.service.Oauth2UserMasterService;
import com.collaboportal.common.oauth2.template.OAuth2LoginTemplate;
import com.collaboportal.common.utils.Message;

import jakarta.annotation.PostConstruct;

public class CallbackLoginTemplate extends OAuth2LoginTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CallbackLoginTemplate.class);
    private final AuthProcessor authProcessor;
    private final LoginStrategyRegistry strategyRegistry;
    private final Oauth2UserMasterService oauth2UserMasterService;
    private final JwtService jwtService;

    public CallbackLoginTemplate(AuthProcessor authProcessor, Oauth2UserMasterService oauth2UserMasterService,
            LoginStrategyRegistry strategyRegistry,
            JwtService jwtService) {
        this.strategyRegistry = strategyRegistry;
        this.oauth2UserMasterService = oauth2UserMasterService;
        this.authProcessor = authProcessor;
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void init() {
        logger.info("ログインストラテジーの初期化を開始します");
        strategyRegistry.register("test", (context) -> handleTestLogin((CallbackContext) context));
        strategyRegistry.register("prod", (context) -> handleProdLogin((CallbackContext) context));
        logger.info("ログインストラテジーの初期化が完了しました");
    }

    /**
     * テスト環境ログイン処理
     * 
     * @param context 認証コンテキスト
     */
    private void handleTestLogin(CallbackContext context) {
        BaseResponse response = context.getResponse();
        try {
            String email = context.getEmailFromForm();
            logger.info("テスト環境ログインプロセスを開始します。メールアドレス: {}", email);

            // ユーザー情報を取得
            UserMasterCollabo user = oauth2UserMasterService.loadByEmail(email);
            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", email);

            // JWTトークンを生成
            String token = jwtService.generateToken(user, JwtConstants.GENERATE_INTERNAL_TOKEN);
            logger.debug("JWTトークンの生成に成功しました");

            // 認証情報をCookieに設定
            CookieUtil.setNoneSameSiteCookie(response, Message.Cookie.AUTH, token);
            CookieUtil.setNoneSameSiteCookie(response, Message.Cookie.HONBU_FLAG, user.getUserType());
            logger.debug("認証用Cookieの設定に成功しました");

            // リダイレクト処理
            redirect(response, ConfigManager.getConfig().getIndexPage());
            logger.info("テスト環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (Exception e) {
            logger.error("テスト環境ログインに失敗しました", e);
            CookieUtil.setNoneSameSiteCookie(response, "MoveURL", "/#/error");
            response.redirect(ConfigManager.getConfig().getIndexPage());
            logger.error("テスト環境ログインに失敗しました。エラーページにリダイレクトします");
        }
    }

    /**
     * 本番環境ログイン処理
     * 
     * @param context 認証コンテキスト
     */
    private void handleProdLogin(CallbackContext context) {
        BaseResponse response = context.getResponse();
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
                String errorMessage = "OAuth 認可コード交換失敗";
                logger.error("OAuthトークンの取得に失敗しました");
                CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");
                throw new OAuth2AuthorizationException(errorMessage, tokenResult);
            }

            String email = tokenResult.getEmail();
            if (email == null || email.isEmpty()) {
                throw new OAuth2AuthorizationException("OAuth レスポンスにユーザーメールアドレスがありません", tokenResult);
            }

            UserMasterCollabo user = oauth2UserMasterService.loadByEmail(email);
            if (user == null) {
                String errorMessage = String.format("ユーザー情報が見つかりません：%s", email);
                throw new OAuth2UserException(errorMessage, email);
            }

            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", email);

            String token = jwtService.generateToken(user, JwtConstants.GENERATE_INTERNAL_TOKEN);
            logger.debug("JWTトークンの生成に成功しました");

            CookieUtil.setSameSiteCookie(response, Message.Cookie.AUTH, token);
            logger.debug("認証用Cookieの設定に成功しました");

            redirect(response, context.getHomePage());
            logger.info("本番環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (OAuth2AuthorizationException | OAuth2UserException e) {
            logger.warn("本番環境ログイン OAuth2 例外: {}", e.getMessage());
            CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("本番環境ログイン処理中に予期しないエラーが発生しました: %s", e.getMessage());
            logger.error("本番環境ログインに失敗しました", e);
            CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");
            throw new OAuth2AuthenticationException(errorMessage, e);
        }
    }

    /**
     * リダイレクト処理
     * 
     * @param response HTTPレスポンス
     * @param url      リダイレクト先URL
     */
    private void redirect(BaseResponse response, String url) {
        logger.debug("リダイレクトを実行します。URL: {}", url);
        response.setHeader("Location", url);
        response.setStatus(302);
    }

}