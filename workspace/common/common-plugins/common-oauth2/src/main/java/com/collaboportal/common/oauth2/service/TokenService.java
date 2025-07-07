package com.collaboportal.common.oauth2.service;

import java.util.Map;

/**
 * Token 服務抽象接口
 * 用於 OAuth2 模組與 JWT 模組的解耦
 */
public interface TokenService {

    /**
     * 為用戶生成認證令牌
     * 
     * @param userInfo 用戶信息 Map
     * @return 生成的令牌字符串
     */
    String generateAuthToken(Map<String, Object> userInfo);

    /**
     * 驗證令牌是否有效
     * 
     * @param token 令牌字符串
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 從令牌中提取用戶信息
     * 
     * @param token 令牌字符串
     * @return 用戶信息 Map
     */
    Map<String, Object> extractUserInfo(String token);

    /**
     * 刷新令牌
     * 
     * @param token 舊令牌
     * @return 新令牌
     */
    String refreshToken(String token);
}