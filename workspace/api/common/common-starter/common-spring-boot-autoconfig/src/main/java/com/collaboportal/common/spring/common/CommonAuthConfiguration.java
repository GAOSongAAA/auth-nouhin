package com.collaboportal.common.spring.common;

import com.collaboportal.common.filter.AuthServletFilter;
import com.collaboportal.common.spring.registry.AuthenticationStrategyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonAuthConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CommonAuthConfiguration.class);

    private final AuthenticationStrategyRegistry strategyRegistry;

    public CommonAuthConfiguration(AuthenticationStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
        logger.debug("CommonAuthConfiguration initialized with AuthenticationStrategyRegistry.");
    }

    @Bean
    public FilterRegistrationBean<AuthServletFilter> authServletFilterRegistration() {
        FilterRegistrationBean<AuthServletFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new AuthServletFilter(strategyRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        
        logger.info("AuthServletFilter registered with URL pattern '/*' and order 1.");
        
        return registrationBean;
    }
}
