
package com.collaboportal.common.filter;

import jakarta.servlet.*;

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

public class AuthorizationServletFilter implements Filter, AuthFilter {

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

    // 修改你的 AuthorizationServletFilter.error 策略
    public AuthorizationErrorStrategy error = e -> {
        return e;
    };

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
            throw new RuntimeException("Match 失败");

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

    // ------------------------ doFilter

}
