// ========== 模板方法抽象基类 - OAuth2登录模板 ==========
package com.collaboportal.common.oauth2.template;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 模板方法模式 - OAuth2登录流程抽象模板
 * 职责：定义OAuth2登录的标准流程，抽象变化点供子类实现
 * 优势：统一流程控制、减少代码重复、易于维护和扩展
 */
public abstract class OAuth2LoginTemplate {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public OAuth2LoginTemplate(OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.clientRegistrationFactory = clientRegistrationFactory;
    }

    /**
     * 模板方法 - 定义OAuth2登录的完整流程
     * 这是不可变的骨架算法
     */
    public final void executeLogin(OAuth2ProviderContext context) {
        try {
            logger.info("[OAuth2登录模板] 开始执行登录流程，提供商: {}", context.getSelectedProviderId());

            // 1. 验证和准备阶段
            if (!validateLoginRequest(context)) {
                logger.error("[OAuth2登录模板] 登录请求验证失败");
                handleLoginError(context, "登录请求验证失败");
                return;
            }

            // 2. 获取客户端注册信息
            OAuth2ClientRegistration registration = getClientRegistration(context);
            if (registration == null) {
                logger.error("[OAuth2登录模板] 未找到提供商配置: {}", context.getSelectedProviderId());
                handleLoginError(context, "提供商配置不存在");
                return;
            }

            // 3. 准备授权请求参数
            String state = generateState(context);
            context.setState(state);

            // 4. 构建授权URL（子类实现）
            String authorizationUrl = buildAuthorizationUrl(context, registration);

            // 5. 存储状态信息（子类实现）
            storeStateInformation(context, registration);

            // 6. 执行重定向
            performRedirect(context, authorizationUrl);

            logger.info("[OAuth2登录模板] 登录流程执行完成，重定向到: {}", authorizationUrl);

        } catch (Exception e) {
            logger.error("[OAuth2登录模板] 登录流程执行异常", e);
            handleLoginError(context, "登录流程执行异常: " + e.getMessage());
        }
    }

    /**
     * 模板方法 - 定义OAuth2回调处理的完整流程
     */
    public final void executeCallback(OAuth2ProviderContext context, String code, String state) {
        try {
            logger.info("[OAuth2回调模板] 开始处理OAuth2回调，提供商: {}", context.getSelectedProviderId());

            // 1. 验证回调请求
            if (!validateCallbackRequest(context, code, state)) {
                logger.error("[OAuth2回调模板] 回调请求验证失败");
                handleCallbackError(context, "回调请求验证失败");
                return;
            }

            // 2. 获取客户端注册信息
            OAuth2ClientRegistration registration = getClientRegistration(context);
            if (registration == null) {
                logger.error("[OAuth2回调模板] 未找到提供商配置: {}", context.getSelectedProviderId());
                handleCallbackError(context, "提供商配置不存在");
                return;
            }

            // 3. 交换授权码获取访问令牌（子类实现）
            String accessToken = exchangeCodeForToken(context, registration, code);
            if (accessToken == null) {
                logger.error("[OAuth2回调模板] 令牌交换失败");
                handleCallbackError(context, "令牌交换失败");
                return;
            }

            // 4. 获取用户信息（子类实现）
            Object userInfo = fetchUserInfo(context, registration, accessToken);
            if (userInfo == null) {
                logger.error("[OAuth2回调模板] 用户信息获取失败");
                handleCallbackError(context, "用户信息获取失败");
                return;
            }

            // 5. 生成JWT令牌（子类实现）
            String jwtToken = generateJwtToken(context, registration, userInfo);

            // 6. 设置认证Cookie和完成登录（子类实现）
            completeLogin(context, registration, jwtToken, userInfo);

            logger.info("[OAuth2回调模板] 回调处理完成");

        } catch (Exception e) {
            logger.error("[OAuth2回调模板] 回调处理异常", e);
            handleCallbackError(context, "回调处理异常: " + e.getMessage());
        }
    }

    // ========== 具体方法 - 子类可以重写但有默认实现 ==========

    /**
     * 验证登录请求 - 子类可重写
     */
    protected boolean validateLoginRequest(OAuth2ProviderContext context) {
        return context.getSelectedProviderId() != null &&
                context.getRequest() != null &&
                context.getResponse() != null;
    }

    /**
     * 验证回调请求 - 子类可重写
     */
    protected boolean validateCallbackRequest(OAuth2ProviderContext context, String code, String state) {
        return code != null && !code.isEmpty() &&
                state != null && !state.isEmpty();
    }

    /**
     * 获取客户端注册信息
     */
    protected OAuth2ClientRegistration getClientRegistration(OAuth2ProviderContext context) {
        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    /**
     * 生成状态参数 - 子类可重写
     */
    protected String generateState(OAuth2ProviderContext context) {
        return UUID.randomUUID().toString();
    }

    /**
     * 执行重定向
     */
    protected void performRedirect(OAuth2ProviderContext context, String authorizationUrl) throws IOException {
        HttpServletResponse response = context.getResponse();
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", authorizationUrl);
    }

    /**
     * 处理登录错误 - 子类可重写
     */
    protected void handleLoginError(OAuth2ProviderContext context, String errorMessage) {
        try {
            HttpServletResponse response = context.getResponse();
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", "/error?message=" + errorMessage);
        } catch (Exception e) {
            logger.error("处理登录错误时发生异常", e);
        }
    }

    /**
     * 处理回调错误 - 子类可重写
     */
    protected void handleCallbackError(OAuth2ProviderContext context, String errorMessage) {
        try {
            HttpServletResponse response = context.getResponse();
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", "/error?message=" + errorMessage);
        } catch (Exception e) {
            logger.error("处理回调错误时发生异常", e);
        }
    }

    // ========== 抽象方法 - 子类必须实现 ==========

    /**
     * 构建授权URL - 子类必须实现
     */
    protected abstract String buildAuthorizationUrl(OAuth2ProviderContext context,
            OAuth2ClientRegistration registration);

    /**
     * 存储状态信息 - 子类必须实现
     */
    protected abstract void storeStateInformation(OAuth2ProviderContext context, OAuth2ClientRegistration registration);

    /**
     * 交换授权码获取访问令牌 - 子类必须实现
     */
    protected abstract String exchangeCodeForToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String code);

    /**
     * 获取用户信息 - 子类必须实现
     */
    protected abstract Object fetchUserInfo(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String accessToken);

    /**
     * 生成JWT令牌 - 子类必须实现
     */
    protected abstract String generateJwtToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            Object userInfo);

    /**
     * 完成登录 - 子类必须实现
     */
    protected abstract void completeLogin(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String jwtToken, Object userInfo);
}
