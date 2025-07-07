package com.collaboportal.common.spring.jwt;

// @ConditionalOnBean(JwtManager.class)
public class JwtBeanRegister {


    // @Bean
    // @Order(1)
    // @ConditionalOnClass(LogTraceIdFilter.class)
    // public FilterRegistrationBean<LogTraceIdFilter> logTraceIdFilter() {
    //     FilterRegistrationBean<LogTraceIdFilter> registration = new FilterRegistrationBean<>();
    //     registration.setFilter(new LogTraceIdFilter());
    //     registration.addUrlPatterns("/*");
    //     return registration;
    // }

    // @Bean
    // @Order(2)
    // @ConditionalOnClass(LogbookFilter.class)
    // public FilterRegistrationBean<LogbookFilter> customLogbookFilter(Logbook logbook) {
    //     FilterRegistrationBean<LogbookFilter> registration = new FilterRegistrationBean<>();
    //     registration.setFilter(new LogbookFilter(logbook));
    //     registration.addUrlPatterns("/*");
    //     return registration;
    // }

    // @Bean
    // @Order(3)
    // @ConditionalOnClass(MaintenanceModeFilter.class)
    // public FilterRegistrationBean<MaintenanceModeFilter> maintenanceModeFilter() {
    //     FilterRegistrationBean<MaintenanceModeFilter> bean = new FilterRegistrationBean<>();
    //     bean.setFilter(new MaintenanceModeFilter());
    //     bean.addUrlPatterns("/ms/*", "/ms", "/honbu", "/honbu/*", "/", "/auth/callback", "/index.html");
    //     return bean;
    // }

    // @Bean
    // @Order(4)
    // @ConditionalOnClass(JwtSignatureValidationFilter.class)
    // public FilterRegistrationBean<JwtSignatureValidationFilter> jwtSignatureValidationFilter(JwtTokenUtil jwtTokenUtil) {
    //     FilterRegistrationBean<JwtSignatureValidationFilter> registration = new FilterRegistrationBean<>();
    //     registration.setFilter(new JwtSignatureValidationFilter(jwtTokenUtil));
    //     registration.addUrlPatterns("/ms/*", "/ms", "/honbu", "/honbu/*", "/");
    //     return registration;
    // }
}
