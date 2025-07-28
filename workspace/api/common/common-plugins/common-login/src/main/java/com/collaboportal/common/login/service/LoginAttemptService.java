package com.collaboportal.common.login.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Service;

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

    private final int MAX_ATTEMPTS = 5; // 最大失敗試行回数
    private final int LOCK_DURATION_MINUTES = 5; // アカウントロック時間（分）

    // LoadingCache を使用して、ログイン失敗回数を保存します
    // - key: ユーザーの一意の識別子（例：メールアドレス）
    // - value: 失敗試行回数
    private final LoadingCache<String, Integer> attemptsCache;

    /**
     * 构造函数
     * Caffeine キャッシュを初期化し、ロック時間と最大キャッシュサイズを設定します。
     */
    public LoginAttemptService() {
        attemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(LOCK_DURATION_MINUTES, TimeUnit.MINUTES) // 5分後に自動的に期限切れ（即ロック解除）
                .maximumSize(1000) // キャッシュ最大項目数、メモリーリーク防止
                .build(key -> 0); // キャッシュに対応するkeyがない場合、デフォルトで0を返す
    }

    /**
     * ログイン成功処理
     * ユーザーがログイン成功した場合、失敗試行回数をリセットします。
     *
     * @param key ユーザーの一意の識別子
     */
    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    /**
            * ログイン失敗処理
     * ユーザーがログイン失敗した場合、失敗試行回数を増加します。
     *
     * @param key ユーザーの一意の識別子
     */
    public void loginFailed(String key) {
        int attempts = attemptsCache.get(key);
        attempts++;
        attemptsCache.put(key, attempts);
    }

    /**
     * アカウントがロックされているかどうかを確認します
     *
     * @param key ユーザーの一意の識別子
     * @return アカウントがロックされている場合はtrueを返します
     */
    public boolean isBlocked(String key) {
        return attemptsCache.get(key) >= MAX_ATTEMPTS;
    }
}
