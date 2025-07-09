# OAuth2 自動配置使用說明

## 概述

本模組提供了完整的 OAuth2 自動配置功能，統一註冊和管理所有 OAuth2 相關的組件和依賴。通過 Spring Boot 的自動配置機制，開發者只需要引入依賴和配置相應的屬性，即可自動獲得完整的 OAuth2 功能。

## 自動配置類結構

### 1. 核心配置類
- **OAuth2CoreAutoConfiguration**: 註冊 OAuth2 核心組件
  - OAuth2ClientRegistrationFactory
  - AuthProcessor
  - APIClient
  - UserInfoServiceFactory
  - OAuth2ConfigurationProperties

### 2. 過濾器配置類
- **OAuth2FilterAutoConfiguration**: 註冊 JWT 相關過濾器
  - JwtValidationTemplate
  - JwtValidationChain
  - JwtAuthFilter

### 3. 策略配置類
- **OAuth2StrategyAutoConfiguration**: 註冊策略和模板類
  - LoginStrategyRegistry
  - JwtTokenStrategyRegistry
  - CallbackLoginTemplate

### 4. 控制器配置類
- **OAuth2ControllerAutoConfiguration**: 註冊控制器組件
  - AuthorizationController

### 5. JWT 配置類
- **JwtAutoConfiguration**: 註冊 JWT 相關組件
  - JwtTokenUtil

## 使用方法

### 1. 添加依賴

在您的項目 `pom.xml` 中添加以下依賴：

```xml
<dependency>
    <groupId>com.collaboportal</groupId>
    <artifactId>common-spring-boot-autoconfig</artifactId>
    <version>SNAPSHOT-1.0.0</version>
</dependency>
```

### 2. 配置屬性

在您的 `application.properties` 或 `application.yml` 中添加 OAuth2 配置：

#### 基本配置範例 (application.properties)
```properties
# OAuth2 提供者配置
oauth2.providers.test.client-id=your-test-client-id
oauth2.providers.test.client-secret=your-test-client-secret
oauth2.providers.test.issuer=https://your-auth-server.com
oauth2.providers.test.redirect-uri=http://localhost:8080/auth/callback
oauth2.providers.test.scope=openid profile email
oauth2.providers.test.audience=your-audience
oauth2.providers.test.path-patterns=/test/**,/auth/test/**

# JWT 配置
jwt.token.secret-key=your-secret-key
jwt.token.expiration=3600
jwt.token.issuer=your-app
jwt.token.audience=web

# 安全配置
oauth2.security.enabled=true
oauth2.filter.enabled=true
```

#### YAML 配置範例 (application.yml)
```yaml
oauth2:
  providers:
    test:
      client-id: your-test-client-id
      client-secret: your-test-client-secret
      issuer: https://your-auth-server.com
      redirect-uri: http://localhost:8080/auth/callback
      scope: openid profile email
      audience: your-audience
      path-patterns:
        - /test/**
        - /auth/test/**
    prod:
      client-id: your-prod-client-id
      client-secret: your-prod-client-secret
      issuer: https://your-prod-auth-server.com
      redirect-uri: https://your-domain.com/auth/callback
      scope: openid profile email
      audience: your-prod-audience
      path-patterns:
        - /prod/**
        - /auth/prod/**
  security:
    enabled: true
    allow-credentials: true
  filter:
    enabled: true
    order: 100

jwt:
  token:
    secret-key: your-secret-key
    expiration: 3600
    issuer: your-app
    audience: web
```

### 3. 環境變數配置

您也可以使用環境變數來配置：

```bash
# OAuth2 配置
export TEST_CLIENT_ID=your-test-client-id
export TEST_CLIENT_SECRET=your-test-client-secret
export PROD_CLIENT_ID=your-prod-client-id
export PROD_CLIENT_SECRET=your-prod-client-secret

# JWT 配置
export JWT_SECRET_KEY=your-secret-key
export JWT_EXPIRATION=3600
```

### 4. 自定義用戶信息服務

實現 `IUserInfoService` 介面並註冊到工廠：

```java
@Service
public class CustomUserInfoService implements IUserInfoService<CustomUserInfoDto> {
    
    @Override
    public CustomUserInfoDto loadByEmail(String email) {
        // 實現用戶查詢邏輯
        return userRepository.findByEmail(email);
    }
    
    @Override
    public CustomUserInfoDto loadById(String userId) {
        // 實現用戶查詢邏輯
        return userRepository.findById(userId);
    }
}

@PostConstruct
public void registerUserInfoService() {
    UserInfoServiceFactory.registerService("your-app-name", customUserInfoService);
}
```

### 5. 自定義登錄策略

實現自定義登錄策略：

```java
@Component
public class CustomLoginStrategy implements LoginStrategy {
    
    @Override
    public void login(OAuth2ProviderContext context) {
        // 實現自定義登錄邏輯
    }
}

@Autowired
private LoginStrategyRegistry loginStrategyRegistry;

@PostConstruct
public void registerLoginStrategy() {
    loginStrategyRegistry.register("custom", customLoginStrategy);
}
```

## 配置屬性詳解

### OAuth2 提供者配置
- `client-id`: OAuth2 客戶端 ID
- `client-secret`: OAuth2 客戶端密鑰
- `issuer`: OAuth2 授權服務器地址
- `redirect-uri`: 回調 URI
- `scope`: 授權範圍
- `audience`: 目標受眾
- `grant-type`: 授權類型（通常為 authorization_code）
- `user-name-attribute`: 用戶名屬性字段
- `display-name`: 顯示名稱
- `path-patterns`: 路徑匹配模式

### 安全配置
- `oauth2.security.enabled`: 是否啟用 OAuth2 安全
- `oauth2.security.allow-credentials`: 是否允許憑證
- `oauth2.security.max-age`: 最大存活時間

### 過濾器配置
- `oauth2.filter.enabled`: 是否啟用過濾器
- `oauth2.filter.order`: 過濾器順序
- `oauth2.filter.exclude-patterns`: 排除路徑模式

### 回調配置
- `oauth2.callback.success-redirect`: 成功重定向路徑
- `oauth2.callback.error-redirect`: 錯誤重定向路徑
- `oauth2.callback.timeout`: 回調超時時間

## 自動配置的 Bean

### 核心 Bean
- `OAuth2ClientRegistrationFactory`: OAuth2 客戶端註冊工廠
- `AuthProcessor`: 認證處理器
- `APIClient`: API 客戶端
- `UserInfoServiceFactory`: 用戶信息服務工廠

### 過濾器 Bean
- `JwtAuthFilter`: JWT 認證過濾器
- `JwtValidationTemplate`: JWT 驗證模板
- `JwtValidationChain`: JWT 驗證鏈

### 策略 Bean
- `LoginStrategyRegistry`: 登錄策略註冊表
- `JwtTokenStrategyRegistry`: JWT Token 策略註冊表
- `CallbackLoginTemplate`: 回調登錄模板

### 控制器 Bean
- `AuthorizationController`: 授權控制器

### JWT Bean
- `JwtTokenUtil`: JWT 工具類

## 條件配置

所有 Bean 都使用了 `@ConditionalOnMissingBean` 註解，這意味著：
- 如果您已經定義了相同類型的 Bean，自動配置不會覆蓋您的配置
- 您可以選擇性地覆蓋任何組件來實現自定義行為

## 故障排除

### 常見問題

1. **Bean 沒有被註冊**
   - 檢查是否正確引入了依賴
   - 確認配置屬性是否正確設置
   - 查看日誌中的自動配置信息

2. **OAuth2 認證失敗**
   - 檢查 OAuth2 提供者配置是否正確
   - 確認回調 URI 是否匹配
   - 檢查客戶端 ID 和密鑰是否正確

3. **JWT 驗證失敗**
   - 檢查 JWT 密鑰配置
   - 確認 JWT 過期時間設置
   - 檢查 JWT 簽發者和受眾配置

### 調試技巧

1. 啟用調試日誌：
```properties
logging.level.com.collaboportal.common.oauth2=DEBUG
logging.level.com.collaboportal.common.spring.oauth2=DEBUG
```

2. 檢查自動配置報告：
```properties
debug=true
```

## 進階配置

### 多環境配置

使用 Spring Profile 來管理不同環境的配置：

```yaml
---
spring:
  profiles: development
oauth2:
  providers:
    dev:
      client-id: dev-client-id
      # 其他開發環境配置

---
spring:
  profiles: production
oauth2:
  providers:
    prod:
      client-id: prod-client-id
      # 其他生產環境配置
```

### 自定義自動配置

如果需要額外的自定義配置，可以創建自己的自動配置類：

```java
@AutoConfiguration
@AutoConfigureAfter(OAuth2CoreAutoConfiguration.class)
public class MyCustomOAuth2Configuration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyCustomComponent myCustomComponent() {
        return new MyCustomComponent();
    }
}
```

## 版本兼容性

- Spring Boot: 2.7.x+
- Java: 11+
- Maven: 3.6.x+

## 更新日誌

### v1.0.0
- 初始版本
- 完整的 OAuth2 自動配置支持
- JWT 集成
- 多提供者支持
- 靈活的策略模式實現 