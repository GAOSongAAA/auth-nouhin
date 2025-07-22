package com.collaboportal.common.context;

import java.io.Serializable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OAuth2 コールバック処理コンテキストクラス
 * OAuth2 認証コールバック処理中のデータ受け渡しに使用
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CallbackContext implements Serializable {

    // ユーザーメールアドレス（フォームパラメータから取得）
    private String emailFromForm;
    
    // OAuth2 認証コード
    private String code;
    
    // OAuth2 ステートパラメータ
    private String state;
    
    // 認証ステートトークン（Cookieから取得）
    private String authStateToken;
    
    // 移動URL（Cookieから取得）
    private String moveUrl;
    
    // HTTPリクエストオブジェクト
    private HttpServletRequest request;
    
    // HTTPレスポンスオブジェクト
    private HttpServletResponse response;
    
    // クライアントID（認証ステートトークンから解析）
    private String clientId;
    
    // クライアントシークレット（認証ステートトークンから解析）
    private String clientSecret;
    
    // オーディエンス（認証ステートトークンから解析）
    private String audience;
    
    // スコープ（認証ステートトークンから解析）
    private String scope;
    
    // トークン（認証ステートトークンから解析）
    private String token;
    
    // ストラテジーキー（test または prod）
    private String strategyKey;
    
    // リダイレクトURI
    private String redirectUri;
    
    // ホームページURL
    private String homePage;
    
    // 選択されたプロバイダーID
    private String selectedProviderId;
    
    // 認証プロバイダーURL
    private String authProviderUrl;
    
    // 発行者
    private String issuer;

} 