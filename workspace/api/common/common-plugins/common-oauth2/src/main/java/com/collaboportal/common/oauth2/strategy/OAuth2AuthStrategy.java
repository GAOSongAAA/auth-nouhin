// ファイルパス: com/collaboportal/common/oauth2/strategy/OAuth2AuthStrategy.java
// (このファイルは、古い OAuth2AuthStrategy と JwtValidationTemplate を置き換えます)

package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.entity.DTO.ContextSerializableDto;
import com.collaboportal.common.oauth2.exception.OAuth2ConfigurationException;
import com.collaboportal.common.oauth2.exception.OAuth2TokenException;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.utils.JwtValidationUtils;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.utils.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 最終的かつ正しいOAuth2認証戦略の実装。
 * これは、設計されたJwtValidationTemplateのフロー制御ロジックを完全にカプセル化します。
 */
@Component("oauth2AuthStrategy")
public class OAuth2AuthStrategy implements AuthorizationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthStrategy.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;
    private final JwtTokenStrategyRegistry jwtTokenStrategyRegistry;

    private final String localAuthPage = "/testEnv";

    /**
     * コンストラクタ。依存性注入により必要なすべてのコンポーネントを受け取ります。
     */
    public OAuth2AuthStrategy(JwtTokenUtil jwtTokenUtil, OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.clientRegistrationFactory = clientRegistrationFactory;
        this.jwtTokenStrategyRegistry = new JwtTokenStrategyRegistry();
        registerDefaultStrategies();
        logger.debug("OAuth2AuthStrategy の初期化が完了しました。");
    }
    
    /**
     * 認証戦略のコア実装。
     * 複雑なOAuth2認証とリダイレクトロジックを処理するために、検証チェーンを構築し実行します。
     */
    @Override
    public void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException {
        logger.debug("OAuth2認証戦略の実行を開始します...");
        OAuth2ProviderContext context = OAuth2ProviderContext.builder().request(request).response(response).build();
        // 2. 責任チェーンを構築し実行します
        JwtValidationChain chain = buildValidationChain();
        boolean success = chain.execute(context);

        // 3. チェーンの実行結果に基づいて処理を行います
        if (success) {
            // チェーンが正常に実行された場合（トークンが有効または更新されたことを意味する）、認証は成功です
            logger.info("OAuth2認証が成功し、トークンは有効です。");
            // ユーザー情報をコンテキストに保存し、後続のビジネスロジックで使用できるようにします
            CommonHolder.getStorage().set("USER_INFO", JwtTokenUtil.getItemsJwtToken(context.getToken()));
        } else {
            // チェーンの実行が中断された場合（リダイレクトが必要であることを意味する）、RedirectExceptionをスローします
            String redirectUrl = context.getAuthProviderUrl();
            if (redirectUrl == null || redirectUrl.isBlank()) {
                logger.error("OAuth2認証は失敗しましたが、リダイレクトURLが提供されていません。");
                throw new OAuth2ConfigurationException("OAuth2認証は失敗し、リダイレクトアドレスを特定できません。");
            }
            logger.info("OAuth2認証にはリダイレクトが必要です。ターゲットアドレス: {}", redirectUrl);
            response.redirect(redirectUrl);
        }
    }

    // =================================================================================
    // 
    // =================================================================================

    private void registerDefaultStrategies() {
        jwtTokenStrategyRegistry.register("header", JwtValidationUtils::extractTokenFromHeader);
        jwtTokenStrategyRegistry.register("cookie", JwtValidationUtils::extractTokenFromCookie);
        logger.debug("デフォルトのトークン抽出戦略が登録されました: header, cookie");
    }

    private JwtValidationChain buildValidationChain() {
        JwtValidationChain chain = new JwtValidationChain();
        chain.addHandler(this::providerIdHandler);
        chain.addHandler(this::oauthContextHandler);
        chain.addHandler(this::stateResolveHandler);
        chain.addHandler(this::cookieCheckHandler);
        chain.addHandler(this::tokenValidationHandler);
        return chain;
    }

    private boolean providerIdHandler(OAuth2ProviderContext context) {
        String providerId = clientRegistrationFactory.getProviderId();
        if (providerId != null && !providerId.isEmpty()) {
            context.setSelectedProviderId(providerId);
            return true;
        }
        logger.warn("リクエストヘッダーに Authorization-Provider が見つかりませんでした。");
        return false;
    }

    private boolean oauthContextHandler(OAuth2ProviderContext context) {
        OAuth2ClientRegistration clientRegistration = getClientRegistration(context);
        if (clientRegistration == null) {
            throw new OAuth2ConfigurationException("OAuth2クライアント設定が見つかりません: " + context.getSelectedProviderId(), context.getSelectedProviderId());
        }
        context.setIssuer(clientRegistration.getIssuer());
        context.setClientId(clientRegistration.getClientId());
        context.setAudience(clientRegistration.getAudience());
        return true;
    }

    private boolean stateResolveHandler(OAuth2ProviderContext context) {
        BaseRequest req = context.getRequest();
        BaseResponse resp = context.getResponse();
        String cookieValue = req.getCookieValue(Message.Cookie.AUTH_STATE);

        if (cookieValue == null || cookieValue.isEmpty()) {
            logger.debug("CookieにStateパラメータが存在しません。新しいStateを生成しています。");
            ContextSerializableDto dto = new ContextSerializableDto(context.getIssuer(), context.getClientId(), context.getAudience());
            storeStateInformation(dto, resp);
        }
        return true;
    }

    private boolean cookieCheckHandler(OAuth2ProviderContext context) {
        if (context.getRequest().getCookieValue(Message.Cookie.AUTH) == null) {
            logger.debug("認証Cookieが検出されませんでした。");
            if (!JwtValidationUtils.isUseCookieAuthorization(context.getRequest())) {
                logger.warn("現在のパスではCookieモードの使用が許可されていません。認証は拒否されました。");
                context.setAuthProviderUrl(getRedirectUrlByEnv(context));
                return false;
            }
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }
        logger.debug("認証Cookieが検出されました。");
        return true;
    }

    private boolean tokenValidationHandler(OAuth2ProviderContext context) {
        String strategyKey = JwtValidationUtils.decideStrategyByPath(context.getRequest());
        String token = jwtTokenStrategyRegistry.resolveToken(context.getRequest(), strategyKey);

        if (token == null || token.isBlank()) {
            logger.debug("トークンが存在しないため、認証に失敗しました。");
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }

        context.setToken(token); // 見つかったトークンをコンテキストに保存します

        try {
            if (jwtTokenUtil.isTokenExpired(token)) {
                logger.debug("トークンは期限切れです。");
                throw new OAuth2TokenException("JWTトークンは期限切れです", token);
            }
            String updatedToken = jwtTokenUtil.updateExpiresAuthToken(token);
            logger.debug("トークンの検証に成功し、更新されました。");
            JwtValidationUtils.setCookie(context.getResponse(), Message.Cookie.AUTH, updatedToken);
            return true;
        } catch (Exception e) {
            logger.warn("トークン検証エラー: {}", e.getMessage(), e);
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            throw e; // 例外を上位にスローし、外部で処理させます
        }
    }

    private OAuth2ClientRegistration getClientRegistration(OAuth2ProviderContext context) {
        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    private void storeStateInformation(ContextSerializableDto contextSerializableDto, BaseResponse response) {
        String stateParameter = jwtTokenUtil.generateTokenFromObject(contextSerializableDto);
        CookieUtil.setNoneSameSiteCookie(response, Message.Cookie.AUTH_STATE, stateParameter);
    }

    private String getRedirectUrlByEnv(OAuth2ProviderContext context) {
        return "0".equals(ConfigManager.getConfig().getEnvFlag())
                ? localAuthPage
                : JwtValidationUtils.buildAuthRedirectUrl(context);
    }
}