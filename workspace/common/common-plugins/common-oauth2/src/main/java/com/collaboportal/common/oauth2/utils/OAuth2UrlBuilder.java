// ========== 工具类 - OAuth2 URL构建器 ==========
package com.collaboportal.common.oauth2.utils;

import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * OAuth2 URL构建工具类
 * 建造者模式的辅助工具 - 构建OAuth2相关的URL
 */
public class OAuth2UrlBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UrlBuilder.class);

    /**
     * 构建授权URL
     */
    public static String buildAuthorizationUrl(OAuth2ClientRegistration registration, String state,
            Map<String, String> additionalParams) {
        try {
            StringBuilder urlBuilder = new StringBuilder(registration.getAuthorizationUri());
            urlBuilder.append("?response_type=").append(URLEncoder.encode(registration.getResponseType(), "UTF-8"));
            urlBuilder.append("&client_id=").append(URLEncoder.encode(registration.getClientId(), "UTF-8"));
            urlBuilder.append("&redirect_uri=").append(URLEncoder.encode(registration.getRedirectUri(), "UTF-8"));
            urlBuilder.append("&scope=").append(URLEncoder.encode(registration.getScope(), "UTF-8"));
            urlBuilder.append("&state=").append(URLEncoder.encode(state, "UTF-8"));

            // 添加额外参数
            if (additionalParams != null) {
                for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
                    urlBuilder.append("&").append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            }

            return urlBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            logger.error("构建授权URL时编码失败", e);
            throw new RuntimeException("URL编码失败", e);
        }
    }

    /**
     * 构建登录触发URL
     */
    public static String buildLoginUrl(String baseUrl, String providerId) {
        return baseUrl + "/auth/login?provider=" + providerId;
    }

    /**
     * 构建回调URL
     */
    public static String buildCallbackUrl(String baseUrl) {
        return baseUrl + "/auth/callback";
    }
}