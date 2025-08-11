package com.collaboportal.common.interceptor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.collaboportal.common.Router.CommonRouter;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.interceptor.AuthInterceptor;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.utils.Message;

@AutoConfiguration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class InterceptorConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);

    private final JwtService jwtService;

    public InterceptorConfig(JwtService jwtService) {
        logger.info("認証インターセプタの登録が成功しました");
        this.jwtService = jwtService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(
            handler -> {
                CommonRouter.match("/api/v1/normality-check").check(
                    r ->{
                        logger.info("認証開始");
                        if(CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH) != null && !CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH).isEmpty()){
                            boolean result = jwtService.validateToken(
                                CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH), 
                                JwtConstants.VALIDATE_TYPE_DATABASE_DATABASE
                            );
                            logger.info("認證結束");
                            logger.debug("認證結果: {}", result);
                            if (!result) {
                                throw new StopMatchException("認證失敗");
                            }
                        } else {
                            logger.info("トークンが存在しません");
                        }
                    }
                );
            }
        )).addPathPatterns("/**");
    }

}
