# IP地址限制功能使用說明

## 概述

本功能允許您在Controller的方法或類上使用 `@IpRestricted` 注解來限制只有特定IP地址才能存取的API。支援多種IP格式包括單一IP、IP範圍和CIDR格式。

## 功能特點

- ✅ 支援方法級別和類級別的IP限制
- ✅ 支援多種IP格式：單一IP、IP範圍、CIDR、萬用字符
- ✅ 可設定自定義錯誤訊息
- ✅ 可動態啟用/停用IP檢查
- ✅ 自動處理代理服務器和負載均衡器的真實IP
- ✅ 詳細的日誌記錄

## 快速開始

### 1. 基本使用

```java
@RestController
public class MyController {
    
    // 限制單一IP存取
    @GetMapping("/admin/users")
    @IpRestricted(allowedIps = {"192.168.1.100"})
    public List<User> getUsers() {
        // ...
    }
}
```

### 2. 支援的IP格式

```java
@IpRestricted(allowedIps = {
    "127.0.0.1",                    // 單一IP
    "192.168.1.0/24",              // CIDR格式（子網路）
    "10.0.0.1-10.0.0.100",         // IP範圍
    "192.168.1.*"                  // 萬用字符（注意：僅支援最後一個段）
})
```

### 3. 類級別限制

```java
@RestController
@IpRestricted(allowedIps = {"192.168.1.0/24"}, message = "僅限內網存取")
public class AdminController {
    
    // 整個Controller的所有方法都會被IP限制
    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }
    
    // 方法級別的注解會覆蓋類級別的設定
    @GetMapping("/admin/public")
    @IpRestricted(allowedIps = {"0.0.0.0/0"})  // 允許所有IP
    public String publicEndpoint() {
        return "public-info";
    }
}
```

## 進階配置

### 1. 自定義錯誤訊息

```java
@IpRestricted(
    allowedIps = {"192.168.1.0/24"},
    message = "此功能僅限辦公室網路使用，請聯繫IT部門"
)
```

### 2. 動態啟用/停用

```java
@IpRestricted(
    allowedIps = {"192.168.1.100"},
    enabled = false  // 臨時停用IP檢查
)
```

### 3. 生產/測試環境配置

```java
@IpRestricted(
    allowedIps = {
        "127.0.0.1",           // 本地開發
        "192.168.1.0/24",      // 辦公室網路
        "10.0.0.0/8"           // VPN網路
    },
    message = "此API僅限內部網路存取"
)
```

## 實際應用場景

### 1. 管理員API保護

```java
@RestController
@RequestMapping("/admin")
@IpRestricted(
    allowedIps = {
        "192.168.1.100",       // 管理員工作站
        "10.0.0.0/8"           // VPN網路
    },
    message = "管理員功能僅限指定IP存取"
)
public class AdminController {
    
    @PostMapping("/users/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        // 停用用戶功能
        return ResponseEntity.ok().build();
    }
}
```

### 2. 內部API限制

```java
@RestController
@RequestMapping("/internal")
public class InternalApiController {
    
    @PostMapping("/sync-data")
    @IpRestricted(
        allowedIps = {
            "192.168.100.10",      // 數據同步服務器
            "192.168.100.11"       // 備用同步服務器
        },
        message = "數據同步API僅限指定服務器存取"
    )
    public ResponseEntity<String> syncData(@RequestBody SyncRequest request) {
        // 數據同步邏輯
        return ResponseEntity.ok("同步完成");
    }
}
```

### 3. 開發/測試環境限制

```java
@RestController
@RequestMapping("/debug")
@IpRestricted(
    allowedIps = {"192.168.1.0/24"},  // 開發部門網路
    enabled = false  // 在生產環境中設為 false
)
public class DebugController {
    
    @GetMapping("/health-check")
    public Map<String, Object> healthCheck() {
        return Map.of(
            "status", "healthy",
            "timestamp", Instant.now()
        );
    }
}
```

## 錯誤處理

當IP驗證失敗時，系統會：

1. 返回HTTP 403 Forbidden狀態碼
2. 返回JSON格式的錯誤訊息：

```json
{
  "error": "IP_ACCESS_DENIED",
  "message": "存取被拒絕：IP地址不在允許範圍內",
  "clientIp": "203.0.113.1",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 注意事項

### 1. IP地址獲取優先級

系統會依照以下順序嘗試獲取真實的客戶端IP：
1. X-Forwarded-For
2. Proxy-Client-IP
3. WL-Proxy-Client-IP
4. HTTP_X_FORWARDED_FOR
5. 其他代理相關標頭
6. request.getRemoteAddr()

### 2. 效能考量

- IP驗證在每個請求時執行，建議IP列表不要過長
- CIDR格式比IP範圍格式效能更好
- 建議在負載均衡器層面也設置IP限制作為第一道防線

### 3. 安全建議

- 定期檢查和更新允許的IP列表
- 在生產環境中避免使用萬用字符格式
- 結合其他認證機制使用，不應僅依賴IP限制
- 監控IP驗證失敗的日誌以發現潛在攻擊

## 日誌監控

系統會記錄以下重要事件：

```log
INFO  - 開始進行方法級別的IP驗證，目標: AdminController.getUsers，客戶端IP: 192.168.1.100
INFO  - 方法級別IP驗證通過，目標: AdminController.getUsers，客戶端IP [192.168.1.100] 在允許範圍內
WARN  - 方法級別IP驗證失敗，目標: AdminController.getUsers，客戶端IP [203.0.113.1] 不在允許範圍內
```

建議配置日誌監控系統來追蹤IP驗證失敗的情況。

## 配置排除路徑

如果需要排除某些路徑的IP檢查，可以修改 `IpRestrictionConfig` 類：

```java
@Configuration
public class IpRestrictionConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipRestrictionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/public/**",      // 公開API
                    "/health/**",      // 健康檢查
                    "/actuator/**"     // Spring Boot Actuator
                );
    }
}
``` 