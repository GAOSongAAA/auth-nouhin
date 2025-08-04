package com.collaboportal.common.login.service;

import com.collaboportal.common.login.model.LoginRequest;


/**
 * ログインサービスのインターフェース定義
 * 核心のログイン認証ロジックを封装します。
 */
public interface LoginService {

    /**
     * ユーザーログイン認証
     *
     * @param loginRequest ユーザー認証情報（メールアドレスとパスワードなど）を含むログインリクエストオブジェクト
     * @return 認証トークンを含むログインレスポンスオブジェクト
     */
    void login(LoginRequest loginRequest);


}
