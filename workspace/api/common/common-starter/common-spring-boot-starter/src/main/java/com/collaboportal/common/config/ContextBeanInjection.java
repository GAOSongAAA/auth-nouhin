package com.collaboportal.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.collaboportal.common.filter.CommonContextBindingFilter;

@Configuration
public class ContextBeanInjection {

    @Bean
    public FilterRegistrationBean<CommonContextBindingFilter> ctxBindingFilter() {
        FilterRegistrationBean<CommonContextBindingFilter> bean = new FilterRegistrationBean<>(
                new CommonContextBindingFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 120);
        bean.addUrlPatterns("/*");
        return bean;
    }

}
