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

    private long JWT_TOKEN_VALIDATION;
    private String secretKey;

    private final static String delimiter = "\\.";
    static Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @jakarta.annotation.PostConstruct
    public void init() {
        com.collaboportal.common.config.CommonConfig config = ConfigManager.getConfig();
        this.JWT_TOKEN_VALIDATION = config.getCookieExpiration() * 1000L;
        this.secretKey = config.getSecretKey();
    }

    private SecretKeySpec getKey() {
        byte[] secretKeyBytes = Base64.decodeBase64(this.secretKey);
        return new SecretKeySpec(secretKeyBytes, "HmacSHA256");
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    public Boolean isTokenExpired(String token) throws ExpiredJwtException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public static Map<String, String> getItemsJwtToken(String token) {
        Map<String, String> items = new HashMap<>();
        try {
            JSONObject playload = new JSONObject(
                    new String(Base64.decodeBase64URLSafe(token.split(delimiter)[1]), "UTF-8"));
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

    public String updateExpiresAuthToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Jwts.builder().setClaims(claims).signWith(getKey())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDATION)).compact();
    }

    public String generateTokenFromObject(Object object) {
        if (object == null) {
            return null;
        }

        Map<String, Object> claims = new HashMap<>();

        try {
            Class<?> clazz = object.getClass();
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value != null) {
                    claims.put(field.getName(), value);
                }
            }

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDATION))
                    .signWith(getKey())
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
