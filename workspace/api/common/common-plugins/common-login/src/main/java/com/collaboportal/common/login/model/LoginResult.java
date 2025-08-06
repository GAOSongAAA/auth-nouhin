package com.collaboportal.common.login.model;

public record LoginResult(
        boolean success, // 是否成功
        String code, // "200" / "401" 等业务码
        String message, // 失败时的提示
        String userId, // ↓ 以下都是成功才有
        String username) {
    /* ======= 工厂方法 ======= */

    public static LoginResult ok(
            String id, String name) {
        return new LoginResult(true, "200", "Login success",
                id, name);
    }

    public static LoginResult fail(String code, String msg) {
        return new LoginResult(false, code, msg,
                null, null);
    }
}
