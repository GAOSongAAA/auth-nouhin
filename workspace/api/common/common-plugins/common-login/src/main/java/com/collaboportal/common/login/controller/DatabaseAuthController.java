package com.collaboportal.common.login.controller;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.model.LoginResponseBody;
import com.collaboportal.common.login.model.LoginResponseBody.UserInfo;
import com.collaboportal.common.login.model.LoginResult;
import com.collaboportal.common.login.service.LoginService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証コントローラー
 * /auth/login でデータベース認証のみを処理。
 */
@RestController
@RequestMapping("/auth")
public class DatabaseAuthController {

    private final LoginService loginService;

    public DatabaseAuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    /** ユーザーログイン */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseBody> login(@RequestBody LoginRequest loginRequest) {

        LoginResult result = loginService.login(loginRequest);

        // 成功
        if (result.success()) {
            UserInfo ui = new UserInfo(result.userId(), result.username());
            BaseResponse response = CommonHolder.getResponse();
            response.redirect("/index.html");
            return ResponseEntity.ok(LoginResponseBody.ok(ui));
        }
        return ResponseEntity
                .status(401)
                .body(LoginResponseBody.fail("401", "ユーザー名またはパスワードが違います"));
    }
}
