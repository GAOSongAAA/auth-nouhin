package com.collaboportal.common.oauth2.exception;

/**
 * 令牌交换异常
 */
public class OAuth2TokenExchangeException extends OAuth2Exception {

    public OAuth2TokenExchangeException(String message, String providerId, Throwable cause) {
        super(message, providerId, "TOKEN_EXCHANGE_ERROR", cause);
    }
}