package com.collaboportal.common.oauth2.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;

import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final Logger logger = LoggerFactory.getLogger(CookieUtil.class);

    /**
     * Cookie設定処理
     * 
     * @param response HTTPレスポンス
     * @param name     Cookie名
     * @param value    Cookie値
     */
    public static void setNoneSameSiteCookie(HttpServletResponse response, String name, String value) {
        logger.debug("Cookieを設定します。{}={}", name, value);
        String secureFlag = (ConfigManager.getConfig().isCookieSecure()) ? "; Secure" : "";
        response.addHeader("Set-Cookie", name + "=" + value + "; Path=/; Max-Age="
                + ConfigManager.getConfig().getCookieExpiration() + "; SameSite=None" + secureFlag);
    }

    public static void setSameSiteCookie(HttpServletResponse response, String name, String value) {
        logger.debug("Cookieを設定します。{}={}", name, value);
        String secureFlag = (ConfigManager.getConfig().isCookieSecure()) ? "; Secure" : "";
        response.addHeader("Set-Cookie", name + "=" + value + "; Path=/; Max-Age="
                + ConfigManager.getConfig().getCookieExpiration() + "; SameSite=Strict" + secureFlag);
    }

}
