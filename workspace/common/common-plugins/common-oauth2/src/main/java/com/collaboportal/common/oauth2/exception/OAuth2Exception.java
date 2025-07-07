// ========== 异常处理类 ==========
package com.collaboportal.common.oauth2.exception;

/**
 * OAuth2相关异常基类
 */
public class OAuth2Exception extends RuntimeException {

    private final String providerId;
    private final String errorCode;

    public OAuth2Exception(String message, String providerId, String errorCode) {
        super(message);
        this.providerId = providerId;
        this.errorCode = errorCode;
    }

    public OAuth2Exception(String message, String providerId, String errorCode, Throwable cause) {
        super(message, cause);
        this.providerId = providerId;
        this.errorCode = errorCode;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
