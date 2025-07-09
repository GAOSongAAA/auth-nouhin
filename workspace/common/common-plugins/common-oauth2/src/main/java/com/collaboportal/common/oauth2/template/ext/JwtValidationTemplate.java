package com.collaboportal.common.oauth2.template.ext;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.template.OAuth2LoginTemplate;
import com.collaboportal.common.oauth2.utils.CookieUtil;
import com.collaboportal.common.oauth2.utils.JwtValidationUtils;
import com.collaboportal.common.utils.Message;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationTemplate extends OAuth2LoginTemplate {

    private final Logger logger = LoggerFactory.getLogger(JwtValidationTemplate.class);

    private final JwtTokenUtil jwtTokenUtil;

    protected final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    private final JwtTokenStrategyRegistry jwtTokenStrategyRegistry;

    private final String localAuthPage = "testEnv";

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
     *         解决请求路径与提供商的映射关系，并设置到context中
     */
    private boolean providerIdHandler(OAuth2ProviderContext context) {
        String path = context.getRequest().getServletPath();
        String providerId = clientRegistrationFactory.findProviderByPath(path);
        if (!providerId.isEmpty()) {
            context.setSelectedProviderId(providerId);
            return true;
        }
        return false;
    }

    private boolean oauthContextHandler(OAuth2ProviderContext context) {
        OAuth2ClientRegistration clientRegistration = getClientRegistration(context);
        if (clientRegistration == null) {
            logger.warn("clientRegistrationが見つかりませんでした: {}", context.getSelectedProviderId());
            return false;
        }
        context.setIssuer(clientRegistration.getIssuer());
        context.setClientId(clientRegistration.getClientId());
        context.setAudience(clientRegistration.getAudience());
        return true;
    }

    private boolean stateResolveHandler(OAuth2ProviderContext context) {
        HttpServletRequest req = context.getRequest();
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            String state = Arrays.stream(cookies)
                    .filter(c -> Message.Cookie.AUTH_STATE.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst().orElse(null);
            if (state == null || state.isEmpty()) {
                storeStateInformation(context);
                return true;
            }
        }
        return false;
    }

    private boolean cookieCheckHandler(OAuth2ProviderContext context) {
        HttpServletRequest req = context.getRequest();
        Cookie[] cookies = req.getCookies();
        if (context.getIssuer().isEmpty() || context.getClientId().isEmpty() || context.getAudience().isEmpty()) {
            logger.warn("issuer, clientId, audienceが設定されていません");
            return false;
        }
        if (cookies == null || cookies.length == 0) {
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
        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();
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
                logger.debug("トークンの有効期限が切れています");
                context.setAuthProviderUrl(getRedirectUrlByEnv(context));
                return false;
            }

            String updatedToken = jwtTokenUtil.updateExpiresAuthToken(token);
            logger.debug("トークンの検証に成功しました。新しいトークン: {}", updatedToken);

            JwtValidationUtils.setCookie(resp, Message.Cookie.AUTH, updatedToken);
            return true;

        } catch (Exception e) {
            logger.error("トークン検証中に例外が発生しました: {}", e.getMessage(), e);
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }
    }

    // ===============================================
    protected OAuth2ClientRegistration getClientRegistration(OAuth2ProviderContext context) {

        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    protected void storeStateInformation(OAuth2ProviderContext context) {
        String stateParameter = jwtTokenUtil.generateTokenFromObject(context);
        CookieUtil.setNoneSameSiteCookie(context.getResponse(), Message.Cookie.AUTH_STATE, stateParameter);

    }

    // ===============================================
    private String getRedirectUrlByEnv(OAuth2ProviderContext context) {
        return "0".equals(ConfigManager.getConfig().getEnvFlag())
                ? localAuthPage
                : JwtValidationUtils.buildAuthRedirectUrl(context);
    }
}
