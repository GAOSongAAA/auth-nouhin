package com.collaboportal.common.oauth2.template.ext;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.entity.DTO.ContextSerializableDto;
import com.collaboportal.common.oauth2.exception.OAuth2ConfigurationException;
import com.collaboportal.common.oauth2.exception.OAuth2TokenException;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.template.OAuth2LoginTemplate;
import com.collaboportal.common.oauth2.utils.CookieUtil;
import com.collaboportal.common.oauth2.utils.JwtValidationUtils;
import com.collaboportal.common.utils.Message;


public class JwtValidationTemplate extends OAuth2LoginTemplate {

    private final Logger logger = LoggerFactory.getLogger(JwtValidationTemplate.class);

    private final JwtTokenUtil jwtTokenUtil;

    protected final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    private final JwtTokenStrategyRegistry jwtTokenStrategyRegistry;

    private final String localAuthPage = "/testEnv";

    public JwtValidationTemplate(JwtTokenUtil jwtTokenUtil, OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.clientRegistrationFactory = clientRegistrationFactory;
        this.jwtTokenStrategyRegistry = new JwtTokenStrategyRegistry();
        registerDefaultStrategies();
        logger.debug("JwtValidationPreHandlerの初期化が完了しました。JwtTokenUtilを注入しました");
    }

    private void registerDefaultStrategies() {
        jwtTokenStrategyRegistry.register("header", JwtValidationUtils::extractTokenFromHeader);
        jwtTokenStrategyRegistry.register("cookie", JwtValidationUtils::extractTokenFromCookie);
        logger.debug("デフォルトのトークン抽出ストラテジーを登録しました: header, cookie");
    }

    public JwtValidationChain buildValidationChain() {
        JwtValidationChain chain = new JwtValidationChain();
        chain.addHandler(this::providerIdHandler);
        chain.addHandler(this::oauthContextHandler);
        chain.addHandler(this::stateResolveHandler);
        // ===============================================
        chain.addHandler(this::cookieCheckHandler);
        chain.addHandler(this::tokenValidationHandler);

        return chain;
    }

    /**
     * 
     * @param context
     * @return
     *         解决请求Header与提供商的映射关系，并设置到context中
     */
    private boolean providerIdHandler(OAuth2ProviderContext context) {
        String providerId = clientRegistrationFactory.getProviderId();
        if (!providerId.isEmpty()) {
            context.setSelectedProviderId(providerId);
            return true;
        }
        logger.warn("Authorization-Providerヘッダーが見つかりませんでした。リクエストヘッダー: {}");
        return false;
    }

    private boolean oauthContextHandler(OAuth2ProviderContext context) {
        try {
            OAuth2ClientRegistration clientRegistration = getClientRegistration(context);
            if (clientRegistration == null) {
                String errorMessage = String.format("OAuth2 クライアント設定が見つかりません: %s", context.getSelectedProviderId());
                logger.warn("clientRegistrationが見つかりませんでした: {}", context.getSelectedProviderId());
                throw new OAuth2ConfigurationException(errorMessage, context.getSelectedProviderId());
            }
            context.setIssuer(clientRegistration.getIssuer());
            context.setClientId(clientRegistration.getClientId());
            context.setAudience(clientRegistration.getAudience());
            return true;
        } catch (OAuth2ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("OAuth2 設定処理中にエラーが発生しました: %s", e.getMessage());
            logger.error("OAuth2 設定処理中に例外が発生しました", e);
            throw new OAuth2ConfigurationException(errorMessage, e);
        }
    }

    private boolean stateResolveHandler(OAuth2ProviderContext context) {
        BaseRequest req = context.getRequest();
        BaseResponse resp = context.getResponse();
        String cookieValue = req.getCookieValue(Message.Cookie.AUTH_STATE);

        ContextSerializableDto contextSerializableDto = new ContextSerializableDto();
        contextSerializableDto.setIssuer(context.getIssuer());
        contextSerializableDto.setClientId(context.getClientId());
        contextSerializableDto.setAudience(context.getAudience());
        if (cookieValue == null || cookieValue.isEmpty()) {
            logger.debug("StateパラメータがCookieに存在しません。新しいStateを生成します");
            storeStateInformation(contextSerializableDto, resp);
            return true;
        }

        return true;
    }

    private boolean cookieCheckHandler(OAuth2ProviderContext context) {
        BaseRequest req = context.getRequest();
        if (context.getIssuer().isEmpty() || context.getClientId().isEmpty() || context.getAudience().isEmpty()) {
            logger.warn("issuer, clientId, audienceが設定されていません");
            return false;
        }
        if (req.getCookieValue(Message.Cookie.AUTH) == null || req.getCookieValue(Message.Cookie.AUTH).isEmpty()) {
            logger.debug("Cookieが検出されませんでした");

            if (!JwtValidationUtils.isUseCookieAuthorization(req)) {
                logger.warn("現在のパスではCookieモードが許可されていません。認証を拒否します");
                context.setAuthProviderUrl(getRedirectUrlByEnv(context));
                return false;
            }

            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }

        logger.debug("Cookieが検出されました");
        return true;
    }

    private boolean tokenValidationHandler(OAuth2ProviderContext context) {
        BaseRequest req = context.getRequest();
        String strategyKey = JwtValidationUtils.decideStrategyByPath(req);
        context.setStrategyKey(strategyKey);
        String token = jwtTokenStrategyRegistry.resolveToken(req, strategyKey);
        context.setToken(token);

        logger.debug("ストラテジー [{}] を使用してトークンを取得しました: {}", strategyKey, token != null ? "[存在]" : "null");

        if (token == null || token.isBlank()) {
            logger.debug("トークンが存在しません。認証に失敗しました");
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }

        try {
            if (jwtTokenUtil.isTokenExpired(token)) {
                String errorMessage = "JWT 令牌已過期";
                logger.debug("トークンの有効期限が切れています");
                throw new OAuth2TokenException(errorMessage, token);
            }

            String updatedToken = jwtTokenUtil.updateExpiresAuthToken(token);
            logger.debug("トークンの検証に成功しました。新しいトークン: {}", updatedToken);

            JwtValidationUtils.setCookie(context.getResponse(), Message.Cookie.AUTH, updatedToken);
            return true;

        } catch (OAuth2TokenException e) {
            logger.warn("OAuth2 令牌異常: {}", e.getMessage());
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("JWT 令牌驗證過程中發生未預期錯誤: %s", e.getMessage());
            logger.error("トークン検証中に例外が発生しました: {}", e.getMessage(), e);
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            throw new OAuth2TokenException(errorMessage, e);
        }
    }

    // ===============================================
    protected OAuth2ClientRegistration getClientRegistration(OAuth2ProviderContext context) {

        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    protected void storeStateInformation(ContextSerializableDto contextSerializableDto, BaseResponse response) {
        String stateParameter = jwtTokenUtil.generateTokenFromObject(contextSerializableDto);
        CookieUtil.setNoneSameSiteCookie(response, Message.Cookie.AUTH_STATE, stateParameter);

    }

    // ===============================================
    private String getRedirectUrlByEnv(OAuth2ProviderContext context) {
        return "0".equals(ConfigManager.getConfig().getEnvFlag())
                ? localAuthPage
                : JwtValidationUtils.buildAuthRedirectUrl(context);
    }
}
