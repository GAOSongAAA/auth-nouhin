package com.collaboportal.common.spring.jwt;

import java.util.EnumSet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.servlet.LogbookFilter;

import com.collaboportal.common.filter.LogTraceIdFilter;
import com.collaboportal.common.filter.MaintenanceModeFilter;
import com.collaboportal.common.jwt.logic.JwtSignatureValidationFilter;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;

import jakarta.servlet.DispatcherType;

/**
 * フィルター自動設定クラス
 */
@AutoConfiguration
public class FilterAutoConfiguration {

    /**
     * ログトレースIDフィルターを登録する
     * 
     * @return フィルターレジストレーションビーン
     */
    @Bean
    @ConditionalOnClass(LogTraceIdFilter.class)
    public FilterRegistrationBean<LogTraceIdFilter> logTraceIdFilter() {
        FilterRegistrationBean<LogTraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogTraceIdFilter());
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }

    /**
     * カスタムログブックフィルターを登録する
     * 
     * @param logbook Logbookインスタンス
     * @return フィルターレジストレーションビーン
     */
    @Bean
    @Order(2)
    @ConditionalOnClass(LogbookFilter.class)
    public FilterRegistrationBean<LogbookFilter> customLogbookFilter(Logbook logbook) {
        FilterRegistrationBean<LogbookFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogbookFilter(logbook));
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * メンテナンスモードフィルターを登録する
     * 
     * @return フィルターレジストレーションビーン
     */
    @Bean
    @Order(3)
    @ConditionalOnClass(MaintenanceModeFilter.class)
    public FilterRegistrationBean<MaintenanceModeFilter> maintenanceModeFilter() {
        FilterRegistrationBean<MaintenanceModeFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new MaintenanceModeFilter());
        bean.addUrlPatterns("/mr/*", "/mr", "/", "/auth/callback", "/index.html");
        return bean;
    }

    /**
     * JWT署名検証フィルターを登録する
     * 
     * @param jwtTokenUtil JWTトークンUtil
     * @return フィルターレジストレーションビーン
     */
    @Bean
    @Order(4)
    @ConditionalOnClass(JwtSignatureValidationFilter.class)
    public FilterRegistrationBean<JwtSignatureValidationFilter> jwtSignatureValidationFilter(
            JwtTokenUtil jwtTokenUtil) {
        FilterRegistrationBean<JwtSignatureValidationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtSignatureValidationFilter(jwtTokenUtil));
        registration.addUrlPatterns("/mr/*", "/mr", "/", "/index.html");
        return registration;
    }
}
