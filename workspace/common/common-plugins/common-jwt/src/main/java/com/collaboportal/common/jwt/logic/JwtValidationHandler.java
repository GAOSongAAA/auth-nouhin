package com.collaboportal.common.jwt.logic;

import com.collaboportal.common.jwt.context.JwtValidationContext;

/**
 * JWT検証ハンドラーのインターフェース
 * 各検証処理を実装するための関数型インターフェース
 */
@FunctionalInterface
public interface JwtValidationHandler {
    /**
     * JWTの検証処理を実行する
     * 
     * @param context JWT検証コンテキスト
     * @return 検証が成功した場合はtrue、失敗した場合はfalse
     */
    boolean handle(JwtValidationContext context);
}
