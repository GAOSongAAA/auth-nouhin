// ========== 具体处理器 - 授权码验证处理器 ==========
package com.collaboportal.common.oauth2.chain.handlers;

import com.collaboportal.common.oauth2.chain.OAuth2CallbackHandler;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 授权码验证处理器
 * 责任链具体实现 - 验证授权码的基本格式和有效性
 */
@Component
public class AuthorizationCodeValidationHandler implements OAuth2CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCodeValidationHandler.class);

    @Override
    public boolean handle(OAuth2ProviderContext context) {
        logger.debug("[授权码验证] 开始验证授权码");

        String code = context.getRequest().getParameter("code");
        if (code == null || code.isEmpty()) {
            logger.error("[授权码验证] 回调请求缺少code参数");
            return false;
        }

        // 基本格式验证
        if (code.length() < 10) {
            logger.error("[授权码验证] 授权码格式错误，长度过短: {}", code.length());
            return false;
        }

        // 检查是否包含错误参数
        String error = context.getRequest().getParameter("error");
        if (error != null) {
            String errorDescription = context.getRequest().getParameter("error_description");
            logger.error("[授权码验证] OAuth2授权失败: {} - {}", error, errorDescription);
            return false;
        }

        logger.debug("[授权码验证] 授权码验证成功");
        return true;
    }
}
