package com.collaboportal.common.filter;

import com.collaboportal.common.Router.CommonRouter;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.registry.AuthenticationStrategyRegistry;
import com.collaboportal.common.strategy.authorization.AuthorizationErrorStrategy;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest; // 确保导入 HttpServletRequest
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
    private AuthorizationStrategy authStrategy; // 优先的钩子函数
    private AuthorizationStrategy beforeAuthStrategy;
    private AuthorizationErrorStrategy errorStrategy;

    public AuthServletFilter(AuthenticationStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    // --- 链式配置方法的实现 (保持不变) ---
    @Override public AuthFilter addInclude(String... patterns) { this.includeList.addAll(List.of(patterns)); return this; }
    @Override public AuthFilter addExclude(String... patterns) { this.excludeList.addAll(List.of(patterns)); return this; }
    @Override public AuthFilter setIncludeList(List<String> pathList) { this.includeList = pathList; return this; }
    @Override public AuthFilter setExcludeList(List<String> pathList) { this.excludeList = pathList; return this; }
    @Override public AuthFilter setAuth(AuthorizationStrategy auth) { this.authStrategy = auth; return this; }
    @Override public AuthFilter setBeforeAuth(AuthorizationStrategy beforeAuth) { this.beforeAuthStrategy = beforeAuth; return this; }
    @Override public AuthFilter setError(AuthorizationErrorStrategy error) { this.errorStrategy = error; return this; }


    /**
     * 【已修改】过滤器的核心方法。
     * 优先检查路径是否需要认证，如果不需要则立即放行，避免不必要的操作。
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // --- 第一步：立即进行路径检查，如果被排除，直接放行 ---
        // 注意：这里我们直接从 ServletRequest 获取路径，避免过早初始化 BaseRequest
        String path = ((HttpServletRequest) request).getRequestURI();

        if (!isRequestNeedAuth(path)) {
            logger.trace("Request path [{}] does not require authentication, passing through.", path);
            chain.doFilter(request, response);
            return; // 直接返回，不执行任何后续逻辑
        }

        // --- 第二步：只有在确定需要认证时，才初始化自订的 Request/Response 和执行逻辑 ---
        logger.debug("Request path [{}] requires authentication.", path);
        BaseRequest baseRequest = CommonHolder.getRequest();
        BaseResponse baseResponse = CommonHolder.getResponse();

        try {
            // 执行前置钩子
            if (beforeAuthStrategy != null) {
                beforeAuthStrategy.authenticate(baseRequest, baseResponse);
            }

            // 执行核心认证逻辑
            executeAuthentication(baseRequest, baseResponse);

            // 注意：认证成功后，仍需调用 chain.doFilter 以便请求能够继续到达 Controller
            chain.doFilter(request, response);

        } catch (Throwable e) {
            // 捕获所有异常，并执行错误处理钩子
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
     * (此方法保持不变)
     */
    private void executeAuthentication(BaseRequest baseRequest, BaseResponse baseResponse) throws Throwable {
        // 优先逻辑：如果 setAuth 钩子函数已配置，则执行它
        if (this.authStrategy != null) {
            logger.debug("Executing custom authentication strategy via setAuth().");
            this.authStrategy.authenticate(baseRequest, baseResponse);
            return;
        }

        // 回退逻辑：如果没有配置 setAuth，则使用基于请求头的默认逻辑
        logger.debug("No custom auth strategy set, falling back to header-based strategy registry.");
        String authType = baseRequest.getHeader("Authorization-Type");
        if (authType != null && !authType.trim().isEmpty()) {
            AuthorizationStrategy strategy = strategyRegistry.getStrategy(authType);
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
     * (此方法保持不变)
     */
    private boolean isRequestNeedAuth(String path) {
        // 检查排除列表
        for (String pattern : excludeList) {
            if (CommonRouter.isMatch(pattern, path)) {
                return false; // 匹配到排除项，则无需认证
            }
        }
        // 如果包含列表为空，则默认需要认证 (黑名单模式)
        if (includeList.isEmpty()) {
            return true;
        }
        // 如果包含列表不为空，则需要匹配到才需要认证 (白名单模式)
        for (String pattern : includeList) {
            if (CommonRouter.isMatch(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}