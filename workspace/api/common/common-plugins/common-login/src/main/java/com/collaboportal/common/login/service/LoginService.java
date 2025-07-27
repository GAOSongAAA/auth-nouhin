package com.collaboportal.common.login.service;

import com.collaboportal.common.login.model.LoginRequest;

/**
 * 登录服务的接口定义
 * 封装了核心的登录认证逻辑。
 */
public interface LoginService {

    /**
     * 用户登录认证
     *
     * @param loginRequest 包含用户凭证（如邮箱和密码）的登录请求对象
     * @return 包含认证令牌的登录响应对象
     */
    void login(LoginRequest loginRequest);

}
