package com.collaboportal.common.jwt.logic.callbacklogin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.context.AuthContext;

import com.collaboportal.common.jwt.entity.UserMaster;

import com.collaboportal.common.jwt.model.OauthTokenResult;
import com.collaboportal.common.jwt.service.AuthService;
import com.collaboportal.common.jwt.service.JwtUserMasterDetailsService;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.utils.Message;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ログインストラテジーの初期化クラス
 */
@Component
public class LoginStrategyInitializer {

    // ログインストラテジーのレジストリ
    private final LoginStrategyRegistry registry;
    // ユーザーマスター詳細サービス
    private final JwtUserMasterDetailsService jwtUserMasterDetailsService;
    // JWTトークン生成ユーティリティ
    private final JwtTokenUtil jwtTokenUtil;
    // 認証サービス
    private final AuthService authService;


    // ロガー
    private Logger logger = LoggerFactory.getLogger(LoginStrategyInitializer.class);

    /**
     * コンストラクタ
     * 
     * @param registry                        ログインストラテジーレジストリ
     * @param authService                     認証サービス
     * @param configManager                   設定マネージャー
     * @param jwtEmployeeMasterDetailsService 従業員マスター詳細サービス
     * @param jwtUserMasterDetailsService     ユーザーマスター詳細サービス
     * @param jwtTokenUtil                    JWTトークン生成ユーティリティ
     */
    public LoginStrategyInitializer(LoginStrategyRegistry registry,
            AuthService authService,
            JwtUserMasterDetailsService jwtUserMasterDetailsService,
            JwtTokenUtil jwtTokenUtil) {
        this.registry = registry;
        this.authService = authService;
        this.jwtUserMasterDetailsService = jwtUserMasterDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 初期化処理
     * テスト環境と本番環境のログインストラテジーを登録
     */
    @PostConstruct
    public void init() {
        logger.info("ログインストラテジーの初期化を開始します");
        registry.register("test", (context) -> handleTestLogin((AuthContext) context));
        registry.register("prod", (context) -> handleProdLogin((AuthContext) context));
        logger.info("ログインストラテジーの初期化が完了しました");
    }

    /**
     * テスト環境ログイン処理
     * 
     * @param context 認証コンテキスト
     */
    private void handleTestLogin(AuthContext context) {
        HttpServletResponse response = context.getResponse();
        try {
            String email = context.getEmail();
            logger.info("テスト環境ログインプロセスを開始します。メールアドレス: {}", email);

            // ユーザー情報を取得
            UserMaster user = jwtUserMasterDetailsService.loadByEmail(email);
            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", email);

            // JWTトークンを生成
            String token = jwtTokenUtil.generateTokenForMr(user);
            logger.debug("JWTトークンの生成に成功しました");

            // 認証情報をCookieに設定
            setCookie(response, Message.Cookie.AUTH, token);
            setCookie(response, Message.Cookie.HONBU_FLAG, user.getUserType());
            logger.debug("認証用Cookieの設定に成功しました");

            // リダイレクト処理
            redirect(response, ConfigManager.getConfig().getIndexPage());
            logger.info("テスト環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (Exception e) {
            logger.error("テスト環境ログインに失敗しました", e);
            setCookie(response, "MoveURL", "/#/error");
            redirect(response, ConfigManager.getConfig().getIndexPage());
            logger.error("テスト環境ログインに失敗しました。エラーページにリダイレクトします");
        }
    }

    /**
     * 本番環境ログイン処理
     * 
     * @param context 認証コンテキスト
     */
    private void handleProdLogin(AuthContext context) {
        HttpServletResponse response = context.getResponse();
        try {
            logger.info("本番環境ログインプロセスを開始します");

            // OAuthトークンを取得
            OauthTokenResult tokenResult = authService.getOauthTokenFromColaboportalApi(
                    "authorization_code", context.getCode(), ConfigManager.getConfig().getCallback(),
                    ConfigManager.getConfig().getCollaboportalAudience(),
                    authService.getClientId(), authService.getClientSecret(), response);

            if (!tokenResult.isSuccess()) {
                logger.error("OAuthトークンの取得に失敗しました");
                setCookie(response, "MoveURL", "/#/error");
                redirect(response, ConfigManager.getConfig().getIndexPage());
                return;
            }
            logger.debug("OAuthトークンの取得に成功しました");

            // ユーザー情報を取得
            UserMaster user = jwtUserMasterDetailsService.loadByEmail(tokenResult.getEmail());
            logger.debug("ユーザー情報の取得に成功しました。メールアドレス: {}", tokenResult.getEmail());

            // JWTトークンを生成
            String token = jwtTokenUtil.generateTokenForMr(user);
            logger.debug("JWTトークンの生成に成功しました");

            setCookie(response, Message.Cookie.AUTH, token);
            setCookie(response, Message.Cookie.HONBU_FLAG, user.getUserType());

            logger.debug("本番環境用認証Cookieの設定に成功しました");

            redirect(response, ConfigManager.getConfig().getIndexPage());
            logger.info("本番環境ログインに成功しました。インデックスページにリダイレクトします");
        } catch (Exception e) {
            logger.error("本番環境ログインに失敗しました", e);
            setCookie(response, "MoveURL", "/#/error");
            redirect(response, ConfigManager.getConfig().getIndexPage());
            logger.error("本番環境ログインに失敗しました。エラーページにリダイレクトします");
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

    /**
     * Cookie設定処理
     * 
     * @param response HTTPレスポンス
     * @param name     Cookie名
     * @param value    Cookie値
     */
    private void setCookie(HttpServletResponse response, String name, String value) {
        logger.debug("Cookieを設定します。{}={}", name, value);
        String secureFlag = (ConfigManager.getConfig().isCookieSecure()) ? "; Secure" : "";
        response.addHeader("Set-Cookie", name + "=" + value + "; Path=/; Max-Age="
                + ConfigManager.getConfig().getCookieExpiration() + "; SameSite=None" + secureFlag);
    }
}
