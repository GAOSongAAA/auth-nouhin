
package com.collaboportal.common.filter;

import jakarta.servlet.*;

import org.springframework.core.annotation.Order;
import org.springframework.security.access.AuthorizationServiceException;

import com.collaboportal.common.Router.CommonRouter;
import com.collaboportal.common.context.CommonHolder;

import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.strategy.authorization.AuthorizationErrorStrategy;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Order(1)
public class AuthorizationServletFilter implements AuthFilter, Filter {

    // ------------------------ 设置此过滤器 拦截 & 放行 的路由

    /**
     * 拦截路由
     */
    public List<String> includeList = new ArrayList<>();

    /**
     * 放行路由
     */
    public List<String> excludeList = new ArrayList<>();

    @Override
    public AuthorizationServletFilter addInclude(String... paths) {
        includeList.addAll(Arrays.asList(paths));
        return this;
    }

    @Override
    public AuthorizationServletFilter addExclude(String... paths) {
        excludeList.addAll(Arrays.asList(paths));
        return this;
    }

    @Override
    public AuthorizationServletFilter setIncludeList(List<String> pathList) {
        includeList = pathList;
        return this;
    }

    @Override
    public AuthorizationServletFilter setExcludeList(List<String> pathList) {
        excludeList = pathList;
        return this;
    }

    public AuthorizationStrategy auth = (req, resp) -> {
    };

    public AuthorizationErrorStrategy error = e -> {
        throw new AuthorizationServiceException("システムエラー");

    };

    /**
     * 前置函数：在每次[认证函数]之前执行
     * <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
     */
    public AuthorizationStrategy beforeAuth = (req, resp) -> {
    };

    @Override
    public AuthorizationServletFilter setAuth(AuthorizationStrategy auth) {
        this.auth = auth;
        return this;
    }

    @Override
    public AuthorizationServletFilter setError(AuthorizationErrorStrategy error) {
        this.error = error;
        return this;
    }

    @Override
    public AuthorizationServletFilter setBeforeAuth(AuthorizationStrategy beforeAuth) {
        this.beforeAuth = beforeAuth;
        return this;
    }

    // ------------------------ doFilter

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // 执行全局过滤器
            beforeAuth.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
            CommonRouter.match(includeList).notMatch(excludeList).check(r -> {
                auth.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
            });

        } catch (StopMatchException e) {
            // StopMatchException 异常代表：停止匹配，进入Controller

        } catch (Throwable e) {
            // 1. 获取异常处理策略结果
            String result = (e instanceof BackResultException) ? e.getMessage() : String.valueOf(error.run(e));

            if (response.getContentType() == null) {
                response.setContentType("application/json;charset=UTF-8");
            }
            response.getWriter().print(result);
            return;
        }

        // 执行
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
