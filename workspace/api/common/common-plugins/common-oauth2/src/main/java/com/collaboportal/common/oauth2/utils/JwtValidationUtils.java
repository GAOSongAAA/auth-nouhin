package com.collaboportal.common.oauth2.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseCookie;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.JwtMaintenanceUtil;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;

/**
 * JWT検証ユーティリティクラス
 */
public class JwtValidationUtils {

    static Logger logger = LoggerFactory.getLogger(JwtValidationUtils.class);

    private static final List<String> COOKIE_AUTH_PATHS = new ArrayList<>(List.of("/", "/index.html"));

    public static void addCookieAuthPath(String path) {
        COOKIE_AUTH_PATHS.add(path);
    }

    public static boolean isUseCookieAuthorization(BaseRequest request) {

        String path = request.getRequestPath();
        boolean result = COOKIE_AUTH_PATHS.contains(path);
        if (result) {
            logger.debug("パス [{}] はCookie認証を使用可能です", path);
        } else {
            logger.debug("パス [{}] はCookie認証を使用できません", path);
        }
        return result;
    }

    /**
     * Cookieを設定する
     * 
     * @param response HTTPレスポンス
     * @param name     Cookie名
     * @param value    Cookie値
     */
    public static void setCookie(BaseResponse response, String name, String value) {
        response.addCookie(
                new BaseCookie(name, value).setPath("/").setMaxAge(ConfigManager.getConfig().getCookieExpiration()));
    }

    /**
     * ヘッダーからトークンを抽出する
     * 
     * @param request HTTPリクエスト
     * @return トークン
     */
    public static String extractTokenFromHeader(BaseRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Cookieからトークンを抽出する
     * 
     * @param request HTTPリクエスト
     * @return トークン
     */
    public static String extractTokenFromCookie(BaseRequest request) {
        String cookie = request.getCookieValue("AuthToken");
        return cookie;
    }

    /**
     * 認証リダイレクトURLを構築する
     * 
     * @return 認証リダイレクトURL
     */
    public static String buildAuthRedirectUrl(OAuth2ProviderContext context) {
        String callback = JwtMaintenanceUtil.resolveCallbackUrl();
        return String.format(
                "https://%s/authorize?client_id=%s&audience=%s&redirect_uri=%s&scope=openid%%20profile%%20email%%20offline_access&response_type=code&response_mode=query",
                context.getIssuer(),
                context.getClientId(),
                context.getAudience(),
                callback);
    }

    /**
     * パスに基づいて認証戦略を決定する
     * 
     * @param request HTTPリクエスト
     * @return 認証戦略 ("cookie" または "header")
     */
    public static String decideStrategyByPath(BaseRequest request) {
        String path = request.getRequestPath();
        if ("/mr".equals(path) || "/".equals(path) || "/index.html".equals(path)) {
            return "cookie";
        } else {
            return "header";
        }
    }
}