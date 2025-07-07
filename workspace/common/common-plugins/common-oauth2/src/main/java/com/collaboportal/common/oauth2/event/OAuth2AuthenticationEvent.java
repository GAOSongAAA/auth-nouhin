package com.collaboportal.common.oauth2.event;

import org.springframework.context.ApplicationEvent;
import java.util.Map;

/**
 * OAuth2 認證事件
 * 用於在認證完成時通知其他模組
 */
public class OAuth2AuthenticationEvent extends ApplicationEvent {

    private final String providerId;
    private final Map<String, Object> userInfo;
    private final String authToken;
    private final boolean isSuccess;
    private final String errorMessage;

    /**
     * 認證成功事件構造器
     */
    public OAuth2AuthenticationEvent(Object source, String providerId,
            Map<String, Object> userInfo, String authToken) {
        super(source);
        this.providerId = providerId;
        this.userInfo = userInfo;
        this.authToken = authToken;
        this.isSuccess = true;
        this.errorMessage = null;
    }

    /**
     * 認證失敗事件構造器
     */
    public OAuth2AuthenticationEvent(Object source, String providerId,
            String errorMessage) {
        super(source);
        this.providerId = providerId;
        this.userInfo = null;
        this.authToken = null;
        this.isSuccess = false;
        this.errorMessage = errorMessage;
    }

    // Getters
    public String getProviderId() {
        return providerId;
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return String.format("OAuth2AuthenticationEvent{providerId='%s', success=%s, errorMessage='%s'}",
                providerId, isSuccess, errorMessage);
    }
}