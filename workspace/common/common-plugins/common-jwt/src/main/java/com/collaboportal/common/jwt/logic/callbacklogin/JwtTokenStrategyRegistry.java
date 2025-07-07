package com.collaboportal.common.jwt.logic.callbacklogin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * JWTトークン解決ストラテジーを管理するレジストリクラス
 */
public class JwtTokenStrategyRegistry {

    Logger logger = LoggerFactory.getLogger(JwtTokenStrategyRegistry.class);

    public JwtTokenStrategyRegistry(){
        logger.debug("JwtTokenStrategyRegistryの初期化が完了しました");
    }

    // ストラテジーを保持するマップ
    // キー: ストラテジー識別キー, 値: トークン解決関数
    private final Map<String, Function<HttpServletRequest, String>> strategyMap = new HashMap<>();

    /**
     * ストラテジーを登録する
     * @param key ストラテジーを識別するキー
     * @param strategy 登録するトークン解決関数
     */
    public void register(String key, Function<HttpServletRequest,String> strategy){
        logger.debug("[ストラテジー登録] 新しいストラテジーを登録します。キー: {}", key);
        strategyMap.put(key, strategy);
        logger.info("[ストラテジー登録] ストラテジーの登録が正常に完了しました。キー: {}", key);
    }

    /**
     * 指定されたキーに対応するストラテジーを取得する
     * @param key 取得するストラテジーのキー
     * @return 対応するトークン解決関数
     */
    public Function<HttpServletRequest, String> getStrategy(String key){
        logger.debug("[ストラテジー取得] ストラテジーの取得を開始します。キー: {}", key);
        Function<HttpServletRequest, String> strategy = strategyMap.get(key);
        if (strategy == null) {
            logger.warn("[ストラテジー取得] 指定されたキーに対応するストラテジーが見つかりませんでした。キー: {}", key);
        } else {
            logger.debug("[ストラテジー取得] ストラテジーの取得に成功しました。キー: {}", key);
        }
        return strategy;
    }

    /**
     * 指定されたストラテジーを使用してトークンを解決する
     * @param request HTTPリクエスト
     * @param strategyKey 使用するストラテジーのキー
     * @return 解決されたトークン。ストラテジーが存在しない場合はnull
     */
    public String resolveToken(HttpServletRequest request, String strategyKey){
        logger.debug("[トークン解決] トークン解決処理を開始します。ストラテジーキー: {}", strategyKey);
        Function<HttpServletRequest, String> strategy = strategyMap.get(strategyKey);
        if (strategy == null) {
            logger.error("[トークン解決] 指定されたストラテジーが見つかりませんでした。キー: {}", strategyKey);
            return null;
        }
        
        String token = strategy.apply(request);
        if (token == null) {
            logger.warn("[トークン解決] トークンの解決に失敗しました。ストラテジーキー: {}", strategyKey);
        } else {
            logger.debug("[トークン解決] トークンの解決に成功しました。ストラテジーキー: {}", strategyKey);
        }
        return token;
    }

}
