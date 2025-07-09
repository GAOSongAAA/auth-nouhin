package com.collaboportal.common.oauth2.strategy.login;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;

/**
 * ログイン処理を行うためのストラテジーインターフェース
 * 関数型インターフェースとして定義されており、ログイン処理を実装する
 */
@FunctionalInterface
public interface LoginStrategy {
    /**
     * ログイン処理を実行する
     * 
     * @param context ログイン処理に必要なオブジェクト
     */
    void login(OAuth2ProviderContext context);

}
