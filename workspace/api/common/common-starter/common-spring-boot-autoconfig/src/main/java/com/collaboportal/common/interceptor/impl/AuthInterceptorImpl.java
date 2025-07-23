package com.collaboportal.common.interceptor.impl;

import jakarta.annotation.PostConstruct;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.collaboportal.common.Router.CommonRouter;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.model.BaseRequest;
import com.collaboportal.common.context.model.BaseResponse;
import com.collaboportal.common.interceptor.AuthInterceptor;


public class AuthInterceptorImpl implements WebMvcConfigurer {

    BaseRequest baseRequest = CommonHolder.getRequest();

    BaseResponse baseResponse = CommonHolder.getResponse();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(handle -> {
            CommonRouter.match("/api/v1/normality-check").check(r -> {

            });
        }));
    }



}
