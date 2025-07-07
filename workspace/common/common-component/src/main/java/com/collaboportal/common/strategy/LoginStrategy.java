package com.collaboportal.common.strategy;

import com.collaboportal.common.context.AuthContext;

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
    void login(AuthContext context);

}
