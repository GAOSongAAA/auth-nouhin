package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.filter.JwtAuthFilter;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.template.ext.JwtValidationTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * OAuth2 フィルター自動設定クラス
 * JWT 認証フィルターと関連コンポーネントの登録を担当する
 */
@AutoConfiguration
public class OAuth2FilterAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2FilterAutoConfiguration.class);

    public OAuth2FilterAutoConfiguration() {
        logger.debug("OAuth2 フィルター自動設定の初期化が完了しました");
    }

    /**
     * JWT 検証テンプレート Bean
     * JWT 検証のコアロジックを提供する
     * 
     * @param jwtTokenUtil              JWT ユーティリティクラス
     * @param clientRegistrationFactory OAuth2 クライアント登録ファクトリ
     * @return JwtValidationTemplate インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(JwtValidationTemplate.class)
    @ConditionalOnBean({ JwtTokenUtil.class, OAuth2ClientRegistrationFactory.class })
    public JwtValidationTemplate jwtValidationTemplate(
            @Autowired JwtTokenUtil jwtTokenUtil,
            @Autowired OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        logger.debug("JwtValidationTemplate Bean を登録します");
        return new JwtValidationTemplate(jwtTokenUtil, clientRegistrationFactory);
    }

    /**
     * JWT 検証チェーン Bean
     * JWT 検証処理チェーンを構築する
     * 
     * @param jwtValidationTemplate JWT 検証テンプレート
     * @return JwtValidationChain インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(JwtValidationChain.class)
    @ConditionalOnBean(JwtValidationTemplate.class)
    @DependsOn("jwtValidationTemplate")
    public JwtValidationChain jwtValidationChain(
            @Autowired JwtValidationTemplate jwtValidationTemplate) {
        logger.debug("JwtValidationChain Bean を登録します");
        return jwtValidationTemplate.buildValidationChain();
    }

    /**
     * JWT 認証フィルター Bean
     * メインの JWT 認証フィルター
     * 
     * @param jwtTokenUtil              JWT ユーティリティクラス
     * @param clientRegistrationFactory OAuth2 クライアント登録ファクトリ
     * @return JwtAuthFilter インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(JwtAuthFilter.class)
    @ConditionalOnBean({ JwtTokenUtil.class, OAuth2ClientRegistrationFactory.class })
    public JwtAuthFilter jwtAuthFilter(
            @Autowired JwtTokenUtil jwtTokenUtil,
            @Autowired OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        logger.debug("JwtAuthFilter Bean を登録します");
        return new JwtAuthFilter(jwtTokenUtil, clientRegistrationFactory);
    }
}