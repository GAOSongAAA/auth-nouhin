package com.collaboportal.common.oauth2.service;

import java.util.Map;

/**
 * 用戶服務抽象接口
 * 用於 OAuth2 模組與具體用戶實現的解耦
 */
public interface UserService {

    /**
     * 根據郵箱獲取用戶信息
     * 
     * @param email 用戶郵箱
     * @return 用戶信息 Map，如果用戶不存在則返回 null
     */
    Map<String, Object> getUserByEmail(String email);

    /**
     * 創建或更新用戶信息
     * 
     * @param userInfo 用戶信息 Map
     * @return 處理後的用戶信息 Map
     */
    Map<String, Object> createOrUpdateUser(Map<String, Object> userInfo);

    /**
     * 檢查用戶是否存在
     * 
     * @param email 用戶郵箱
     * @return 是否存在
     */
    boolean userExists(String email);

    /**
     * 獲取用戶類型/角色
     * 
     * @param email 用戶郵箱
     * @return 用戶類型字符串
     */
    String getUserType(String email);
}