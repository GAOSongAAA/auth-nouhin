package com.collaboportal.common.oauth2.controller;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.oauth2.chain.OAuth2CallbackChain;
import com.collaboportal.common.oauth2.chain.handlers.AuthorizationCodeValidationHandler;
import com.collaboportal.common.oauth2.chain.handlers.ProviderConfigValidationHandler;
import com.collaboportal.common.oauth2.chain.handlers.StateValidationHandler;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.strategy.OAuth2ProviderSelectionStrategyComposer;
import com.collaboportal.common.oauth2.template.impl.DecoupledOAuth2LoginTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 增强的OAuth2认证控制器
 * 集成多个设计模式，支持多OAuth2提供商的统一认证
 */
@RestController
@RequestMapping("/auth")
public class OAuth2AuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthController.class);

    private final OAuth2ProviderSelectionStrategyComposer providerSelectionComposer;
    private final DecoupledOAuth2LoginTemplate loginTemplate;
    private final OAuth2CallbackChain callbackChain;

    public OAuth2AuthController(
            OAuth2ProviderSelectionStrategyComposer providerSelectionComposer,
            DecoupledOAuth2LoginTemplate loginTemplate,
            StateValidationHandler stateValidationHandler,
            AuthorizationCodeValidationHandler codeValidationHandler,
            ProviderConfigValidationHandler configValidationHandler) {

        this.providerSelectionComposer = providerSelectionComposer;
        this.loginTemplate = loginTemplate;

        // 构建责任链
        this.callbackChain = new OAuth2CallbackChain()
                .addHandler(stateValidationHandler)
                .addHandler(codeValidationHandler)
                .addHandler(configValidationHandler);

        logger.info("OAuth2认证控制器初始化完成");
    }

    /**
     * 发起OAuth2登录
     * 支持多种触发方式：路径匹配、参数指定、域名路由等
     */
    @GetMapping("/login")
    public void initiateLogin(
            HttpServletRequest request,
            HttpServletResponse response) {

        logger.info("[OAuth2登录] 收到登录请求，URI: {}", request.getRequestURI());

        try {
            // 构建提供商选择上下文
            OAuth2ProviderContext context = new OAuth2ProviderContext.Builder()
                    .request(request)
                    .response(response)
                    .build();

            // 选择OAuth2提供商
            String providerId = providerSelectionComposer.selectProvider(context);
            context.setSelectedProviderId(providerId);

            logger.info("[OAuth2登录] 选择的提供商: {}", providerId);

            // 执行登录流程
            loginTemplate.executeLogin(context);

        } catch (Exception e) {
            logger.error("[OAuth2登录] 登录发起失败", e);
            redirectToError(response, "登录发起失败");
        }
    }

    /**
     * 处理OAuth2回调
     * 统一的回调端点，支持所有OAuth2提供商
     */
    @GetMapping("/callback")
    public void handleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String error_description,
            HttpServletRequest request,
            HttpServletResponse response) {

        logger.info("[OAuth2回调] 收到回调请求，code存在: {}, state: {}",
                code != null, state);

        // 检查OAuth2错误
        if (error != null) {
            logger.error("[OAuth2回调] OAuth2授权失败: {} - {}", error, error_description);
            redirectToError(response, "OAuth2授权失败: " + error);
            return;
        }

        try {
            // 构建上下文
            OAuth2ProviderContext context = new OAuth2ProviderContext.Builder()
                    .request(request)
                    .response(response)
                    .build();

            // 执行回调处理链
            boolean chainResult = callbackChain.execute(context);
            if (!chainResult) {
                logger.error("[OAuth2回调] 回调处理链验证失败");
                redirectToError(response, "回调验证失败");
                return;
            }

            // 执行回调流程
            loginTemplate.executeCallback(context, code, state);

        } catch (Exception e) {
            logger.error("[OAuth2回调] 回调处理失败", e);
            redirectToError(response, "回调处理失败");
        }
    }

    /**
     * 基于请求路径的自动登录触发
     * 当访问受保护资源时自动重定向到对应的OAuth2提供商
     */
    @GetMapping("/{provider}/**")
    public void autoLogin(
            @PathVariable String provider,
            HttpServletRequest request,
            HttpServletResponse response) {

        logger.info("[自动登录] 基于路径触发登录，提供商: {}", provider);

        try {
            // 直接指定提供商进行登录
            OAuth2ProviderContext context = new OAuth2ProviderContext.Builder()
                    .request(request)
                    .response(response)
                    .selectedProviderId(provider)
                    .build();

            loginTemplate.executeLogin(context);

        } catch (Exception e) {
            logger.error("[自动登录] 自动登录失败", e);
            redirectToError(response, "自动登录失败");
        }
    }

    /**
     * 获取可用的OAuth2提供商列表
     */
    @GetMapping("/providers")
    @ResponseBody
    public Object getAvailableProviders() {
        // 返回可用的OAuth2提供商信息
        return providerSelectionComposer.getAvailableProviders();
    }

    /**
     * 错误重定向辅助方法
     */
    private void redirectToError(HttpServletResponse response, String message) {
        try {
            String errorUrl = ConfigManager.getConfig().getIndexPage() + "?error=" + message;
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", errorUrl);
        } catch (Exception e) {
            logger.error("重定向到错误页面失败", e);
        }
    }
}