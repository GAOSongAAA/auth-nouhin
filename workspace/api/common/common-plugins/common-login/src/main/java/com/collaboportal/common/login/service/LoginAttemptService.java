package com.collaboportal.common.login.service;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Attr;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ログイン試行サービス
 * ユーザーのログイン失敗試行回数を追跡し、ブルートフォース攻撃を防止します。
 *
 * 主要功能：
 * 1. 指定されたユーザーのログイン失敗回数を記録します。
 * 2. 失敗回数がしきい値に達した場合、一時的にユーザーアカウントをロックします。
 * 3. ロック時間が経過したアカウントを自動的にロック解除します。
 *
 * 实现方式：
 * - Caffeine キャッシュライブラリを使用して、ログイン失敗回数を保存します。
 * - Caffeine の `expireAfterWrite` 機能を使用して、自動ロック解除を簡単に実現します。
 */
@Service
public class LoginAttemptService {

    private final JwtService jwtService;
    private static final int MAX_ATTEMPTS = 3;


    private LoginAttemptService(JwtService jwtService) {
        this.jwtService = jwtService;
    }



    /**
     * ログイン成功処理
     * ユーザーがログイン成功した場合、失敗試行回数をリセットします。
     *
     * @param key ユーザーの一意の識別子
     */
    public void loginSucceeded(String key) {
    }

    public void loginFailed(String uid) {

    /**
     * アカウントがロックされているかどうかを確認します
     *
     * @param key ユーザーの一意の識別子
     * @return アカウントがロックされている場合はtrueを返します
     */
    public boolean isBlocked(String retryToken) {
        Integer retry = jwtService.extractClaim(retryToken, "retry");
        if (retry == null) {
            return false;
        }
        return retry >= MAX_ATTEMPTS;
    }
}
