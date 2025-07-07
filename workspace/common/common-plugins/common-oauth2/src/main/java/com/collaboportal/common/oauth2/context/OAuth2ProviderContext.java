package com.collaboportal.common.oauth2.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OAuth2ProviderContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String selectedProviderId;
    private String requestPath;
    private String requestHost;
    private String state;
    private String redirectUrl;

    private OAuth2ProviderContext(Builder builder) {
        this.request = builder.request;
        this.response = builder.response;
        this.selectedProviderId = builder.selectedProviderId;
        this.requestPath = builder.requestPath;
        this.requestHost = builder.requestHost;
        this.state = builder.state;
        this.redirectUrl = builder.redirectUrl;
    }

    // Getters and Setters
    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getSelectedProviderId() {
        return selectedProviderId;
    }

    public void setSelectedProviderId(String selectedProviderId) {
        this.selectedProviderId = selectedProviderId;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getRequestHost() {
        return requestHost;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static class Builder {
        private HttpServletRequest request;
        private HttpServletResponse response;
        private String selectedProviderId;
        private String requestPath;
        private String requestHost;
        private String state;
        private String redirectUrl;

        public Builder request(HttpServletRequest request) {
            this.request = request;
            if (request != null) {
                this.requestPath = request.getRequestURI();
                this.requestHost = request.getServerName();
            }
            return this;
        }

        public Builder response(HttpServletResponse response) {
            this.response = response;
            return this;
        }

        public Builder selectedProviderId(String selectedProviderId) {
            this.selectedProviderId = selectedProviderId;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder redirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public OAuth2ProviderContext build() {
            return new OAuth2ProviderContext(this);
        }
    }
}