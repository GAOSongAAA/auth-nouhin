package com.collaboportal.common.oauth2.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuth2ProviderContext {

    private String email;
    private String code;
    private String state;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String selectedProviderId;
    private String requestPath;
    private String requestHost;
    private String redirectUri;
    private String authProviderUrl;
    private String issuer;
    private String clientId;
    private String clientSecret;
    private String audience;
    private String scope;
    // 使用するストラテジーのキー
    private String strategyKey;
    // トークン
    private String token;

}