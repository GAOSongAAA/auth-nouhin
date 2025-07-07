package com.collaboportal.common.jwt.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT検証コンテキストクラス
 * JWT検証処理に必要な情報を保持する
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtValidationContext {
    // HTTPリクエストオブジェクト
    private HttpServletRequest request;
    // HTTPレスポンスオブジェクト
    private HttpServletResponse response;
    // 認証済みかどうかのフラグ
    private boolean isAuthenticated;
    // リダイレクト先URL
    private String redirectUrl;
    // 移動先URL
    private String moveUrl;
    // JWTトークン
    private String token;
    // 使用するストラテジーのキー
    private String strategyKey;
}
