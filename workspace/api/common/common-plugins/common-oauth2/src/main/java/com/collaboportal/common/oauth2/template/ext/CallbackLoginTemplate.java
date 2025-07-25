package com.collaboportal.common.oauth2.template.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CallbackContext;
import com.collaboportal.common.context.web.BaseCookie;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.entity.DTO.IUserInfoDto;
import com.collaboportal.common.oauth2.entity.DTO.UserInfo;
import com.collaboportal.common.oauth2.exception.OAuth2AuthenticationException;
import com.collaboportal.common.oauth2.exception.OAuth2AuthorizationException;
import com.collaboportal.common.oauth2.exception.OAuth2UserException;
import com.collaboportal.common.oauth2.factory.UserInfoServiceFactory;
import com.collaboportal.common.oauth2.model.OauthTokenResult;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.template.OAuth2LoginTemplate;
import com.collaboportal.common.oauth2.utils.CookieUtil;
import com.collaboportal.common.utils.Message;
import com.fasterxml.jackson.databind.JsonSerializable.Base;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

public class CallbackLoginTemplate extends OAuth2LoginTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CallbackLoginTemplate.class);
    private final AuthProcessor authProcessor;
    private final LoginStrategyRegistry strategyRegistry;

    private final JwtTokenUtil jwtTokenUtil;

    public CallbackLoginTemplate(AuthProcessor authProcessor, LoginStrategyRegistry strategyRegistry, JwtTokenUtil jwtTokenUtil) {
        this.strategyRegistry = strategyRegistry;
        this.authProcessor = authProcessor;
        this.jwtTokenUtil = jwtTokenUtil;
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
            if (email == null || email.isEmpty()) {
                throw new OAuth2AuthenticationException("テスト環境ログイン失敗：メールアドレスが空です");
            }

            logger.info("テスト環境ログインプロセスを開始します。メールアドレス: {}", email);

            UserInfo user = new UserInfo();
            user.setEmail(email);
            user.setUserId("123123123");

            if (user == null) {
                String errorMessage = String.format("ユーザー情報が見つかりません：%s", email);
                throw new OAuth2UserException(errorMessage, email);
            }

            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", email);
            String token = jwtTokenUtil.generateTokenFromObject(user);
            logger.debug("JWTトークンの生成に成功しました");

            // CookieUtil.setSameSiteCookie(response, Message.Cookie.AUTH, token);
            response.addCookie(new BaseCookie(Message.Cookie.AUTH, token).setPath("/").setMaxAge(ConfigManager.getConfig().getCookieExpiration()).setSameSite("None").setSecure(ConfigManager.getConfig().isCookieSecure()));
            logger.debug("認証用Cookieの設定に成功しました");

            redirect(response, context.getHomePage());
            logger.info("テスト環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (OAuth2AuthenticationException | OAuth2UserException e) {
            logger.warn("テスト環境ログイン OAuth2 例外: {}", e.getMessage());
            //CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");
            response.addCookie(new BaseCookie("MoveURL", "/#/error").setPath("/").setMaxAge(ConfigManager.getConfig().getCookieExpiration()).setSameSite("None").setSecure(ConfigManager.getConfig().isCookieSecure()));
            redirect(response, context.getHomePage());
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("テスト環境ログイン処理中に予期しないエラーが発生しました: %s", e.getMessage());
            logger.error("テスト環境ログインに失敗しました", e);
            //CookieUtil.setSameSiteCookie(response, "MoveURL", "/#/error");
            response.addCookie(new BaseCookie("MoveURL", "/#/error").setPath("/").setMaxAge(ConfigManager.getConfig().getCookieExpiration()).setSameSite("None").setSecure(ConfigManager.getConfig().isCookieSecure()));
            redirect(response, context.getHomePage());
            throw new OAuth2AuthenticationException(errorMessage, e);
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

            IUserInfoDto user = UserInfoServiceFactory.loadUserByEmail(context.getSelectedProviderId(), email);
            if (user == null) {
                String errorMessage = String.format("ユーザー情報が見つかりません：%s", email);
                throw new OAuth2UserException(errorMessage, email);
            }

            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", email);

            String token = jwtTokenUtil.generateTokenFromObject(user);
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