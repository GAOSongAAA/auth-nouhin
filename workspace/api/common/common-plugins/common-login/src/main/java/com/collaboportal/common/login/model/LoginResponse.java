package com.collaboportal.common.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应的数据传输对象 (DTO)
 * 封装了成功登录后返回给客户端的信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JSON Web Token (JWT)
     * 用于后续的API请求身份验证。
     */
    private String token;
}
