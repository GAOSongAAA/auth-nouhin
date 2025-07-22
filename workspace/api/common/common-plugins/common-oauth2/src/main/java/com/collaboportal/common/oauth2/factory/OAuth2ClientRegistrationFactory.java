package com.collaboportal.common.oauth2.factory;

import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ファクトリーパターン - OAuth2クライアント登録ファクトリー
 * 責務：複数のOAuth2ClientRegistrationインスタンスの作成と管理
 * 利点：クライアント設定の集中管理、新しいプロバイダーの拡張が容易
 */
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2ClientRegistrationFactory {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientRegistrationFactory.class);

    // 全プロバイダー設定を格納
    private Map<String, Map<String, Object>> providers = new HashMap<>();

    // 作成済みクライアント登録をキャッシュ
    private final Map<String, OAuth2ClientRegistration> clientRegistrations = new ConcurrentHashMap<>();

    // パスパターンからプロバイダーIDへのマッピング
    private final Map<String, String> pathToProviderMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("OAuth2クライアント登録ファクトリー初期化開始、プロバイダー数: {}", providers.size());

        for (Map.Entry<String, Map<String, Object>> entry : providers.entrySet()) {
            String providerId = entry.getKey();
            Map<String, Object> config = entry.getValue();

            try {
                OAuth2ClientRegistration registration = createClientRegistration(providerId, config);
                clientRegistrations.put(providerId, registration);

                for (String pattern : toList(config.get("path-patterns"), "path-patterns")) {
                    pathToProviderMap.put(pattern, providerId);
                }

                logger.info("OAuth2クライアント登録の作成に成功: {} - {}", providerId, registration.getDisplayName());
            } catch (Exception e) {
                logger.error("OAuth2クライアント登録の作成に失敗: {}", providerId, e);
            }
        }

        logger.info("OAuth2クライアント登録ファクトリー初期化完了、登録成功プロバイダー: {}", clientRegistrations.keySet());
    }

    /**
     * OAuth2クライアント登録を作成
     */
    private OAuth2ClientRegistration createClientRegistration(String providerId, Map<String, Object> config) {
        return OAuth2ClientRegistration.builder()
        .providerId(providerId)
        .clientId((String) config.get("client-id"))
        .clientSecret((String) config.get("client-secret"))
        .issuer((String) config.get("issuer"))
        .audience((String) config.get("audience"))
        .scope(toList(config.get("scope"), "scope")) // 複数フォーマット対応
        .grantType((String) config.getOrDefault("grant-type", "authorization_code"))
        .redirectUri((String) config.getOrDefault("redirect-uri", "{baseUrl}/login/oauth2/code/" + providerId))
        .pathPatterns(toList(config.get("path-patterns"), "path-patterns"))
        .userNameAttribute((String) config.getOrDefault("user-name-attribute", "email"))
        .displayName((String) config.getOrDefault("display-name", providerId))
        .build();

    }

    /**
     * プロバイダーIDに基づいてクライアント登録を取得
     */
    public OAuth2ClientRegistration getClientRegistration(String providerId) {
        return clientRegistrations.get(providerId);
    }

    /**
     * 全クライアント登録を取得
     */
    public Map<String, OAuth2ClientRegistration> getAllClientRegistrations() {
        return Collections.unmodifiableMap(clientRegistrations);
    }

    /**
     * リクエストパスに基づいてプロバイダーをマッチング
     */
    public String findProviderByPath(String requestPath) {
        for (Map.Entry<String, String> entry : pathToProviderMap.entrySet()) {
            String pattern = entry.getKey();
            String providerId = entry.getValue();

            // シンプルなワイルドカードマッチング
            if (isPathMatch(requestPath, pattern)) {
                logger.debug("パス {} がプロバイダーにマッチしました: {}", requestPath, providerId);
                return providerId;
            }
        }

        logger.debug("パス {} がどのプロバイダーにもマッチしませんでした", requestPath);
        return null;
    }

    /**
     * パスマッチングロジック
     */
    private boolean isPathMatch(String requestPath, String pattern) {
        AntPathMatcher matcher = new AntPathMatcher();

        return matcher.match(pattern, requestPath);
    }

    // Spring Boot設定プロパティ注入
    public void setProviders(Map<String, Map<String, Object>> providers) {
        this.providers = providers;
    }

    public Map<String, Map<String, Object>> getProviders() {
        return providers;
    }

    /** String / List / Map → List<String> への対応 */
    private List<String> toList(Object raw, String field) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof String str) {                 // "a b c" | "a,b,c"
            return Arrays.asList(str.split("[,\\s]+"));
        }
        if (raw instanceof List<?> list) {               // YAML リスト
            return list.stream().map(Object::toString).toList();
        }
        if (raw instanceof Map<?, ?> map) {              // YAML マップ → 値を取得
            return map.values().stream().map(Object::toString).toList();
        }
        throw new IllegalArgumentException(
                "サポートされていないフィールドタイプ '" + field + "': " + raw.getClass());
    }

}