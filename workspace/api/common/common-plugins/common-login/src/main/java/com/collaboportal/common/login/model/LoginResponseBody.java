package com.collaboportal.common.login.model;

/**
 * 登录接口统一响应体
 */
public record LoginResponseBody(
        String code, // 业务码：200=成功，其余=失败
        String message, // 提示信息
        UserInfo user // 成功时返回最小用户信息；失败为 null
) {

    /* ========= 静态工厂方法 ========= */

    public static LoginResponseBody ok(UserInfo user) {
        return new LoginResponseBody("200", "ログイン成功", user);
    }

    public static LoginResponseBody fail(String code, String msg) {
        return new LoginResponseBody(code, msg, null);
    }

    /* ========= 内嵌只读用户 DTO ========= */
    public record UserInfo(String id, String username) {
    }
}
