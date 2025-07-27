// 文件路径: com/collaboportal/common/filter/AuthServletFilter.java
package com.collaboportal.common.filter;

import com.collaboportal.common.Router.CommonRouter;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.registry.AuthenticationStrategyRegistry;
import com.collaboportal.common.strategy.authorization.AuthorizationErrorStrategy;
import com.collaboportal.common.strategy.common.AuthStrategy;
import com.collaboportal.common.strategy.authorization.AuthenticationStrategy;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class AuthServletFilter implements AuthFilter, Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthServletFilter.class);

    // --- 策略和配置存储 ---
    private final AuthenticationStrategyRegistry strategyRegistry; // 保留默认逻辑
    private List<String> includeList = new ArrayList<>();
    private List<String> excludeList = new ArrayList<>();
    private AuthStrategy authStrategy; // 优先的钩子函数
    private AuthStrategy beforeAuthStrategy;
    private AuthorizationErrorStrategy errorStrategy;

    public AuthServletFilter(AuthenticationStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    // --- 链式配置方法的实现 (与之前相同) ---
    @Override public AuthFilter addInclude(String... patterns) { this.includeList.addAll(List.of(patterns)); return this; }
    @Override public AuthFilter addExclude(String... patterns) { this.excludeList.addAll(List.of(patterns)); return this; }
    @Override public AuthFilter setIncludeList(List<String> pathList) { this.includeList = pathList; return this; }
    @Override public AuthFilter setExcludeList(List<String> pathList) { this.excludeList = pathList; return this; }
    @Override public AuthFilter setAuth(AuthStrategy auth) { this.authStrategy = auth; return this; }
    @Override public AuthFilter setBeforeAuth(AuthStrategy beforeAuth) { this.beforeAuthStrategy = beforeAuth; return this; }
    @Override public AuthFilter setError(AuthorizationErrorStrategy error) { this.errorStrategy = error; return this; }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        BaseRequest baseRequest = CommonHolder.getRequest();
        BaseResponse baseResponse = CommonHolder.getResponse();

        try {
            // 步骤 1: 执行前置钩子 (不受任何限制)
            if (beforeAuthStrategy != null) {
                beforeAuthStrategy.run(baseRequest, baseResponse);
            }

            // 步骤 2: 进行路径匹配，判断是否需要执行认证
            if (isRequestNeedAuth(baseRequest.getRequestPath())) {
                logger.debug("Request path [{}] requires authentication.", baseRequest.getRequestPath());
                // 步骤 3: 执行核心认证逻辑
                executeAuthentication(baseRequest, baseResponse);
            } else {
                logger.trace("Request path [{}] does not require authentication, skipping.", baseRequest.getRequestPath());
            }

            chain.doFilter(request, response);

        } catch (Throwable e) {
            // 步骤 4: 捕获所有异常，并执行错误处理钩子
            if (errorStrategy != null) {
                errorStrategy.run(e, baseRequest, baseResponse);
            } else {
                // 如果没有配置错误处理器，则将异常重新抛出
                throw new ServletException(e);
            }
        }
    }

    /**
     * 执行认证的核心方法。
     * 优先使用通过 setAuth() 设置的钩子函数，如果未设置，则回退到基于请求头的策略注册表逻辑。
     */
    private void executeAuthentication(BaseRequest baseRequest, BaseResponse baseResponse) throws Throwable {
        // 优先逻辑：如果 setAuth 钩子函数已配置，则执行它
        if (this.authStrategy != null) {
            logger.debug("Executing custom authentication strategy via setAuth().");
            this.authStrategy.run(baseRequest, baseResponse);
            return;
        }

        // 回退逻辑：如果没有配置 setAuth，则使用基于请求头的默认逻辑
        logger.debug("No custom auth strategy set, falling back to header-based strategy registry.");
        String authType = baseRequest.getHeader("Authorization-Type");
        if (authType != null && !authType.trim().isEmpty()) {
            AuthenticationStrategy strategy = strategyRegistry.getStrategy(authType);
            if (strategy == null) {
                // 直接抛出异常，由 setError 钩子或全局异常处理器处理
                throw new UnsupportedOperationException("Unsupported authorization type: " + authType);
            }
            strategy.authenticate(baseRequest, baseResponse);
        }
        // 如果连请求头都没有，则不执行任何认证，直接通过（可以根据需要修改此行为）
    }

    /**
     * 判断当前请求是否需要进行认证，使用 CommonRouter 作为路径匹配工具。
     */
    private boolean isRequestNeedAuth(String path) {
        for (String pattern : excludeList) {
            if (CommonRouter.isMatch(pattern, path)) {
                return false;
            }
        }
        if (includeList.isEmpty()) {
            return true;
        }
        for (String pattern : includeList) {
            if (CommonRouter.isMatch(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}