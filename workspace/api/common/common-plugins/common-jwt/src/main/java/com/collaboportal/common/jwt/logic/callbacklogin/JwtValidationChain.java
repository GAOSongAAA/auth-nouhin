package com.collaboportal.common.jwt.logic.callbacklogin;

import java.util.ArrayList;
import java.util.List;

import com.collaboportal.common.jwt.context.JwtValidationContext;
import com.collaboportal.common.jwt.logic.JwtValidationHandler;

/**
 * JWT検証チェーンを管理するクラス
 */
public class JwtValidationChain {

    // ハンドラーのリスト
    private final List<JwtValidationHandler> handlers = new ArrayList<>();

    /**
     * ハンドラーをチェーンに追加する
     * 
     * @param handler 追加するJWT検証ハンドラー
     * @return チェーンインスタンス
     */
    public JwtValidationChain addHandler(JwtValidationHandler handler) {
        handlers.add(handler);
        return this;
    }

    /**
     * 検証チェーンを実行する
     * 
     * @param context JWT検証コンテキスト
     * @return すべてのハンドラーが成功した場合はtrue、いずれかのハンドラーが失敗した場合はfalse
     */
    public boolean execute(JwtValidationContext context) {
        for (JwtValidationHandler handler : handlers) {
            if (!handler.handle(context)) {
                return false;
            }
        }
        return true;
    }

}
