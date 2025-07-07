package com.collaboportal.common.jwt.logic.callbacklogin;

import java.util.HashMap;
import java.util.Map;

import com.collaboportal.common.strategy.LoginStrategy;

/**
 * ログインストラテジーを管理するレジストリクラス
 * 基底クラスとして使用され、子クラスで具体的な実装を提供
 */
public class LoginStrategyRegistry {

    // ログインストラテジーを保持するマップ
    private final Map<String, LoginStrategy> strategyMap = new HashMap<>();

    /**
     * 初期化メソッド - 子クラスで必要に応じてオーバーライド可能
     */
    public void init() {
        // デフォルトでは何もしない
        // 子クラスで必要に応じて初期化ロジックを実装
    }

    /**
     * ログインストラテジーを登録する
     * 
     * @param key      ストラテジーを識別するキー
     * @param strategy 登録するログインストラテジー
     */
    public void register(String key, LoginStrategy strategy) {
        strategyMap.put(key, strategy);
    }

    /**
     * 指定されたキーに対応するログインストラテジーを取得する
     * 
     * @param key 取得するストラテジーのキー
     * @return 対応するログインストラテジー
     */
    public LoginStrategy getStrategy(String key) {
        return strategyMap.get(key);
    }

}
