package com.collaboportal.common.oauth2.context;

import java.io.Serializable;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.model.BaseRequest;
import com.collaboportal.common.context.model.BaseResponse;


public class OAuth2ProviderContext implements Serializable{

    
    // リクエストパス
    private String requestPath;

    // ログインメールアドレス
    private String email;
    // 認証コード
    private String code;
    // ステート
    private String state;
    // リクエスト
    BaseRequest request = CommonHolder.getRequest();
    // レスポンス
    BaseResponse response = CommonHolder.getResponse();
    // 選択されたプロバイダーID
    private String selectedProviderId;
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

    // 無引數建構子
    public OAuth2ProviderContext() {
    }

    // 全引數建構子
    public OAuth2ProviderContext(String requestPath, String email, String code, String state, 
                                BaseRequest request, BaseResponse response, String selectedProviderId, 
                                String redirectUri, String authProviderUrl, String issuer, 
                                String clientId, String clientSecret, String homePage, 
                                String audience, String scope, String strategyKey, String token) {
        this.requestPath = requestPath;
        this.email = email;
        this.code = code;
        this.state = state;
        this.request = request;
        this.response = response;
        this.selectedProviderId = selectedProviderId;
        this.redirectUri = redirectUri;
        this.authProviderUrl = authProviderUrl;
        this.issuer = issuer;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.homePage = homePage;
        this.audience = audience;
        this.scope = scope;
        this.strategyKey = strategyKey;
        this.token = token;
    }

    // Getter 方法
    public String getRequestPath() {
        return requestPath;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public BaseResponse getResponse() {
        return response;
    }

    public String getSelectedProviderId() {
        return selectedProviderId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAuthProviderUrl() {
        return authProviderUrl;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getHomePage() {
        return homePage;
    }

    public String getAudience() {
        return audience;
    }

    public String getScope() {
        return scope;
    }

    public String getStrategyKey() {
        return strategyKey;
    }

    public String getToken() {
        return token;
    }

    // 鏈式 Setter 方法
    public OAuth2ProviderContext setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public OAuth2ProviderContext setEmail(String email) {
        this.email = email;
        return this;
    }

    public OAuth2ProviderContext setCode(String code) {
        this.code = code;
        return this;
    }

    public OAuth2ProviderContext setState(String state) {
        this.state = state;
        return this;
    }

    public OAuth2ProviderContext setRequest(BaseRequest request) {
        this.request = request;
        return this;
    }

    public OAuth2ProviderContext setResponse(BaseResponse response) {
        this.response = response;
        return this;
    }

    public OAuth2ProviderContext setSelectedProviderId(String selectedProviderId) {
        this.selectedProviderId = selectedProviderId;
        return this;
    }

    public OAuth2ProviderContext setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public OAuth2ProviderContext setAuthProviderUrl(String authProviderUrl) {
        this.authProviderUrl = authProviderUrl;
        return this;
    }

    public OAuth2ProviderContext setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public OAuth2ProviderContext setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public OAuth2ProviderContext setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public OAuth2ProviderContext setHomePage(String homePage) {
        this.homePage = homePage;
        return this;
    }

    public OAuth2ProviderContext setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public OAuth2ProviderContext setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public OAuth2ProviderContext setStrategyKey(String strategyKey) {
        this.strategyKey = strategyKey;
        return this;
    }

    public OAuth2ProviderContext setToken(String token) {
        this.token = token;
        return this;
    }

    // Builder 模式靜態方法
    public static OAuth2ProviderContextBuilder builder() {
        return new OAuth2ProviderContextBuilder();
    }

    // Builder 類別
    public static class OAuth2ProviderContextBuilder {
        private String requestPath;
        private String email;
        private String code;
        private String state;
        private BaseRequest request;
        private BaseResponse response;
        private String selectedProviderId;
        private String redirectUri;
        private String authProviderUrl;
        private String issuer;
        private String clientId;
        private String clientSecret;
        private String homePage;
        private String audience;
        private String scope;
        private String strategyKey;
        private String token;

        public OAuth2ProviderContextBuilder requestPath(String requestPath) {
            this.requestPath = requestPath;
            return this;
        }

        public OAuth2ProviderContextBuilder email(String email) {
            this.email = email;
            return this;
        }

        public OAuth2ProviderContextBuilder code(String code) {
            this.code = code;
            return this;
        }

        public OAuth2ProviderContextBuilder state(String state) {
            this.state = state;
            return this;
        }

        public OAuth2ProviderContextBuilder request(BaseRequest request) {
            this.request = request;
            return this;
        }

        public OAuth2ProviderContextBuilder response(BaseResponse response) {
            this.response = response;
            return this;
        }

        public OAuth2ProviderContextBuilder selectedProviderId(String selectedProviderId) {
            this.selectedProviderId = selectedProviderId;
            return this;
        }

        public OAuth2ProviderContextBuilder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public OAuth2ProviderContextBuilder authProviderUrl(String authProviderUrl) {
            this.authProviderUrl = authProviderUrl;
            return this;
        }

        public OAuth2ProviderContextBuilder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public OAuth2ProviderContextBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public OAuth2ProviderContextBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public OAuth2ProviderContextBuilder homePage(String homePage) {
            this.homePage = homePage;
            return this;
        }

        public OAuth2ProviderContextBuilder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public OAuth2ProviderContextBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public OAuth2ProviderContextBuilder strategyKey(String strategyKey) {
            this.strategyKey = strategyKey;
            return this;
        }

        public OAuth2ProviderContextBuilder token(String token) {
            this.token = token;
            return this;
        }

        public OAuth2ProviderContext build() {
            return new OAuth2ProviderContext(requestPath, email, code, state, request, response, 
                                           selectedProviderId, redirectUri, authProviderUrl, issuer, 
                                           clientId, clientSecret, homePage, audience, scope, 
                                           strategyKey, token);
        }
    }

    // equals 方法
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        OAuth2ProviderContext that = (OAuth2ProviderContext) obj;
        
        if (requestPath != null ? !requestPath.equals(that.requestPath) : that.requestPath != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (request != null ? !request.equals(that.request) : that.request != null) return false;
        if (response != null ? !response.equals(that.response) : that.response != null) return false;
        if (selectedProviderId != null ? !selectedProviderId.equals(that.selectedProviderId) : that.selectedProviderId != null) return false;
        if (redirectUri != null ? !redirectUri.equals(that.redirectUri) : that.redirectUri != null) return false;
        if (authProviderUrl != null ? !authProviderUrl.equals(that.authProviderUrl) : that.authProviderUrl != null) return false;
        if (issuer != null ? !issuer.equals(that.issuer) : that.issuer != null) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null) return false;
        if (homePage != null ? !homePage.equals(that.homePage) : that.homePage != null) return false;
        if (audience != null ? !audience.equals(that.audience) : that.audience != null) return false;
        if (scope != null ? !scope.equals(that.scope) : that.scope != null) return false;
        if (strategyKey != null ? !strategyKey.equals(that.strategyKey) : that.strategyKey != null) return false;
        return token != null ? token.equals(that.token) : that.token == null;
    }

    // hashCode 方法
    @Override
    public int hashCode() {
        int result = requestPath != null ? requestPath.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        result = 31 * result + (response != null ? response.hashCode() : 0);
        result = 31 * result + (selectedProviderId != null ? selectedProviderId.hashCode() : 0);
        result = 31 * result + (redirectUri != null ? redirectUri.hashCode() : 0);
        result = 31 * result + (authProviderUrl != null ? authProviderUrl.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
        result = 31 * result + (homePage != null ? homePage.hashCode() : 0);
        result = 31 * result + (audience != null ? audience.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (strategyKey != null ? strategyKey.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    // toString 方法
    @Override
    public String toString() {
        return "OAuth2ProviderContext{" +
                "requestPath='" + requestPath + '\'' +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", state='" + state + '\'' +
                ", request=" + request +
                ", response=" + response +
                ", selectedProviderId='" + selectedProviderId + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", authProviderUrl='" + authProviderUrl + '\'' +
                ", issuer='" + issuer + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", homePage='" + homePage + '\'' +
                ", audience='" + audience + '\'' +
                ", scope='" + scope + '\'' +
                ", strategyKey='" + strategyKey + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

}