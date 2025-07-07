package com.collaboportal.common.oauth2.exception;

/**
 * 提供商配置异常
 */
public class OAuth2ProviderConfigurationException extends OAuth2Exception {

    public OAuth2ProviderConfigurationException(String message, String providerId) {
        super(message, providerId, "PROVIDER_CONFIG_ERROR");
    }
}