package com.collaboportal.common.filter;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.RedirectException;
import com.collaboportal.common.spring.registry.AuthenticationStrategyRegistry;
import com.collaboportal.common.strategy.auth.AuthenticationStrategy;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthServletFilter implements Filter {

    private final AuthenticationStrategyRegistry strategyRegistry;

    public AuthServletFilter(AuthenticationStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            BaseRequest baseRequest = CommonHolder.getRequest();
            BaseResponse baseResponse = CommonHolder.getResponse();

            String authType = baseRequest.getHeader("Authorization-Type");

            if (authType != null && !authType.trim().isEmpty()) {
                AuthenticationStrategy strategy = strategyRegistry.getStrategy(authType);
                if (strategy == null) {
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported authorization type: " + authType);
                    return;
                }
                strategy.authenticate(baseRequest, baseResponse);
            }
            
            chain.doFilter(request, response);

        } catch (RedirectException e) {
            ((HttpServletResponse) response).sendRedirect(e.getRedirectUrl());
        } catch (AuthenticationException e) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } 
    }
}