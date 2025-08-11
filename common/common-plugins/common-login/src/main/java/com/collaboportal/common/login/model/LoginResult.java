package com.collaboportal.common.login.model;

public record LoginResult(
        boolean success, 
        String code, 
        String message, 
        String userId, 
        String username,
        String token) { 

    public static LoginResult ok(
            String id, String name) {
        return new LoginResult(true, "200", "Login success",
                id, name, null);
    }

    public static LoginResult okWithToken(
            String id, String name, String token) {
        return new LoginResult(true, "200", "Login success",
                id, name, token);
    }

    public static LoginResult fail(String code, String msg) {
        return new LoginResult(false, code, msg,
                null, null, null);
    }
}
