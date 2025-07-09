package com.collaboportal.common.oauth2.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OAuth2客户端注册信息
 * 使用建造者模式构建复杂配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuth2ClientRegistration {
    private String providerId;
    private String issuer;
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String audience;
    private String redirectUri;
    private String scope;
    private List<String> pathPatterns;
    private String userNameAttribute;
    private String displayName;

}
