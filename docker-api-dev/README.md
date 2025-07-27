# Nginx 網關配置

這個項目配置了 nginx 作為 API 網關，用於代理請求到後端 Spring 服務。

## 配置說明

### 服務架構
- **nginx**: 網關服務，監聽 80 端口
- **shoho-spring**: 後端 Spring 服務，運行在 8080 端口

### 主要功能

1. **反向代理**: 將所有請求轉發到 shoho-spring 服務
2. **請求頭處理**: 自動添加 `Authorization-Type: Bearer` 請求頭
3. **安全頭部**: 添加各種安全相關的 HTTP 頭部
4. **Gzip 壓縮**: 啟用響應壓縮以提高性能
5. **健康檢查**: 提供 `/health` 端點用於健康檢查

### 請求頭配置

nginx 會自動為每個轉發的請求添加以下請求頭：

- `Host`: 原始主機名
- `X-Real-IP`: 客戶端真實 IP
- `X-Forwarded-For`: 轉發鏈路信息
- `X-Forwarded-Proto`: 原始協議
- `Authorization-Type`: 固定值 "Bearer"

## 使用方法

### 啟動服務
```bash
docker-compose up -d
```

### 訪問服務
- 網關地址: `http://localhost`
- 健康檢查: `http://localhost/health`

### 停止服務
```bash
docker-compose down
```

## 配置自定義

### 修改 Authorization-Type 值
編輯 `nginx.conf` 文件中的以下行：
```nginx
proxy_set_header Authorization-Type "Bearer";
```

### 添加更多請求頭
在 `nginx.conf` 的 location 塊中添加：
```nginx
proxy_set_header Your-Custom-Header "value";
```

### 修改端口
在 `docker-compose.yml` 中修改 nginx 服務的端口映射：
```yaml
ports:
  - "8080:80"  # 將外部端口改為 8080
```

## 日誌查看

查看 nginx 日誌：
```bash
docker-compose logs nginx
```

查看 Spring 服務日誌：
```bash
docker-compose logs shoho-spring
``` 