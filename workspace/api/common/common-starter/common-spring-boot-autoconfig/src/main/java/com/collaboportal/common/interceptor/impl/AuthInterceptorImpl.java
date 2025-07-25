package com.collaboportal.common.interceptor.impl;

import com.collaboportal.common.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptorImpl implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;

    @Value("${common.auth.includePaths:/api/*}")
    private String includePaths;

    @Value("${common.auth.excludePaths:}")
    private String excludePaths;

    @Autowired
    public AuthInterceptorImpl(AuthorizationInterceptor authorizationInterceptor) {
        this.authorizationInterceptor = authorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> includePathList = Arrays.asList(includePaths.split(","));
        List<String> excludePathList = (excludePaths != null && !excludePaths.isEmpty()) ? Arrays.asList(excludePaths.split(",")) : List.of();

        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns(includePathList)
                .excludePathPatterns(excludePathList);
    }
}

