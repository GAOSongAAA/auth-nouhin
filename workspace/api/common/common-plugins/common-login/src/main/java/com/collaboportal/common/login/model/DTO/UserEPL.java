package com.collaboportal.common.login.model.DTO;

import lombok.Data;

/**
 * 登录用户的数传对象 (DTO)
 * 包含用于认证和JWT生成所需的用户基本信息。
 */
@Data
public class UserEPL {

    /**
     * 用户唯一ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 加密后的用户密码
     */
    private String password;

    /**
     * 用户邮箱
     */
    private String email;
}