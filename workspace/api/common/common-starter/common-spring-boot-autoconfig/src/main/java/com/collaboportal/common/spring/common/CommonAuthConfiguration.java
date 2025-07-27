// 文件路径: com/collaboportal/common/spring/common/CommonAuthConfiguration.java
package com.collaboportal.common.spring.common;

import com.collaboportal.common.filter.AuthFilter;
import com.collaboportal.common.filter.AuthServletFilter;
import com.collaboportal.common.registry.AuthenticationStrategyRegistry;

import jakarta.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonAuthConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CommonAuthConfiguration.class);

    /**
     * 将 AuthFilter 自身定义为一个 Bean。
     * 这样它就可以被其他 @Configuration 类注入并进行配置 (例如调用 setAuth)。
     * @ConditionalOnMissingBean 允许用户在自己的项目中定义同名的 Bean 来覆盖默认行为。
     * @param strategyRegistry 自动注入策略注册表
     * @return AuthFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean(AuthFilter.class)
    public AuthFilter authFilter(AuthenticationStrategyRegistry strategyRegistry) {
        logger.info("Creating default AuthServletFilter bean. This can be overridden by the user.");
        // 在这里可以进行一些默认配置
        return new AuthServletFilter(strategyRegistry)
                .addExclude("/login", "/error", "/static/**", "/favicon.ico");
    }

    /**
     * 负责将 AuthFilter Bean 注册到 Servlet 容器的过滤器链中。
     * 它依赖于上面定义的 AuthFilter Bean。
     * @param authFilter 从 Spring IoC 容器中注入的 AuthFilter 实例
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<Filter> authFilterRegistration(AuthFilter authFilter) {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        
        // 确保注入的是一个 jakarta.servlet.Filter
        if (!(authFilter instanceof Filter)) {
            throw new IllegalStateException("The AuthFilter bean must implement jakarta.servlet.Filter");
        }
        
        registrationBean.setFilter((Filter) authFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        
        logger.info("AuthFilter bean registered with URL pattern '/*' and order 1.");
        
        return registrationBean;
    }
}