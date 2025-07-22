package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.processor.impl.AuthProcessorImpl;
import com.collaboportal.common.oauth2.factory.UserInfoServiceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * OAuth2 コア自動設定クラス
 * OAuth2 関連のコアコンポーネントの登録を担当する
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.oauth2.factory",
        "com.collaboportal.common.oauth2.processor",
        "com.collaboportal.common.oauth2.utils"
})
public class OAuth2CoreAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2CoreAutoConfiguration.class);

    public OAuth2CoreAutoConfiguration() {
        logger.debug("OAuth2 コア自動設定の初期化が完了しました");
    }

    /**
     * OAuth2 クライアント登録ファクトリ Bean
     * OAuth2 プロバイダーの設定情報を管理する
     * 
     * @return OAuth2ClientRegistrationFactory インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2ClientRegistrationFactory.class)
    public OAuth2ClientRegistrationFactory oAuth2ClientRegistrationFactory() {
        logger.debug("OAuth2ClientRegistrationFactory Bean を登録します");
        OAuth2ClientRegistrationFactory bean = new OAuth2ClientRegistrationFactory();
        return bean;
    }

    /**
     * 認証プロセッサ Bean
     * OAuth2 認証フローを処理する
     *  
     * @return AuthProcessor インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(AuthProcessor.class)
    public AuthProcessor authProcessor() {
        logger.debug("AuthProcessor Bean を登録します");
        return new AuthProcessorImpl();
    }



    /**
     * ユーザー情報サービスファクトリ Bean
     * 注意：UserInfoServiceFactory は静的ファクトリクラスです。ここでは主に初期化を確実にするためです
     * 
     * @return UserInfoServiceFactory インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(UserInfoServiceFactory.class)
    public UserInfoServiceFactory userInfoServiceFactory() {
        logger.debug("UserInfoServiceFactory を初期化します");
        return new UserInfoServiceFactory();
    }

    /**
     * OAuth2 設定プロパティ Bean
     * 外部設定ファイルの OAuth2 関連プロパティをバインドする
     * 
     * @return OAuth2ConfigurationProperties インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2ConfigurationProperties.class)
    public OAuth2ConfigurationProperties oAuth2ConfigurationProperties() {
        logger.debug("OAuth2ConfigurationProperties Bean を登録します");
        return new OAuth2ConfigurationProperties();
    }
}