package com.collaboportal.common.login.controller;

import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証コントローラー
 * 認証に関連するすべてのHTTPリクエスト（例：ログイン）を処理します。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;

    /**
     * コンストラクタ
     * 依存性注入によって認証サービスを受け取ります。
     *
     * @param loginService 認証サービス
     */
    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * ユーザーログインエンドポイント
     * ログイン認証情報を受け取り、JWTを返します。
     *
     * @param loginRequest ユーザー認証情報を含むログインリクエスト
     */
    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest) {
        loginService.login(loginRequest);
    }
}
