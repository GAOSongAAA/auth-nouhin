// ========== テンプレートメソッド抽象基底クラス - OAuth2ログインテンプレート ==========
package com.collaboportal.common.oauth2.template;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * テンプレートメソッドパターン - OAuth2ログインフロー抽象テンプレート
 * 責務：OAuth2ログインの標準フローを定義し、変化点を抽象化してサブクラスで実装
 * 利点：統一されたフロー制御、コード重複の削減、保守・拡張の容易性
 */
public abstract class OAuth2LoginTemplate {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public OAuth2LoginTemplate(OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.clientRegistrationFactory = clientRegistrationFactory;
    }

    /**
     * mainロジック処理
     */
    public abstract void executeLogin(OAuth2ProviderContext context);

    /**
     * コールバックロジック処理
     */
    public abstract void executeCallback(OAuth2ProviderContext context, String code, String state);

    // ========== 具象メソッド - サブクラスでオーバーライド可能だがデフォルト実装あり ==========

    /**
     * ログインリクエストの検証 - サブクラスでオーバーライド可能
     */
    protected boolean validateLoginRequest(OAuth2ProviderContext context) {
        return context.getSelectedProviderId() != null &&
                context.getRequest() != null &&
                context.getResponse() != null;
    }

    /**
     * コールバックリクエストの検証 - サブクラスでオーバーライド可能
     */
    protected boolean validateCallbackRequest(OAuth2ProviderContext context, String code, String state) {
        return code != null && !code.isEmpty() &&
                state != null && !state.isEmpty();
    }

    /**
     * クライアント登録情報の取得
     */
    protected OAuth2ClientRegistration getClientRegistration(OAuth2ProviderContext context) {
        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    /**
     * 状態パラメータの生成 - サブクラスでオーバーライド可能
     */
    protected String generateState(OAuth2ProviderContext context) {
        return UUID.randomUUID().toString();
    }

    /**
     * リダイレクトの実行
     */
    protected void performRedirect(OAuth2ProviderContext context, String authorizationUrl) throws IOException {
        HttpServletResponse response = context.getResponse();
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", authorizationUrl);
    }

    /**
     * ログインエラーの処理 - サブクラスでオーバーライド可能
     */
    protected void handleLoginError(OAuth2ProviderContext context, String errorMessage) {
        try {
            HttpServletResponse response = context.getResponse();
            setCookie(response, "MoveURL", "/#/error");
        } catch (Exception e) {
            logger.error("ログインエラーの処理中に例外が発生しました", e);
        }
    }

    protected void setCookie(HttpServletResponse response, String name, String value) {
        logger.debug("Cookieを設定します。{}={}", name, value);
        String secureFlag = (ConfigManager.getConfig().isCookieSecure()) ? "; Secure" : "";
        response.addHeader("Set-Cookie", name + "=" + value + "; Path=/; Max-Age="
                + ConfigManager.getConfig().getCookieExpiration() + "; SameSite=None" + secureFlag);
    }

    /**
     * コールバックエラーの処理 - サブクラスでオーバーライド可能
     */
    protected void handleCallbackError(OAuth2ProviderContext context, String errorMessage) {
        try {
            HttpServletResponse response = context.getResponse();
            setCookie(response, "MoveURL", "/#/error");
        } catch (Exception e) {
            logger.error("コールバックエラーの処理中に例外が発生しました", e);
        }
    }

    // ========== 抽象メソッド - サブクラスで必ず実装 ==========

    /**
     * 状態情報の保存 - サブクラスで必ず実装
     */
    protected abstract void storeStateInformation(OAuth2ProviderContext context, OAuth2ClientRegistration registration);

    /**
     * 認可コードとアクセストークンの交換 - サブクラスで必ず実装
     */
    protected abstract String exchangeCodeForToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String code);

    /**
     * ユーザー情報の取得 - サブクラスで必ず実装
     */
    protected abstract Object fetchUserInfo(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String accessToken);

    /**
     * JWTトークンの生成 - サブクラスで必ず実装
     */
    protected abstract String generateJwtToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            Object userInfo);

    /**
     * ログインの完了 - サブクラスで必ず実装
     */
    protected abstract void completeLogin(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String jwtToken, Object userInfo);
}
