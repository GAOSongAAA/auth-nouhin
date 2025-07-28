package com.collaboportal.common.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ログインリクエストのデータ転送オブジェクト (DTO)
 * ユーザーがログインするために必要な認証情報をカプセル化します。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * ユーザーのメールアドレス
     * ログインの一意の識別子として使用されます。
     */
    private String username;

    /**
     * ユーザーのパスワード
     * ユーザーの身元を検証するために使用されます。
     */
    private String password;
}
