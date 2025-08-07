package com.collaboportal.common.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * ログイン応答ボディ
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseBody {
    
    private boolean success;
    private String code;
    private String message;
    private String redirectUrl; // 🔥 重定向URL字段
    private Object data;

    // 构造函数
    public LoginResponseBody() {}

    public LoginResponseBody(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public LoginResponseBody(boolean success, String code, String message, String redirectUrl) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.redirectUrl = redirectUrl;
    }

    // 静态工厂方法
    public static LoginResponseBody success(String code, String message) {
        return new LoginResponseBody(true, code, message);
    }

    public static LoginResponseBody success(String code, String message, String redirectUrl) {
        return new LoginResponseBody(true, code, message, redirectUrl);
    }

    public static LoginResponseBody fail(String code, String message) {
        return new LoginResponseBody(false, code, message);
    }

    public static LoginResponseBody ok(String redirectUrl) {
        return new LoginResponseBody(true, "200", "操作成功", redirectUrl);
    }

    // Getter and Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}