package com.collaboportal.common.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求的数据传输对象 (DTO)
 * 封装了用户登录时需要提供的凭证信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 用户邮箱地址
     * 作为登录的唯一标识符。
     */
    private String email;

    /**
     * 用户密码
     * 用于验证用户身份。
     */
    private String password;
}
