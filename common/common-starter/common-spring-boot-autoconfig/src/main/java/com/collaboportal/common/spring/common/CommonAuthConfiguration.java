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
                        "/auth/callback",
                        "/manifest.webmanifest",
                        "/login.html", "/auth/login", "/error", "/static/**", "/favicon.ico",
                        "/testEnv",
                        "/testEnv.html")
                .setAuth((res, resp) -> {
                    try {
                        String authorizationType = CommonHolder.getRequest().getHeader("Authorization-Type");
                        AuthorizationStrategy strategy = strategyRegistry.getStrategy(authorizationType);
                        if (strategy == null) {
                            throw new UnsupportedOperationException("サポートされていない認証タイプ: " + authorizationType);
                        }
                        strategy.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
                    } catch (Exception e) {
                        // ❶ 重要ポイント：完全なスタックトレースを出力
                        logger.error("認証例外: {}", e.getMessage(), e);
                    }
                });

    }

    @Bean
    public FilterRegistrationBean<AuthorizationServletFilter> authorizationFilterRegistration(
            AuthorizationServletFilter authorizationServletFilter) {
        FilterRegistrationBean<AuthorizationServletFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationServletFilter);

        // インターセプトパスを設定
        registrationBean.addUrlPatterns("/*");

        // 優先度を設定（値が小さいほど前に実行される）
        registrationBean.setOrder(-101);

        // 有効/無効を設定（グレーリリースなどに使用可能）
        registrationBean.setEnabled(true);

        return registrationBean;

    }
}