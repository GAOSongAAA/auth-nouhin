package com.collaboportal.common.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.spring.ContextForSpringInJakartaServlet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE + 120)
public class CommonContextBindingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // ① 绑定
        ConfigManager.setCommonContext(new ContextForSpringInJakartaServlet());
        try {
            filterChain.doFilter(request, response);
        } finally {
            ConfigManager.clearContext();
        }
    }
}