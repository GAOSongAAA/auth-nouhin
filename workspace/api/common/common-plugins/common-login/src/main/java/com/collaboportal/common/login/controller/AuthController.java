package com.collaboportal.common.login.controller;

import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.model.LoginResponse;
import com.collaboportal.common.login.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 负责处理所有与认证相关的HTTP请求，例如登录。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 构造函数
     * 通过依赖注入接收认证服务。
     *
     * @param authService 认证服务
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录端点
     * 接收登录凭证，并返回一个JWT。
     *
     * @param loginRequest 包含用户凭证的登录请求
     * @return 如果成功，返回包含JWT的200 OK响应；如果失败，由全局异常处理器处理
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
