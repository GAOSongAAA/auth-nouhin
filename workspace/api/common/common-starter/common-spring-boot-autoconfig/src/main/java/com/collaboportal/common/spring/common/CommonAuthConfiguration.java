// ファイルパス: com/collaboportal/common/spring/common/CommonAuthConfiguration.java
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
     * AuthFilter 自体を Bean として定義します。
     * これにより、他の @Configuration クラスから注入され、設定（例：setAuth の呼び出し）が可能になります。
     * @ConditionalOnMissingBean は、ユーザーが自身のプロジェクトで同名の Bean を定義してデフォルトの動作を上書きすることを許可します。
     * @param strategyRegistry 自動注入される認証戦略レジストリ
     * @return AuthFilter インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(AuthFilter.class)
    public AuthFilter authFilter(AuthenticationStrategyRegistry strategyRegistry) {
        logger.info("デフォルトの AuthServletFilter Bean を作成しています。これはユーザーによって上書き可能です。");
        // ここでいくつかのデフォルト設定を行うことができます
        return new AuthServletFilter(strategyRegistry)
                .addExclude("/login.html","/login", "/error", "/static/**", "/favicon.ico","/testEnv","/testEnv.html");
    }

    /**
     * AuthFilter Bean を Servlet コンテナのフィルターチェーンに登録する役割を担います。
     * これは、上記で定義された AuthFilter Bean に依存します。
     * @param authFilter Spring IoC コンテナから注入された AuthFilter インスタンス
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<Filter> authFilterRegistration(AuthFilter authFilter) {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        
        // 注入されたものが jakarta.servlet.Filter であることを確認します
        if (!(authFilter instanceof Filter)) {
            throw new IllegalStateException("AuthFilter Bean は jakarta.servlet.Filter を実装している必要があります");
        }
        
        registrationBean.setFilter((Filter) authFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        
        logger.info("AuthFilter Bean が URL パターン '/*' と順序 1 で登録されました。");
        
        return registrationBean;
    }
}