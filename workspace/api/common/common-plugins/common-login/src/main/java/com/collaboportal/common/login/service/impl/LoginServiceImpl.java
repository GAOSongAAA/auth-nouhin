package com.collaboportal.common.login.service.impl;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.login.mapper.LoginMapper;
import com.collaboportal.common.login.model.DTO.UserEPL;
import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.service.LoginAttemptService;
import com.collaboportal.common.login.service.LoginService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ログインサービスの実装クラス
 * パスワード検証、失敗ロック、JWT生成などの主要なログインロジックを実装します。
 */
@Service
public class LoginServiceImpl implements LoginService {

    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final LoginAttemptService loginAttemptService;

    /**
     * コンストラクタ
     * 必要なサービスとコンポーネントを初期化するために、依存性注入を使用します。
     *
     * @param loginMapper        データベース操作のためのMyBatis Mapper
     * @param passwordEncoder   パスワードの暗号化と検証を行うサービス
     * @param jwtTokenUtil      JWTを生成するためのユーティリティクラス
     * @param loginAttemptService ログイン失敗を処理するサービス
     */
    public LoginServiceImpl(LoginMapper loginMapper, PasswordEncoder passwordEncoder,
                           JwtTokenUtil jwtTokenUtil, LoginAttemptService loginAttemptService) {
        this.loginMapper = loginMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * ユーザーログイン認証
     * 1. アカウントがロックされているかどうかを確認します。
     * 2. データベースからユーザー情報を取得します。
     * 3. パスワードを検証します。
     * 4. ログイン成功後、JWTを生成して返します。
     * 5. ログイン失敗後、失敗回数を記録します。
     *
     * @param loginRequest ユーザー認証情報を含むログインリクエスト
     * @return JWTを含むログイン応答
     * @throws AuthenticationException 認証に失敗した場合
     */
    @Override
    public void login(LoginRequest loginRequest) {
        BaseResponse response = CommonHolder.getResponse();
        // アカウントがロックされているかどうかを確認します
        if (loginAttemptService.isBlocked(loginRequest.getUsername())) {
            throw new AuthenticationException("アカウントがロックされています");
        }

        // データベースからユーザー情報を取得します
        UserEPL user = loginMapper.findUserByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new AuthenticationException("ユーザーが見つかりません");
        }

        // パスワードを検証します
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // ログイン成功、失敗回数をリセットします
            loginAttemptService.loginSucceeded(loginRequest.getUsername());

            // JWTを生成します
            String token = jwtTokenUtil.generateTokenFromObject(user);
            CookieUtil.setNoneSameSiteCookie(response, "token", token);
            response.redirect("/index.html");
        } else {
            // ログイン失敗、失敗回数を増加します
            loginAttemptService.loginFailed(loginRequest.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }
    }
}
