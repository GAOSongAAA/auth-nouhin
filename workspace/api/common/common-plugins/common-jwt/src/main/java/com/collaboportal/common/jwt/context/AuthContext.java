package com.collaboportal.common.jwt.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 認証コンテキストクラス
 * 認証処理に必要な情報を保持する
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthContext {
    // ユーザーのメールアドレス
    private String email;
    // OAuth認証コード
    private String code;
    // OAuth認証のstateパラメータ
    private String state;

    private String moveUrl;
    // HTTPリクエストオブジェクト
    private HttpServletRequest request;
    // HTTPレスポンスオブジェクト
    private HttpServletResponse response;
}
