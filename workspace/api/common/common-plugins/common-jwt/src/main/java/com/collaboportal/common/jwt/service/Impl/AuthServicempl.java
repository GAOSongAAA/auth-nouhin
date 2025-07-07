package com.collaboportal.common.jwt.service.Impl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.model.OauthTokenResponseBody;
import com.collaboportal.common.jwt.model.OauthTokenResult;
import com.collaboportal.common.jwt.repository.AuthMapper;
import com.collaboportal.common.jwt.service.APIClientService;
import com.collaboportal.common.jwt.service.AuthService;
import com.collaboportal.common.jwt.utils.APIClient;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.HttpServletResponse;
import retrofit2.Response;

/**
 * 認証サービスの実装クラス
 */
@Service
public class AuthServicempl implements AuthService {

    // ロガー
    Logger logger = LoggerFactory.getLogger(AuthServicempl.class);

    // ベースURL
    private String baseUrl = ConfigManager.getConfig().getCollaboidBaseurl();

    // WebクライアントID
    private String clientIdWeb = ConfigManager.getConfig().getCollaboportalClientIdWeb();

    // Webクライアントシークレット
    private String clientSecretWeb = ConfigManager.getConfig().getCollaboportalClientSecretWeb();

    // Cookieの有効期限
    private int COOKIE_EXPIRATION = ConfigManager.getConfig().getCookieExpiration();

    // JWTトークンUtil
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * コンストラクタ
     * 
     * @param jwtTokenUtil JWTトークンUtil
     * @param authMapper   認証マッパー
     */
    public AuthServicempl(JwtTokenUtil jwtTokenUtil,
            AuthMapper authMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        logger.debug(clientIdWeb);
        logger.debug(clientSecretWeb);
    }

    /**
     * Colaboportal APIからOAuthトークンを取得する
     * 
     * @param grant_type    グラントタイプ
     * @param code          認証コード
     * @param redirect_uri  リダイレクトURI
     * @param audience      オーディエンス
     * @param client_id     クライアントID
     * @param client_secret クライアントシークレット
     * @param response      HTTPレスポンス
     * @return OAuthトークン結果
     */
    @Override
    public OauthTokenResult getOauthTokenFromColaboportalApi(String grant_type, String code, String redirect_uri,
            String audience, String client_id, String client_secret, HttpServletResponse response) {

        // Serviceのインスタンスを取得
        APIClientService client = new APIClient(baseUrl).getClient();

        try {
            // 外部API呼出をログに出力
            logger.debug("コラボトークン取得API呼出開始");
            logger.info(baseUrl);
            logger.debug("コラボトークン取得API URL：" + client
                    .getOauthToken(grant_type, code, redirect_uri, audience, client_id, client_secret).request().url());
            Response<OauthTokenResponseBody> apiResponse = client
                    .getOauthToken(grant_type, code, redirect_uri, audience, client_id, client_secret).execute();

            // ステータスコード、レスポンスボディの取得
            int statusCode = apiResponse.code();

            // 外部API呼出のステータスコードをログに出力
            logger.debug("コラボトークン取得API呼出結果のステータスコード: {} @response {}", statusCode, apiResponse.toString());

            // ステータスがOKの場合
            if (statusCode == HttpStatus.OK.value()) {
                OauthTokenResponseBody responseBody = apiResponse.body();

                // レスポンスボディからアクセストークン、Idトークン、リフレッシュトークンを取得
                String accessToken = responseBody.getAccess_token();
                String idToken = responseBody.getId_token();
                String refreshToken = responseBody.getRefresh_token();

                logger.debug("アクセストークン：{}", accessToken);
                logger.debug("IDトークン：{}", idToken);
                logger.debug("リフレッシュトークン：{}", refreshToken);

                // リフレッシュトークンを設定
                response.addHeader("Set-Cookie", Message.Cookie.CLB_REFRESH_TOKEN + "=" + refreshToken
                        + ";path=/;MAX-AGE=" + COOKIE_EXPIRATION + ";SameSite=strict; ");

                // Idトークンから項目の取り出し
                Map<String, String> items = jwtTokenUtil.getItemsFromIdToken(idToken);
                OauthTokenResult result = new OauthTokenResult(accessToken, true, items.get("name"),
                        items.get("sub"), items.get("email"), items.get("given_name"), items.get("family_name"));
                return result;
            }

            // それ以外の場合
            else {
                logger.error(
                        "コラボトークン取得APIステータスエラー。statusCode：{}、responseBody：{}、grant_type：{}、code：{}、redirect_uri：{}、audience：{}、client_id：{}、client_secret：{}",
                        statusCode, apiResponse.body(), grant_type, code, redirect_uri, audience, client_id,
                        client_secret);
                OauthTokenResult result = new OauthTokenResult(null, false, null, null, null, null, null);
                return result;
            }
        }
        // timeoutの場合ここで処理がされる
        catch (IOException ex) {
            logger.error(
                    "コラボAPI呼び出しエラー: grant_type:{}, code:{}, redirect_uri:{}, audience:{}, client_id:{}, client_secret:{}",
                    grant_type, code, redirect_uri, audience, client_id, client_secret, ex);
            OauthTokenResult result = new OauthTokenResult(null, false, null, null, null, null, null);
            return result;
        } catch (Exception ex) {
            logger.error(
                    "コラボAPI呼び出しで予期せぬエラー: grant_type:{}, code:{}, redirect_uri:{}, audience:{}, client_id:{}, client_secret:{}",
                    grant_type, code, redirect_uri, audience, client_id, client_secret, ex);
            OauthTokenResult result = new OauthTokenResult(null, false, null, null, null, null, null);
            return result;
        }
    }

    /**
     * トークンリフレッシュ
     * 
     * @param client_id     クライアントID
     * @param client_secret クライアントシークレット
     * @param refreshToken  リフレッシュトークン
     * @param response      HTTPレスポンス
     * @return アクセストークン情報
     */
    @Override
    public OauthTokenResult getOauthTokenByRefreshToken(String client_id, String client_secret, String refreshToken,
            HttpServletResponse response) {

        // サービスのインスタンスを取得
        APIClientService client = new APIClient(baseUrl).getClient();

        try {
            // 外部API呼出をログに出力
            logger.debug("トークンリフレッシュ開始");
            logger.debug("トークンリフレッシュAPI URL："
                    + client.getOauthTokenByRefreshToken("refresh_token", client_id, client_secret, refreshToken)
                            .request().url());
            Response<OauthTokenResponseBody> apiResponse = client
                    .getOauthTokenByRefreshToken("refresh_token", client_id, client_secret, refreshToken).execute();

            // ステータスコード、レスポンスボディの取得
            int statusCode = apiResponse.code();

            // 外部API呼出のステータスコードをログに出力
            logger.debug("トークンリフレッシュAPI呼出結果のステータスコード: {} @response {}", statusCode, apiResponse.toString());

            // ステータスがOKの場合
            if (statusCode == HttpStatus.OK.value()) {
                OauthTokenResponseBody responseBody = apiResponse.body();

                // レスポンスボディからアクセストークン、Idトークン、リフレッシュトークンを取得
                logger.debug("アクセストークン：{}", responseBody.getAccess_token());
                logger.debug("IDトークン：{}", responseBody.getId_token());
                logger.debug("リフレッシュトークン：{}", responseBody.getRefresh_token());

                // リフレッシュトークンを設定
                response.addHeader("Set-Cookie",
                        Message.Cookie.CLB_REFRESH_TOKEN + "=" + responseBody.getRefresh_token() + ";path=/;MAX-AGE="
                                + COOKIE_EXPIRATION + ";SameSite=strict; ");

                // Idトークンから項目の取り出し
                Map<String, String> items = jwtTokenUtil.getItemsFromIdToken(responseBody.getId_token());
                OauthTokenResult result = new OauthTokenResult(responseBody.getAccess_token(), true, items.get("name"),
                        items.get("sub"), items.get("email"), items.get("given_name"), items.get("family_name"));
                return result;
            }

            // それ以外の場合
            else {

                logger.error(
                        "トークンリフレッシュAPI呼び出しでステータスエラー。statusCode：{}、responseBody：{}、grant_type：{}、client_id：{}、client_secret：{}、refreshToken：{}",
                        statusCode, apiResponse.body(), "refresh_token", client_id, client_secret, refreshToken);
                OauthTokenResult result = new OauthTokenResult(null, false, null, null, null, null, null);
                return result;
            }
        }
        // timeoutの場合ここで処理がされる
        catch (IOException ex) {
            logger.error("トークンリフレッシュAPI呼び出しエラー: grant_type:{}, client_id:{}, client_secret:{}、refreshToken：{}",
                    "refresh_token", client_id, client_secret, refreshToken, ex);
            OauthTokenResult result = new OauthTokenResult(null, false, null, null, null, null, null);
            return result;
        } catch (Exception ex) {
            logger.error("トークンリフレッシュAPI呼び出しで予期せぬエラー: grant_type:{}, client_id:{}, client_secret:{}、refreshToken：{}",
                    "refresh_token", client_id, client_secret, refreshToken, ex);
            OauthTokenResult result = new OauthTokenResult(null, false, null, null, null, null, null);
            return result;
        }
    }

    @Override
    public String getClientId() {
        return clientIdWeb;
    }

    @Override
    public String getClientSecret() {
        return clientSecretWeb;
    }

}
