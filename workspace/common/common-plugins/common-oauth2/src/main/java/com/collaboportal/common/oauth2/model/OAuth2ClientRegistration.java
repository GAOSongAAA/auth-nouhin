// ========== OAuth2客户端注册模型 ==========
package com.collaboportal.common.oauth2.model;

import java.util.List;

/**
 * OAuth2客户端注册信息
 * 使用建造者模式构建复杂配置
 */
public class OAuth2ClientRegistration {
    private String providerId;
    private String clientId;
    private String clientSecret;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String redirectUri;
    private String scope;
    private String responseType;
    private String grantType;
    private List<String> pathPatterns;
    private String userNameAttribute;
    private String displayName;

    private OAuth2ClientRegistration(Builder builder) {
        this.providerId = builder.providerId;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.authorizationUri = builder.authorizationUri;
        this.tokenUri = builder.tokenUri;
        this.userInfoUri = builder.userInfoUri;
        this.redirectUri = builder.redirectUri;
        this.scope = builder.scope;
        this.responseType = builder.responseType;
        this.grantType = builder.grantType;
        this.pathPatterns = builder.pathPatterns;
        this.userNameAttribute = builder.userNameAttribute;
        this.displayName = builder.displayName;
    }

    // Getters
    public String getProviderId() {
        return providerId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getGrantType() {
        return grantType;
    }

    public List<String> getPathPatterns() {
        return pathPatterns;
    }

    public String getUserNameAttribute() {
        return userNameAttribute;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 建造者模式 - 构建复杂的OAuth2客户端配置
     */
    public static class Builder {
        private String providerId;
        private String clientId;
        private String clientSecret;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String redirectUri;
        private String scope;
        private String responseType = "code";
        private String grantType = "authorization_code";
        private List<String> pathPatterns;
        private String userNameAttribute = "email";
        private String displayName;

        public Builder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder authorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
            return this;
        }

        public Builder tokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
            return this;
        }

        public Builder userInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder responseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public Builder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public Builder pathPatterns(List<String> pathPatterns) {
            this.pathPatterns = pathPatterns;
            return this;
        }

        public Builder userNameAttribute(String userNameAttribute) {
            this.userNameAttribute = userNameAttribute;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public OAuth2ClientRegistration build() {
            return new OAuth2ClientRegistration(this);
        }
    }
}
