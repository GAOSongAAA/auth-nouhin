# OAuth2 與 JWT 模塊解耦架構指南

## 🎯 架構概覽

本解耦方案通過引入**全局策略註冊中心**，實現了 OAuth2 和 JWT 模塊的完全解耦，並將 prod 策略與 OAuth2 進行了深度集成。

### 核心組件

```
┌─────────────────────────────────────────────────────────────┐
│                   全局策略註冊中心                              │
│        (GlobalAuthenticationStrategyRegistry)                │
├─────────────────┬───────────────────────┬─────────────────────┤
│   JWT 模塊       │      OAuth2 模塊      │    其他認證模塊       │
│                │                      │                    │
│ • test策略      │ • OAuth2增強prod策略   │ • 自定義策略           │
│ • 基礎prod策略   │ • 提供商特定策略       │ • 第三方策略           │
└─────────────────┴───────────────────────┴─────────────────────┘
```

## 🔧 主要改進

### 1. **完全解耦**
- ❌ **移除**: OAuth2模塊對JWT模塊的直接依賴
- ✅ **新增**: 通過全局策略註冊中心進行交互
- ✅ **效果**: 兩個模塊可以獨立開發、測試、部署

### 2. **增強的prod策略**
- 🚀 **原prod策略**: 基礎OAuth認證
- 🌟 **新增強策略**: 智能多提供商OAuth2認證
  - 支持動態提供商選擇
  - 內建故障轉移機制
  - 支持路徑、域名、參數等多種路由策略

### 3. **靈活的策略管理**
- 📝 **動態註冊**: 運行時註冊/移除策略
- 🔄 **策略覆蓋**: OAuth2模塊可以覆蓋預設的prod策略
- 🏷️ **多重映射**: 同一策略可以註冊多個別名

## ⚙️ 配置說明

### 1. **OAuth2策略配置**

```yaml
# application.yml
oauth2:
  strategy:
    # 是否用OAuth2策略替換默認的prod策略
    replace-prod: true
    # 是否註冊OAuth2專用策略 (oauth2, oauth2-prod)
    register-oauth2-specific: true
  
  # OAuth2提供商配置
  providers:
    google:
      client-id: your-google-client-id
      client-secret: your-google-client-secret
      authorization-uri: https://accounts.google.com/o/oauth2/auth
      token-uri: https://oauth2.googleapis.com/token
      user-info-uri: https://www.googleapis.com/oauth2/v2/userinfo
      redirect-uri: http://localhost:8080/auth/callback
      scope: openid email profile
      path-patterns:
        - "/google/**"
        - "/auth/google/**"
    
    github:
      client-id: your-github-client-id
      client-secret: your-github-client-secret
      authorization-uri: https://github.com/login/oauth/authorize
      token-uri: https://github.com/login/oauth/access_token
      user-info-uri: https://api.github.com/user
      redirect-uri: http://localhost:8080/auth/callback
      scope: user:email
      path-patterns:
        - "/github/**"
        - "/auth/github/**"
```

### 2. **環境變數配置**

```bash
# 環境標識 (0=test, 1=prod)
ENV_FLAG=1

# OAuth2策略配置
OAUTH2_STRATEGY_REPLACE_PROD=true
OAUTH2_STRATEGY_REGISTER_OAUTH2_SPECIFIC=true

# 預設提供商
APP_DEFAULT_PROVIDER=google
```

## 🚀 使用方式

### 1. **自動策略選擇** (推薦)
```
# 系統根據環境自動選擇策略
GET /auth/callback?code=xxx&state=yyy

# ENV_FLAG=0 → 使用test策略
# ENV_FLAG=1 → 使用增強的OAuth2 prod策略
```

### 2. **明確指定策略**
```java
@Autowired
private AuthenticationStrategyRegistry globalRegistry;

// 使用OAuth2策略
LoginStrategy oauth2Strategy = globalRegistry.getStrategy("oauth2");
oauth2Strategy.login(authContext);

// 使用特定提供商策略
LoginStrategy googleStrategy = globalRegistry.getStrategy("google");
googleStrategy.login(authContext);
```

### 3. **多種觸發方式**

#### a) **路徑匹配觸發**
```
GET /google/login        → 自動選擇Google OAuth2
GET /github/dashboard    → 自動選擇GitHub OAuth2
GET /auth/microsoft/api  → 自動選擇Microsoft OAuth2
```

#### b) **參數指定觸發**
```
GET /auth/login?provider=google   → 使用Google OAuth2
GET /auth/login?provider=github   → 使用GitHub OAuth2
```

#### c) **域名路由觸發**
```yaml
oauth2:
  domain-mappings:
    google: app-google.example.com
    github: app-github.example.com
```

## 🛡️ 故障轉移機制

### 自動降級流程
```
1. 嘗試OAuth2提供商選擇
   ↓ (失敗)
2. 使用預設提供商
   ↓ (失敗)
3. 重定向到錯誤頁面
   ↓ (可選)
4. 降級到基礎認證策略
```

### 配置故障轉移
```java
@Component
public class CustomOAuth2Strategy extends EnhancedOAuth2ProdStrategy {
    
    @Override
    protected void handleProviderSelectionFailure(OAuth2ProviderContext context) {
        // 自定義故障處理邏輯
        if (shouldFallbackToBasicAuth()) {
            // 降級到基礎認證
            fallbackToBasicAuthentication(context);
        } else {
            super.handleProviderSelectionFailure(context);
        }
    }
}
```

## 🔍 監控和調試

### 1. **策略統計信息**
```java
@Autowired
private GlobalAuthenticationStrategyRegistry globalRegistry;

// 獲取統計信息
Map<String, Object> stats = globalRegistry.getStrategyStatistics();
logger.info("策略統計: {}", stats);

// 輸出示例:
// {
//   "totalStrategies": 6,
//   "registeredKeys": ["test", "prod", "oauth2", "google", "github"],
//   "pluginMapping": {"prod": "OAuth2Module", "test": "JWTModule"}
// }
```

### 2. **日誌監控**
```
# 開啟詳細日誌
logging:
  level:
    com.collaboportal.common.strategy: DEBUG
    com.collaboportal.common.oauth2: DEBUG
```

### 3. **健康檢查端點**
```java
@RestController
public class AuthStrategyHealthController {
    
    @GetMapping("/health/auth-strategies")
    public ResponseEntity<Map<String, Object>> checkAuthStrategies() {
        // 檢查所有策略的健康狀態
        return ResponseEntity.ok(strategyHealthCheck());
    }
}
```

## 🔄 升級指南

### 從舊架構升級

1. **更新依賴**
   ```xml
   <!-- 移除 -->
   <dependency>
       <groupId>com.collaboportal</groupId>
       <artifactId>common-jwt</artifactId>
   </dependency>
   
   <!-- 保留公共接口 -->
   <dependency>
       <groupId>com.collaboportal</groupId>
       <artifactId>common-component</artifactId>
   </dependency>
   ```

2. **更新配置**
   ```yaml
   # 新增OAuth2配置
   oauth2:
     strategy:
       replace-prod: true
     providers:
       # ... 提供商配置
   ```

3. **驗證功能**
   ```bash
   # 測試test策略
   curl "http://localhost:8080/auth/callback?email=test@example.com"
   
   # 測試OAuth2策略
   curl "http://localhost:8080/auth/login?provider=google"
   ```

## 🎉 優勢總結

### ✅ **解耦效果**
- 模塊間無直接依賴
- 可以獨立開發和部署
- 更好的測試隔離

### ✅ **功能增強**
- prod策略集成完整OAuth2功能
- 支持多提供商智能選擇
- 內建故障轉移機制

### ✅ **靈活性提升**
- 動態策略管理
- 配置化路由選擇
- 可擴展的認證架構

### ✅ **維護便利**
- 清晰的職責分離
- 統一的策略管理
- 便於監控和調試

---

## 📞 支援

如有任何問題或需要進一步的自定義，請參考：
- 策略接口文檔: `LoginStrategy.java`
- 全局註冊中心: `GlobalAuthenticationStrategyRegistry.java`
- OAuth2增強策略: `EnhancedOAuth2ProdStrategy.java` 