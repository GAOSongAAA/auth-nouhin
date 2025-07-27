package com.collaboportal.common.login.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录尝试服务
 * 负责跟踪和管理用户的登录失败尝试次数，以防止暴力破解攻击。
 *
 * 主要功能：
 * 1. 记录指定用户的登录失败次数。
 * 2. 在失败次数达到阈值时，暂时锁定用户账户。
 * 3. 自动解锁在锁定时间过期的账户。
 *
 * 实现方式：
 * - 使用 Caffeine 缓存库来存储登录失败次数。
 * - Caffeine 的 `expireAfterWrite` 功能可以方便地实现自动解锁。
 */
@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 5; // 最大失败尝试次数
    private final int LOCK_DURATION_MINUTES = 5; // 账户锁定时间（分钟）

    // 使用 LoadingCache 存储登录失败次数
    // - key: 用户的唯一标识符（例如，邮箱地址）
    // - value: 失败尝试次数
    private final LoadingCache<String, Integer> attemptsCache;

    /**
     * 构造函数
     * 初始化 Caffeine 缓存，设置锁定时间和最大缓存大小。
     */
    public LoginAttemptService() {
        attemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(LOCK_DURATION_MINUTES, TimeUnit.MINUTES) // 5分钟后自动过期（即解锁）
                .maximumSize(1000) // 缓存最大条目数，防止内存溢出
                .build(key -> 0); // 当缓存中没有对应key时，默认返回0
    }

    /**
     * 登录成功处理
     * 当用户成功登录时，重置其失败尝试次数。
     *
     * @param key 用户的唯一标识符
     */
    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    /**
     * 登录失败处理
     * 当用户登录失败时，增加其失败尝试次数。
     *
     * @param key 用户的唯一标识符
     */
    public void loginFailed(String key) {
        int attempts = attemptsCache.get(key);
        attempts++;
        attemptsCache.put(key, attempts);
    }

    /**
     * 检查账户是否被锁定
     *
     * @param key 用户的唯一标识符
     * @return 如果账户被锁定，则返回 true
     */
    public boolean isBlocked(String key) {
        return attemptsCache.get(key) >= MAX_ATTEMPTS;
    }
}
