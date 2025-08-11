package com.collaboportal.common.jwt.service;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.strategy.JwtClaimResolver;
import com.collaboportal.common.jwt.strategy.JwtTokenGenerator;
import com.collaboportal.common.jwt.strategy.JwtTokenValidator;
import com.collaboportal.common.jwt.registry.JwtStrategyRegistry;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

/**
 * JWTサービスクラス
 * JWTトークンの生成、検証、クレーム抽出などの機能を提供する
 */
@Service
public class JwtService {

    /** JWTストラテジーレジストリ */
    private final JwtStrategyRegistry registry;

    /**
     * コンストラクタ
     * @param registry JWTストラテジーレジストリ
     */
    public JwtService(JwtStrategyRegistry registry) {
        this.registry = registry;
    }

    /**
     * 秘密鍵を取得する
     * @return HMAC署名用の秘密鍵
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(ConfigManager.getConfig().getSecretKey()));
    }

    /**
     * JWTトークンからすべてのクレームを取得する
     * @param token JWTトークン
     * @return クレーム情報
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 指定されたリゾルバーキーを使用してJWTトークンからクレームを抽出する
     * @param token JWTトークン
     * @param resolverKey クレームリゾルバーのキー
     * @param <T> 戻り値の型
     * @return 抽出されたクレーム値
     */
    public <T> T extractClaim(String token, String resolverKey) {
        JwtClaimResolver<T> resolver = registry.getClaimResolver(resolverKey);
        return resolver.resolve(getAllClaims(token));
    }

    /**
     * 指定されたジェネレーターキーを使用してJWTトークンを生成する
     * @param source トークン生成のソースデータ
     * @param generatorKey トークンジェネレーターのキー
     * @param <T> ソースデータの型
     * @return 生成されたJWTトークン
     */
    public <T> String generateToken(T source, String generatorKey) {
        JwtTokenGenerator<T> generator = registry.getTokenGenerator(generatorKey);
        return generator.generate(source);
    }

    /**
     * 指定されたバリデーターキーを使用してJWTトークンを検証する
     * @param token JWTトークン
     * @param validatorKey トークンバリデーターのキー
     * @return 検証結果（true: 有効、false: 無効）
     */
    public boolean validateToken(String token, String validatorKey) {
        JwtTokenValidator validator = registry.getTokenValidator(validatorKey);
        return validator.validate(token, getAllClaims(token));
    }

}
