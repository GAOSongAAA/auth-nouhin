// 文件路径: com/collaboportal/common/oauth2/strategy/OAuth2AuthStrategy.java
// (这个文件将替换旧的 OAuth2AuthStrategy 和 JwtValidationTemplate)

package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.AuthContext;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.RedirectException;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.entity.DTO.ContextSerializableDto;
import com.collaboportal.common.oauth2.exception.OAuth2ConfigurationException;
import com.collaboportal.common.oauth2.exception.OAuth2TokenException;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.utils.CookieUtil;
import com.collaboportal.common.oauth2.utils.JwtValidationUtils;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.utils.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 最终的、正确的OAuth2认证策略实现。
 * 它完整地封装了您设计的JwtValidationTemplate的流程控制逻辑。
 */
@Component("oauth2AuthStrategy")
public class OAuth2AuthStrategy implements AuthorizationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthStrategy.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;
    private final JwtTokenStrategyRegistry jwtTokenStrategyRegistry;

    private final String localAuthPage = "/testEnv";

    /**
     * 构造函数，通过依赖注入接收所有必要的组件。
     */
    public OAuth2AuthStrategy(JwtTokenUtil jwtTokenUtil, OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.clientRegistrationFactory = clientRegistrationFactory;
        this.jwtTokenStrategyRegistry = new JwtTokenStrategyRegistry();
        registerDefaultStrategies();
        logger.debug("OAuth2AuthStrategy 初始化完成。");
    }
    
    /**
     * 认证策略的核心实现。
     * 它将构建并执行一个验证链，来处理复杂的OAuth2认证和重定向逻辑。
     */
    @Override
    public void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException, RedirectException {
        logger.debug("开始执行OAuth2认证策略...");

        // 2. 构建并执行责任链
        JwtValidationChain chain = buildValidationChain();
        boolean success = chain.execute(context);

        // 3. 根据链的执行结果进行处理
        if (success) {
            // 如果链成功执行（意味着token有效或被刷新），则认证通过
            logger.info("OAuth2认证成功，令牌有效。");
            // 将用户信息存入上下文，以便后续业务使用
            CommonHolder.getStorage().set("USER_INFO", JwtTokenUtil.getItemsJwtToken(context.getToken()));
        } else {
            // 如果链执行中断（意味着需要重定向），则抛出RedirectException
            String redirectUrl = context.getAuthProviderUrl();
            if (redirectUrl == null || redirectUrl.isBlank()) {
                logger.error("OAuth2认证失败，但未提供重定向URL。");
                throw new OAuth2ConfigurationException("OAuth2认证失败，且无法确定重定向地址。");
            }
            logger.info("OAuth2认证需要重定向，目标地址: {}", redirectUrl);
            throw new RedirectException(redirectUrl);
        }
    }

    // =================================================================================
    // 以下所有方法都是从您设计的 JwtValidationTemplate 中原封不动迁移过来的内部实现细节
    // =================================================================================

    private void registerDefaultStrategies() {
        jwtTokenStrategyRegistry.register("header", JwtValidationUtils::extractTokenFromHeader);
        jwtTokenStrategyRegistry.register("cookie", JwtValidationUtils::extractTokenFromCookie);
        logger.debug("默认的令牌提取策略已注册: header, cookie");
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
        logger.warn("未在请求头中找到 Authorization-Provider。");
        return false;
    }

    private boolean oauthContextHandler(OAuth2ProviderContext context) {
        OAuth2ClientRegistration clientRegistration = getClientRegistration(context);
        if (clientRegistration == null) {
            throw new OAuth2ConfigurationException("未找到OAuth2客户端配置: " + context.getSelectedProviderId(), context.getSelectedProviderId());
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
            logger.debug("Cookie中不存在State参数，正在生成新的State。");
            ContextSerializableDto dto = new ContextSerializableDto(context.getIssuer(), context.getClientId(), context.getAudience());
            storeStateInformation(dto, resp);
        }
        return true;
    }

    private boolean cookieCheckHandler(OAuth2ProviderContext context) {
        if (context.getRequest().getCookieValue(Message.Cookie.AUTH) == null) {
            logger.debug("未检测到认证Cookie。");
            if (!JwtValidationUtils.isUseCookieAuthorization(context.getRequest())) {
                logger.warn("当前路径不允许使用Cookie模式，认证被拒绝。");
                context.setAuthProviderUrl(getRedirectUrlByEnv(context));
                return false;
            }
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }
        logger.debug("检测到认证Cookie。");
        return true;
    }

    private boolean tokenValidationHandler(OAuth2ProviderContext context) {
        String strategyKey = JwtValidationUtils.decideStrategyByPath(context.getRequest());
        String token = jwtTokenStrategyRegistry.resolveToken(context.getRequest(), strategyKey);

        if (token == null || token.isBlank()) {
            logger.debug("令牌不存在，认证失败。");
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }

        context.setToken(token); // 将找到的令牌存入上下文

        try {
            if (jwtTokenUtil.isTokenExpired(token)) {
                logger.debug("令牌已过期。");
                throw new OAuth2TokenException("JWT令牌已过期", token);
            }
            String updatedToken = jwtTokenUtil.updateExpiresAuthToken(token);
            logger.debug("令牌验证成功，已刷新。");
            JwtValidationUtils.setCookie(context.getResponse(), Message.Cookie.AUTH, updatedToken);
            return true;
        } catch (Exception e) {
            logger.warn("令牌验证异常: {}", e.getMessage(), e);
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            throw e; // 向上抛出异常，由外部处理
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
                : JwtValidationUtils.buildAuthRedirectUrl(context); // 确保传递context
    }
}