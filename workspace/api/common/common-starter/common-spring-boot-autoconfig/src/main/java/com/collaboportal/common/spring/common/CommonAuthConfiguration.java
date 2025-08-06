// ファイルパス: com/collaboportal/common/spring/common/CommonAuthConfiguration.java
package com.collaboportal.common.spring.common;

import com.collaboportal.common.context.CommonHolder;

import com.collaboportal.common.filter.AuthorizationServletFilter;
import com.collaboportal.common.registry.AuthorizationStrategyRegistry;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonAuthConfiguration implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<AuthorizationServletFilter> authorizationServletFilterRegistration(
            AuthorizationStrategyRegistry strategyRegistry) {

        AuthorizationServletFilter filter = new AuthorizationServletFilter()
                .addInclude("/**")
                .addExclude("/login.html", "/login", "/error", "/static/**", "/favicon.ico", "/testEnv",
                        "/testEnv.html")
                .setAuth((res, resp) -> {
                    String authorizationType = CommonHolder.getRequest().getHeader("Authorization-Type");
                    if (authorizationType != null && !authorizationType.trim().isEmpty()) {
                        AuthorizationStrategy strategy = strategyRegistry.getStrategy(authorizationType);
                        if (strategy == null) {
                            throw new UnsupportedOperationException(
                                    "Unsupported authorization type: " + authorizationType);
                        }
                        strategy.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
                    }
                });

        FilterRegistrationBean<AuthorizationServletFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

}