package com.collaboportal.common.jwt.logic.callbacklogin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.collaboportal.common.strategy.LoginStrategy;

/**
 * ログインストラテジーを管理するレジストリクラス
 */
@Component
public class LoginStrategyRegistry {

    // ログインストラテジーを保持するマップ
    private final Map<String, LoginStrategy> strategyMap = new HashMap<>();

    /**
     * ログインストラテジーを登録する
     * @param key ストラテジーを識別するキー
     * @param strategy 登録するログインストラテジー
     */
    public void register(String key, LoginStrategy strategy){
        strategyMap.put(key, strategy);
    }

    /**
     * 指定されたキーに対応するログインストラテジーを取得する
     * @param key 取得するストラテジーのキー
     * @return 対応するログインストラテジー
     */
    public LoginStrategy getStrategy(String key){
        return strategyMap.get(key);
    }

}
