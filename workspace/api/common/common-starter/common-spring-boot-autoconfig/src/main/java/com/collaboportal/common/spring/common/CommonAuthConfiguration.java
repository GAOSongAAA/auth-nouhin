// ファイルパス: com/collaboportal/common/spring/common/CommonAuthConfiguration.java
package com.collaboportal.common.spring.common;

import com.collaboportal.common.context.CommonHolder;

import com.collaboportal.common.filter.AuthorizationServletFilter;
import com.collaboportal.common.registry.AuthorizationStrategyRegistry;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonAuthConfiguration implements WebMvcConfigurer {

    Logger logger = LoggerFactory.getLogger(CommonAuthConfiguration.class);

    @Bean
    public AuthorizationServletFilter getAuthorizationServletFilter(
            AuthorizationStrategyRegistry strategyRegistry) {
        return new AuthorizationServletFilter()
                .addInclude("/**")
                .addExclude("/static/**",
                        "/favicon.ico",
                        "/.well-known/**",
                        "/manifest.webmanifest",
                        "/login.html", "/login", "/error", "/static/**", "/favicon.ico",
                        "/testEnv",
                        "/testEnv.html")
                .setAuth((res, resp) -> {
                    try {
                        String authorizationType = CommonHolder.getRequest().getHeader("Authorization-Type");
                        AuthorizationStrategy strategy = strategyRegistry.getStrategy(authorizationType);
                        if (strategy == null) {
                            throw new UnsupportedOperationException("不支持的授权类型: " + authorizationType);
                        }
                        strategy.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
                    } catch (Exception e) {
                        // ❶ 关键点：打出完整堆栈
                        logger.error("认证异常: {}", e.getMessage(), e);
                    }
                });

    }

    @Bean
    public FilterRegistrationBean<AuthorizationServletFilter> authorizationFilterRegistration(
            AuthorizationServletFilter authorizationServletFilter) {
        FilterRegistrationBean<AuthorizationServletFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationServletFilter);

        // 设置拦截路径
        registrationBean.addUrlPatterns("/*");

        // 设置优先级（值越小越靠前）
        registrationBean.setOrder(-101);

        // 设置是否启用（可用于灰度等）
        registrationBean.setEnabled(true);

        return registrationBean;

    }
}