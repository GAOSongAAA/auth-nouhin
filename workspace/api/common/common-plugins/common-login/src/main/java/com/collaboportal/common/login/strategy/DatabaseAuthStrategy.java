package com.collaboportal.common.login.strategy;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.constants.JwtConstants;

import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.login.model.DTO.UserMasterEPL;
import com.collaboportal.common.login.service.LoginUserMasterService;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.utils.Message;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * データベース認証戦略実装クラス
 * 
 * このクラスはAuthenticationStrategyインターフェースを実装し、データベースベースのユーザー認証フローを処理する責任を持ちます。
 * 主な機能は以下の通りです：
 * 1. 既存のJWT認証トークンの確認と検証
 * 2. 期限切れ間近の有効なトークンの更新
 * 3. 期限切れまたは無効なトークンのログインページへのリダイレクト処理
 * 4. ユーザー情報の保存と管理
 * 
 * 認証フロー：
 * - まずCookieに認証トークンが存在するかを確認
 * - トークンが存在し有効であれば、トークンを更新して続行
 * - トークンが存在しない、期限切れ、または無効な場合はログインページにリダイレクト
 */
@Component("databaseAuthStrategy")
public class DatabaseAuthStrategy implements AuthorizationStrategy {

    /**
     * ログレコーダー、認証プロセス中の各種状態とエラー情報を記録するために使用
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthStrategy.class);

    /**
     * JWTトークンユーティリティクラス、トークンの生成、検証、操作に使用
     */
    private final JwtService jwtService;

    private final LoginUserMasterService loginUserMasterService;

    /**
     * コンストラクタ
     * 依存性注入によってJWTトークンユーティリティクラスインスタンスを受け取る
     * 
     * @param jwtTokenUtil JWTトークンユーティリティクラス、トークン関連操作の処理に使用
     */
    public DatabaseAuthStrategy(JwtService jwtService, LoginUserMasterService loginUserMasterService) {
        this.jwtService = jwtService;
        this.loginUserMasterService = loginUserMasterService;
        logger.info("DatabaseAuthStrategyの初期化が完了しました");
    }

    /**
     * データベース認証ロジックを実行
     * 
     * このメソッドは認証戦略の核心実装であり、以下の手順で実行されます：
     * 1. リクエストのCookieから認証トークンを抽出
     * 2. トークンが存在するかを確認
     * 3. トークンが存在する場合、有効性と期限切れ状態を検証
     * 4. 有効なトークンに対して、ユーザー情報を抽出しトークンを更新
     * 5. 無効または存在しないトークンに対して、ログインページにリダイレクト
     * 
     * @param request  ベースリクエストオブジェクト、HTTPリクエストの関連情報を含む
     * @param response ベースレスポンスオブジェクト、HTTPレスポンスの関連情報を設定するために使用
     * @throws AuthenticationException 認証プロセス中にエラーが発生した場合に投げられる
     */
    @Override
    public void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException {
        logger.debug("データベース認証戦略の実行を開始します...");

        // Cookieから認証トークンを取得
        // 事前定義されたCookie名を使用して認証トークンを検索
        String token = request.getCookieValue(Message.Cookie.AUTH);

        // トークンが存在するかを確認
        // トークンが存在しないか空文字列の場合、ユーザーがまだログインしていないか、ログイン状態が失効していることを表す
        if (token == null || token.isEmpty()) {
            response.redirect("/login.html");
            return;
        }

        try {
            // トークンが期限切れかを検証
            // JWTトークンは期限切れ時間情報を含み、ここで現在時間がトークンの有効期限を超えているかを確認
            if (!jwtService.validateToken(token, JwtConstants.VALIDATE_TYPE_EXPIRED)) {
                response.redirect("/login.html");
                return;
            }

            // JWTトークンからユーザー情報を抽出
            // JWTトークンのpayloadにはユーザーID、ロールなどのユーザー関連情報が含まれている
            String email = jwtService.extractClaim(token, JwtConstants.RESOLVER_TYPE_EMAIL);

            // ユーザー情報が空かを確認
            // トークンが有効でも、ユーザー情報が含まれていない場合は認証失敗と見なす
            if ("".equals(email) || email == null) {
                response.redirect("/login.html");
                return;
            }
            UserMasterEPL userInfo = loginUserMasterService.loadByEmail(email);
            // トークンの期限切れ時間を更新
            // これはユーザーのログイン状態を維持し、頻繁な再ログインを避けるのに役立つ
            String refreshedToken = jwtService.generateToken(email, JwtConstants.GENERATE_DATABASE_TOKEN);

            // 更新されたトークンをCookieに設定
            // パラメータ説明：トークン値、パス、ドメイン、最大生存時間
            CookieUtil.setSameSiteCookie(response, Message.Cookie.AUTH, refreshedToken);
            logger.debug("認証トークンが正常に更新され、レスポンスCookieに設定されました。");

            // 認証成功、成功メッセージを記録
            logger.info("ユーザーデータベース認証成功：{}。", userInfo.getUserMail());

        } catch (ExpiredJwtException e) {
            // JWT期限切れ例外をキャッチ
            // これは正常なビジネスフローであり、トークンが期限切れの場合は再ログインが必要
            logger.info("認証トークンが期限切れです（例外キャッチ）。ログインページにリダイレクトします。");
            response.redirect("/login.html");
            return;
        } catch (Exception e) {
            // その他の可能な例外をキャッチ（トークン形式エラー、署名無効など）
            // エラーを記録してログインページにリダイレクト
            logger.error("データベーストークン検証プロセス中にエラーが発生しました。ログインページにリダイレクトします。", e);
            response.redirect("/login.html");
            return;
        }
    }
}
