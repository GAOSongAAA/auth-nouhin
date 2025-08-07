package com.collaboportal.common.login.controller;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.model.LoginResponseBody;
import com.collaboportal.common.login.model.LoginResult;
import com.collaboportal.common.login.service.LoginService;
import com.collaboportal.common.utils.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 認證控制器
 * /auth/login 僅處理資料庫認證。
 * 包含CORS對應和安全Cookie設定
 */
@RestController
@RequestMapping("/auth")
public class DatabaseAuthController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthController.class);

    private final LoginService loginService;

    public DatabaseAuthController(LoginService loginService) {
        this.loginService = loginService;
        logger.info("資料庫認證控制器已初始化");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseBody> login(@RequestBody LoginRequest loginRequest) {
    
        logger.info("接收到登入請求。使用者: {}", loginRequest.getEmail());
        BaseResponse response = CommonHolder.getResponse();
    
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            logger.warn("無效的登入請求: 電子郵件地址為空");
            return ResponseEntity
                    .badRequest()
                    .body(LoginResponseBody.fail("400", "電子郵件地址為必填項"));
        }
    
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            logger.warn("無效的登入請求: 密碼為空");
            return ResponseEntity
                    .badRequest()
                    .body(LoginResponseBody.fail("400", "密碼為必填項"));
        }
    
        try {
            LoginResult result = loginService.login(loginRequest);
    
            if (result.success()) {
                logger.info("登入成功。使用者ID: {}, 電子郵件地址: {}",
                        result.userId(), loginRequest.getEmail());
    
                if (result.token() != null && !result.token().isEmpty()) {
                    CookieUtil.setNoneSameSiteCookie(response, Message.Cookie.AUTH, result.token());
                    logger.info("已設定認證Cookie。使用者ID: {}", result.userId());
                } else {
                    logger.warn("JWT token未生成。使用者ID: {}", result.userId());
                }
                
                // 返回JSON響應而非重定向，讓前端處理跳轉
                return ResponseEntity
                        .ok()
                        .header("Content-Type", "application/json")
                        .body(LoginResponseBody.ok("/index.html"));
    
            } else {
                logger.warn("登入失敗。使用者: {}, 原因: {}",
                        loginRequest.getEmail(),
                        result.message() != null ? result.message() : "未知原因");
                return ResponseEntity
                        .status(401)
                        .header("Content-Type", "application/json")
                        .body(LoginResponseBody.fail("401", "使用者名稱或密碼錯誤"));
            }
    
        } catch (Exception e) {
            logger.error("登入處理過程中發生錯誤。使用者: {}, 錯誤: {}",
                    loginRequest.getEmail(), e.getMessage(), e);
    
            return ResponseEntity
                    .status(500)
                    .header("Content-Type", "application/json")
                    .body(LoginResponseBody.fail("500", "內部伺服器錯誤"));
        }
    }
    
    
}