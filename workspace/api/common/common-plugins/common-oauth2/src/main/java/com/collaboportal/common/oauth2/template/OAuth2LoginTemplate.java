// ========== テンプレートメソッド抽象基底クラス - OAuth2ログインテンプレート ==========
package com.collaboportal.common.oauth2.template;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseCookie;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


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
    public OAuth2LoginTemplate() {
        this.clientRegistrationFactory = null;
    }

    /**
     * mainロジック処理 - 非抽象方法，提供默認實現
     */
    public void executeLogin(OAuth2ProviderContext context) {
        // 提供默認實現，子類可以重寫
        logger.debug("執行默認登錄邏輯");
    }

    /**
     * コールバックロジック処理 - 非抽象方法，提供默認實現
     */
    public void executeCallback(OAuth2ProviderContext context, String code, String state) {
        // 提供默認實現，子類可以重寫
        logger.debug("執行默認回調邏輯");
    }

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
        if (clientRegistrationFactory == null) {
            return null;
        }
        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    /**
     * リダイレクトの実行
     */
    protected void performRedirect(OAuth2ProviderContext context, String authorizationUrl) throws IOException {
        BaseResponse response = context.getResponse();
        response.redirect(authorizationUrl);
    }

    /**
     * ログインエラーの処理 - サブクラスでオーバーライド可能
     */
    protected void handleLoginError(OAuth2ProviderContext context, String errorMessage) {
        try {
            BaseResponse response = context.getResponse();
            response.addCookie(new BaseCookie("MoveURL", "/#/error").setPath("/").setMaxAge(ConfigManager.getConfig().getCookieExpiration()).setSameSite("None").setSecure(ConfigManager.getConfig().isCookieSecure()));
        } catch (Exception e) {
            logger.error("ログインエラーの処理中に例外が発生しました", e);
        }
    }

   

    /**
     * コールバックエラーの処理 - サブクラスでオーバーライド可能
     */
    protected void handleCallbackError(OAuth2ProviderContext context, String errorMessage) {
        try {
            BaseResponse response = context.getResponse();
            response.addCookie(new BaseCookie("MoveURL", "/#/error").setPath("/").setMaxAge(ConfigManager.getConfig().getCookieExpiration()).setSameSite("None").setSecure(ConfigManager.getConfig().isCookieSecure()));
        } catch (Exception e) {
            logger.error("コールバックエラーの処理中に例外が発生しました", e);
        }
    }

    // ========== 抽象メソッド改為具象メソッド，提供默認實現 ==========

    /**
     * 状態情報の保存 - 提供默認實現，子類可以重寫
     */
    protected void storeStateInformation(OAuth2ProviderContext context, OAuth2ClientRegistration registration) {
        // 提供默認實現
        logger.debug("存儲狀態信息");
    }

    /**
     * 状態情報の保存（重載方法） - 為了支援JwtValidationTemplate
     */
    protected void storeStateInformation(OAuth2ProviderContext context) {
        // 提供默認實現
        logger.debug("存儲狀態信息（無registration參數）");
    }

    /**
     * 認可コードとアクセストークンの交換 - 提供默認實現，子類可以重寫
     */
    protected String exchangeCodeForToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String code) {
        // 提供默認實現
        logger.debug("交換授權碼為訪問令牌");
        return null;
    }

    /**
     * ユーザー情報の取得 - 提供默認實現，子類可以重寫
     */
    protected Object fetchUserInfo(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String accessToken) {
        // 提供默認實現
        logger.debug("獲取用戶信息");
        return null;
    }

    /**
     * JWTトークンの生成 - 提供默認實現，子類可以重寫
     */
    protected String generateJwtToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            Object userInfo) {
        // 提供默認實現
        logger.debug("生成JWT令牌");
        return null;
    }

    /**
     * ログインの完了 - 提供默認實現，子類可以重寫
     */
    protected void completeLogin(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String jwtToken, Object userInfo) {
        // 提供默認實現
        logger.debug("完成登錄");
    }
}
