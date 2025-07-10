package com.collaboportal.common.jwt.utils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.entity.JwtObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JWTトークン関連のユーティリティクラス
 */
public class JwtTokenUtil implements Serializable {

    // JWTトークンの有効期間
    private static long JWT_TOKEN_VALIDATION = ConfigManager.getConfig().getCookieExpiration() * 1000;

    // JWTトークンのデリミタ
    private final static String delimiter = "\\.";

    // シークレットキー
    private static String secretKey = ConfigManager.getConfig().getSecretKey();

    /**
     * シークレットキーを取得
     * 
     * @return シークレットキー
     */
    private static SecretKeySpec getKey(String secretKey) {
        byte[] secretKeyBytes = Base64.decodeBase64(secretKey);
        return new SecretKeySpec(secretKeyBytes, "HmacSHA256");
    }

    // ロガー
    static Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    /**
     * トークンからユーザ名を取得
     * 
     * @param token JWTトークン
     * @return ユーザ名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * トークンから有効期限を取得
     * 
     * @param token JWTトークン
     * @return 有効期限
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * トークンからクレームを取得
     * 
     * @param token          JWTトークン
     * @param claimsResolver クレーム解決関数
     * @return クレームの値
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * トークンから全てのクレームを取得
     * 
     * @param token JWTトークン
     * @return 全てのクレーム
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getKey(secretKey)).build().parseSignedClaims(token).getPayload();
    }

    /**
     * トークンが期限切れかどうかを判定
     * 
     * @param token JWTトークン
     * @return 期限切れならtrue
     * @throws ExpiredJwtException
     */
    public Boolean isTokenExpired(String token) throws ExpiredJwtException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Accessトークンから項目を取得
     * 
     * @param token Accessトークン
     * @return 取得した項目のマップ
     */
    public static Map<String, String> getItemsJwtToken(String token) {
        Map<String, String> items = new HashMap<>();
        try {
            JSONObject playload = new JSONObject(
                    new String(Base64.decodeBase64URLSafe(token.split(delimiter)[1]), "UTF-8"));
            // イテレータを使用してペイロードのすべてのフィールドを取得
            for (String key : playload.keySet()) {
                Object value = playload.get(key);
                if (value != null) {
                    items.put(key, value.toString());
                }
            }
            logger.debug("getItemsFromIdTokenで取得した全項目：{}", items);
            return items;
        } catch (JSONException ex) {
            logger.error("JWT変換エラー");
            return items;
        } catch (UnsupportedEncodingException ex) {
            logger.error("JWTエンコードエラー");
            return items;
        }
    }

    public static JwtObject getItemsFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Map<String, String> claims = getItemsJwtToken(token);
        JwtObject jwtObject = new JwtObject();
        jwtObject.setEmail(claims.get("sub"));
        jwtObject.setHonbuFlg(claims.get("honbuFlg"));
        return jwtObject;
    }

    /**
     * 認証トークンの有効期限を更新
     * 
     * @param token JWTトークン
     * @return 更新されたトークン
     */
    public String updateExpiresAuthToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Jwts.builder().setClaims(claims).signWith(getKey(secretKey))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDATION * 1000)).compact();
    }

    // ===============================

    // ===============================

    /**
     * オブジェクトからJWTトークンを生成する（反射を使用）
     * 
     * @param object トークンに含めるオブジェクト
     * @return 生成されたJWTトークン
     */
    public static String generateTokenFromObject(Object object) {
        if (object == null) {
            return null;
        }

        Map<String, Object> claims = new HashMap<>();

        try {
            // 反射を使用してオブジェクトのフィールドを取得
            Class<?> clazz = object.getClass();
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value != null) {
                    claims.put(field.getName(), value);
                }
            }

            // JWTトークンを生成
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDATION))
                    .signWith(getKey(secretKey))
                    .compact();

        } catch (IllegalAccessException e) {
            logger.error("オブジェクトのフィールドアクセスに失敗しました", e);
            return null;
        } catch (Exception e) {
            logger.error("JWTトークンの生成に失敗しました", e);
            return null;
        }
    }

}
