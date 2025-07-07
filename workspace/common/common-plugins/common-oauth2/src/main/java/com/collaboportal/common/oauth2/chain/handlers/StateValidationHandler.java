// ========== 具体処理器 - 状態検証処理器 ==========
package com.collaboportal.common.oauth2.chain.handlers;

import com.collaboportal.common.oauth2.chain.OAuth2CallbackHandler;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

/**
 * OAuth2状態検証処理器
 * 責任チェーン具体実装 - OAuth2コールバックのstateパラメータを検証する
 */
@Component
public class StateValidationHandler implements OAuth2CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(StateValidationHandler.class);

    @Override
    public boolean handle(OAuth2ProviderContext context) {
        logger.debug("[状態検証] OAuth2コールバック状態の検証を開始");

        String callbackState = context.getRequest().getParameter("state");
        if (callbackState == null || callbackState.isEmpty()) {
            logger.error("[状態検証] コールバックリクエストにstateパラメータが不足");
            return false;
        }

        // Cookieから保存された状態情報を取得
        String storedState = getStateFromCookie(context);
        if (storedState == null) {
            logger.error("[状態検証] 保存された状態情報が見つかりません");
            return false;
        }

        // 保存された状態情報を解析 (フォーマット: providerId:state)
        String[] stateParts = storedState.split(":", 2);
        if (stateParts.length != 2) {
            logger.error("[状態検証] 保存された状態情報のフォーマットが正しくありません: {}", storedState);
            return false;
        }

        String expectedState = stateParts[1];
        String providerId = stateParts[0];

        // 状態の一致を検証
        if (!callbackState.equals(expectedState)) {
            logger.error("[状態検証] 状態検証が失敗しました、期待値: {}, 実際: {}", expectedState, callbackState);
            return false;
        }

        // 選択されたプロバイダーIDを設定
        context.setSelectedProviderId(providerId);
        context.setState(callbackState);

        logger.debug("[状態検証] 状態検証が成功しました、プロバイダー: {}", providerId);
        return true;
    }

    private String getStateFromCookie(OAuth2ProviderContext context) {
        Cookie[] cookies = context.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("OAUTH2_STATE".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}