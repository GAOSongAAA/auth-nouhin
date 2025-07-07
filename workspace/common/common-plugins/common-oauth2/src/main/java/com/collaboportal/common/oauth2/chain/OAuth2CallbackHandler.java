// ========== 責任チェーンパターン - OAuth2コールバック処理器インターフェース ==========
package com.collaboportal.common.oauth2.chain;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;

/**
 * OAuth2コールバック処理器インターフェース
 * 責任チェーンパターン - コールバック処理チェーン内の各ノードの契約を定義
 */
@FunctionalInterface
public interface OAuth2CallbackHandler {
    /**
     * OAuth2コールバックリクエストを処理
     * 
     * @param context OAuth2プロバイダーコンテキスト
     * @return trueはチェーンの実行を継続、falseはチェーンの実行を中断
     */
    boolean handle(OAuth2ProviderContext context);
}
