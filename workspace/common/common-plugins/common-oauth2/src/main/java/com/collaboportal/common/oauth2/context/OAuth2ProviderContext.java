package com.collaboportal.common.oauth2.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuth2ProviderContext {

    // ログインメールアドレス
    private String email;
    // 認証コード
    private String code;
    // ステート
    private String state;
    // リクエスト
    private HttpServletRequest request;
    // レスポンス
    private HttpServletResponse response;
    // 選択されたプロバイダーID
    private String selectedProviderId;
    // リクエストパス
    private String requestPath;
    // リクエストホスト
    private String requestHost;
    // リダイレクトURI
    private String redirectUri;
    // 認証プロバイダーURL
    private String authProviderUrl;
    // 発行者
    private String issuer;
    // クライアントID
    private String clientId;
    // クライアントシークレット
    private String clientSecret;

    // ホームページ
    private String homePage;
    // オーディエンス
    private String audience;
    // スコープ
    private String scope;
    // 使用するストラテジーのキー
    private String strategyKey;
    // トークン
    private String token;

}