package com.collaboportal.common.login.service.impl;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.login.mapper.LoginMapper;

import com.collaboportal.common.login.model.DTO.UserMasterEPL;
import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.model.LoginResult;
import com.collaboportal.common.login.service.LoginService;
import com.collaboportal.common.utils.Message;

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
    private final JwtService jwtService;

    /**
     * コンストラクタ
     * 必要なサービスとコンポーネントを初期化するために、依存性注入を使用します。
     *
     * @param loginMapper         データベース操作のためのMyBatis Mapper
     * @param passwordEncoder     パスワードの暗号化と検証を行うサービス
     * @param jwtTokenUtil        JWTを生成するためのユーティリティクラス
     * @param loginAttemptService ログイン失敗を処理するサービス
     */
    public LoginServiceImpl(LoginMapper loginMapper, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.loginMapper = loginMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
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
    public LoginResult login(LoginRequest loginRequest) {

        UserMasterEPL user = loginMapper.findUserByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new AuthenticationException("ユーザーが見つかりません");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // 密码错用 401
            return LoginResult.fail("401", "パスワードが正しくありません");
        }
        String token = jwtService.generateToken(user, JwtConstants.GENERATE_DATABASE_TOKEN);
        String email = user.getUserMail();
        String id = user.getUserId();
        // String role = user.getRole();
        BaseResponse response = CommonHolder.getResponse();
        CookieUtil.setNoneSameSiteCookie(response, Message.Cookie.AUTH, token);
        return LoginResult.ok(id, email);
    }
}
