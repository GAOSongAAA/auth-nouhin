package com.collaboportal.common.jwt.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.entity.JwtObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT令牌相關的工具類
 * 支援令牌生成、解析、驗證等功能
 */
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private long JWT_TOKEN_VALIDATION;
    private String secretKey;

    static Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @jakarta.annotation.PostConstruct
    public void init() {
        com.collaboportal.common.config.CommonConfig config = ConfigManager.getConfig();
        this.JWT_TOKEN_VALIDATION = config.getCookieExpiration() * 1000L;
        this.secretKey = config.getSecretKey();
    }

    /**
     * 獲取簽名密鑰
     * @return SecretKeySpec 密鑰規格
     */
    private SecretKeySpec getKey() {
        byte[] secretKeyBytes = Base64.decodeBase64(this.secretKey);
        return new SecretKeySpec(secretKeyBytes, "HmacSHA256");
    }

    /**
     * 從令牌中提取用戶名
     * @param token JWT令牌
     * @return 用戶名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 從令牌中提取過期時間
     * @param token JWT令牌
     * @return 過期時間
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 從令牌中提取特定聲明
     * @param token JWT令牌
     * @param claimsResolver 聲明解析器
     * @return 聲明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 從令牌中獲取所有聲明
     * @param token JWT令牌
     * @return 所有聲明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("解析JWT令牌失敗", e);
            throw e;
        }
    }

    /**
     * 檢查令牌是否已過期
     * @param token JWT令牌
     * @return 是否已過期
     * @throws ExpiredJwtException 令牌過期異常
     */
    public Boolean isTokenExpired(String token) throws ExpiredJwtException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 從JWT令牌中提取所有項目
     * @param token JWT令牌
     * @return 包含所有聲明的Map
     */
    public static Map<String, String> getItemsJwtToken(String token) {
        Map<String, String> items = new HashMap<>();
        try {
            JwtTokenUtil util = new JwtTokenUtil();
            util.init(); // 確保初始化
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(util.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                    
            for (String key : claims.keySet()) {
                Object value = claims.get(key);
                if (value != null) {
                    items.put(key, value.toString());
                }
            }
            logger.debug("從JWT令牌中提取的所有項目：{}", items);
            return items;
        } catch (Exception ex) {
            logger.error("JWT轉換錯誤", ex);
            return items;
        }
    }

    /**
     * 從HTTP請求中提取JWT對象
     * @param request HTTP請求
     * @return JWT對象
     */
    public static JwtObject getItemsFromRequest(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Authorization標頭格式無效");
                return null;
            }
            
            String token = authHeader.substring(7);
            Map<String, String> claims = getItemsJwtToken(token);
            
            JwtObject jwtObject = new JwtObject();
            jwtObject.setEmail(claims.get("sub"));
            jwtObject.setHonbuFlg(claims.get("honbuFlg"));
            return jwtObject;
        } catch (Exception e) {
            logger.error("從請求中提取JWT信息失敗", e);
            return null;
        }
    }

    /**
     * 更新令牌的過期時間
     * @param token 原始令牌
     * @return 新的令牌
     */
    public String updateExpiresAuthToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDATION))
                    .signWith(getKey())
                    .compact();
        } catch (Exception e) {
            logger.error("更新令牌過期時間失敗", e);
            throw e;
        }
    }

    /**
     * 從對象生成JWT令牌
     * @param object 要轉換為令牌的對象
     * @return JWT令牌字符串
     */
    public String generateTokenFromObject(Object object) {
        if (object == null) {
            logger.warn("無法從null對象生成令牌");
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

            Date now = new Date();
            Date expiration = new Date(now.getTime() + JWT_TOKEN_VALIDATION);

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(getKey())
                    .compact();

        } catch (IllegalAccessException e) {
            logger.error("訪問對象字段失敗", e);
            return null;
        } catch (Exception e) {
            logger.error("生成JWT令牌失敗", e);
            return null;
        }
    }

    /**
     * 驗證令牌的有效性
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            
            // 解析令牌並檢查是否過期
            Claims claims = getAllClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            logger.debug("令牌已過期");
            return false;
        } catch (Exception e) {
            logger.error("令牌驗證失敗", e);
            return false;
        }
    }

    /**
     * 生成簡單的JWT令牌
     * @param subject 主題（通常是用戶標識）
     * @param additionalClaims 額外的聲明
     * @return JWT令牌
     */
    public String generateToken(String subject, Map<String, Object> additionalClaims) {
        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + JWT_TOKEN_VALIDATION);

            var builder = Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiration);

            if (additionalClaims != null && !additionalClaims.isEmpty()) {
                builder.addClaims(additionalClaims);
            }

            return builder.signWith(getKey()).compact();
        } catch (Exception e) {
            logger.error("生成JWT令牌失敗", e);
            return null;
        }
    }
}