package com.collaboportal.common.jwt.service.Impl;

import com.collaboportal.common.jwt.strategy.JwtTokenGenerator;
import com.collaboportal.common.jwt.strategy.JwtTokenValidator;
import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.entity.UserMaster;
import com.collaboportal.common.jwt.registry.JwtStrategyRegistry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * カスタムJWTストラテジー実装クラス
 * JWT関連の各種ストラテジー（Resolver、Generator、Validator）を定義し、
 * JwtStrategyRegistryに登録する
 */
@Component
public class CustomJwtStrategies implements CommandLineRunner {

        private final JwtStrategyRegistry registry;

        /**
         * コンストラクタ
         * 
         * @param registry JWTストラテジーレジストリ
         */
        public CustomJwtStrategies(JwtStrategyRegistry registry) {
                this.registry = registry;
        }

        /**
         * 統一された署名キーの取得
         * 
         * @return HMAC署名用の秘密鍵
         */
        private SecretKey secretKey() {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(ConfigManager.getConfig().getSecretKey()));
        }

        @Override
        public void run(String... args) {

                /* --------------- ① Claimリゾルバー（Resolver） ---------------- */
                // ユーザーIDの抽出
                registry.registerClaimResolver("uid", claims -> claims.get("uid", Long.class));
                // メールアドレスの抽出
                registry.registerClaimResolver("email", Claims::getSubject);
                // ロールの抽出
                registry.registerClaimResolver("role", claims -> claims.get("role", String.class));

                registry.registerClaimResolver("all", claims -> new LinkedHashMap<>(claims));

                /* --------------- ② トークンジェネレーター（Generator） --------------- */

                // 2-2 Map → StateToken（コールバック後の状態用）
                JwtTokenGenerator<Map<String, Object>> mapGenerator = map -> Jwts.builder()
                                .addClaims(map)
                                .setIssuedAt(new Date())
                                .claim("token_type", JwtConstants.TOKEN_TYPE_STATE)
                                .setExpiration(new Date(System.currentTimeMillis()
                                                + ConfigManager.getConfig().getCookieExpiration() * 1000L))
                                .signWith(secretKey())
                                .compact();
                registry.registerTokenGenerator(JwtConstants.GENERATE_STATE_MAP, mapGenerator);

                // 2-3 User → RefreshToken（長期有効）AccessTokenの更新用、内部使用
                JwtTokenGenerator<UserMaster> oauth2Generator = user -> Jwts.builder()
                                .setSubject(user.getUserMail())
                                .setIssuer(ConfigManager.getConfig().getCollaboportalIssuer())
                                .claim("token_type", JwtConstants.TOKEN_TYPE_INTERNAL)
                                .setIssuedAt(new Date())
                                .setExpiration(new Date(System.currentTimeMillis()
                                                + ConfigManager.getConfig().getCookieExpiration() * 1000L))
                                .signWith(secretKey())
                                .compact();
                registry.registerTokenGenerator(JwtConstants.GENERATE_INTERNAL_TOKEN, oauth2Generator);

                // 2-4 User -> AccessToken（長期有効）データベース認証用、内部使用
                JwtTokenGenerator<UserMaster> databaseTokenGenerator = user -> Jwts.builder()
                                .setSubject(user.getUserMail())
                                .setIssuer("database")
                                .claim("token_type", JwtConstants.TOKEN_TYPE_INTERNAL)
                                .setIssuedAt(new Date())
                                .setExpiration(new Date(System.currentTimeMillis()
                                                + ConfigManager.getConfig().getCookieExpiration() * 1000L))
                                .signWith(secretKey())
                                .compact();
                registry.registerTokenGenerator(JwtConstants.GENERATE_DATABASE_TOKEN, databaseTokenGenerator);

                /* --------------- ③ トークンバリデーター（Validator） ---------------- */

                // 3-1 有効期限のみ検証
                registry.registerTokenValidator(JwtConstants.VALIDATE_TYPE_EXPIRED,
                                (token, claims) -> claims.getExpiration().after(new Date()));

                // 3-2 厳密検証：有効期限 + issuer + database以外
                JwtTokenValidator strictValidator = (token, claims) -> claims.getExpiration().after(new Date())
                                && ConfigManager.getConfig().getCollaboportalIssuer().equals(claims.getIssuer());
                registry.registerTokenValidator(JwtConstants.VALIDATE_TYPE_ISSUER_OAUTH2, strictValidator);

                // 3-3 データベース認証用トークンの検証
                JwtTokenValidator databaseValidator = (token, claims) -> {
                        if (!claims.getExpiration().after(new Date())) {
                                return false; // 有効期限切れ
                        }
                        if (!"database".equals(claims.getIssuer())) {
                                return false; // issuerが一致しない
                        }
                        return true; // 検証成功
                };
                registry.registerTokenValidator(JwtConstants.VALIDATE_TYPE_DATABASE_DATABASE, databaseValidator);

                /* --------------- ② トークンジェネレーター（update） --------------- */
                // トークンの更新処理
                /**
                 * 既存トークンを受け取り、期限だけ延長して返す。
                 * - exp, iat を更新
                 * - その他の Claim はそのままコピー
                 * 失効／署名エラー時は RuntimeException を投げる想定。
                 */
                JwtTokenGenerator<String> refreshGenerator = oldToken -> {

                        // 1) 既存トークンをパース
                        Claims claims = Jwts.parserBuilder()
                                        .setSigningKey(secretKey())
                                        .build()
                                        .parseClaimsJws(oldToken)
                                        .getBody();

                        // 2) 新しい時間をセット
                        Date now = new Date();
                        Date exp = new Date(now.getTime()
                                        + ConfigManager.getConfig().getCookieExpiration() * 1000L);

                        // 3) exp / iat を上書きし再署名
                        return Jwts.builder()
                                        .setClaims(new LinkedHashMap<>(claims)) // 深コピーして安心
                                        .setIssuedAt(now)
                                        .setExpiration(exp)
                                        .signWith(secretKey())
                                        .compact();
                };

                // レジストリに登録（定数名は好きに決めてください）
                registry.registerTokenGenerator(
                                JwtConstants.GENERATE_REFRESH_FROM_OLD, refreshGenerator);
        }
}
