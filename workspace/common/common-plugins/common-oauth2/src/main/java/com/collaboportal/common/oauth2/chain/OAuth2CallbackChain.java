// ========== 責任チェーンパターン - OAuth2コールバック処理チェーン ==========
package com.collaboportal.common.oauth2.chain;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * OAuth2コールバック処理チェーン
 * 責任チェーンパターン - OAuth2コールバックの前処理チェーンを管理
 * 職責：各処理器を順序通りに実行、いずれかの処理器が失敗した場合はチェーンを中断
 * 利点：処理ロジックを柔軟に組み合わせ可能、新しい検証段階の拡張が容易
 */
public class OAuth2CallbackChain {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2CallbackChain.class);

    private final List<OAuth2CallbackHandler> handlers = new ArrayList<>();

    /**
     * 処理器をチェーンに追加
     */
    public OAuth2CallbackChain addHandler(OAuth2CallbackHandler handler) {
        handlers.add(handler);
        logger.debug("OAuth2コールバック処理器を追加: {}", handler.getClass().getSimpleName());
        return this;
    }

    /**
     * 処理チェーンを実行
     */
    public boolean execute(OAuth2ProviderContext context) {
        logger.debug("OAuth2コールバック処理チェーンの実行を開始、処理器数: {}", handlers.size());

        for (int i = 0; i < handlers.size(); i++) {
            OAuth2CallbackHandler handler = handlers.get(i);
            logger.debug("第{}番目の処理器を実行: {}", i + 1, handler.getClass().getSimpleName());

            try {
                boolean result = handler.handle(context);
                if (!result) {
                    logger.warn("処理器 {} が失敗を返しました、処理チェーンを中断", handler.getClass().getSimpleName());
                    return false;
                }
                logger.debug("処理器 {} の実行が成功", handler.getClass().getSimpleName());
            } catch (Exception e) {
                logger.error("処理器 {} の実行中に例外が発生", handler.getClass().getSimpleName(), e);
                return false;
            }
        }

        logger.debug("OAuth2コールバック処理チェーンの実行が完了");
        return true;
    }
}