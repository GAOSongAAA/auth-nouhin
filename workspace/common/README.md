# CollabPortal Common Components

CollabPortal 通用組件庫，提供 JWT、OAuth2、Spring Boot 自動配置等功能。

## 📋 目錄結構

```
common/
├── common-dependencies/     # 依賴管理
├── common-bom/             # BOM 依賴
├── common-component/       # 核心組件
├── common-plugins/         # 插件模組
│   ├── common-jwt/         # JWT 組件
│   └── common-oauth2/      # OAuth2 組件
└── common-starter/         # Spring Boot Starter
    ├── common-spring-boot-autoconfig/    # 自動配置
    └── common-spring-boot-starter/       # Starter
```

## 🚀 快速開始

### 1. 引入 BOM 依賴

在你的專案根 `pom.xml` 中添加：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.collaboportal</groupId>
            <artifactId>common-bom</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 使用 Spring Boot Starter

添加 starter 依賴：

```xml
<dependency>
    <groupId>com.collaboportal</groupId>
    <artifactId>common-spring-boot-starter</artifactId>
</dependency>
```

### 3. 使用特定組件

```xml
<!-- 僅使用 JWT 組件 -->
<dependency>
    <groupId>com.collaboportal</groupId>
    <artifactId>common-jwt</artifactId>
</dependency>

<!-- 僅使用 OAuth2 組件 -->
<dependency>
    <groupId>com.collaboportal</groupId>
    <artifactId>common-oauth2</artifactId>
</dependency>

<!-- 僅使用核心組件 -->
<dependency>
    <groupId>com.collaboportal</groupId>
    <artifactId>common-component</artifactId>
</dependency>
```

## 🏗️ 構建專案

### 本地構建

```bash
# 進入專案目錄
cd common

# 編譯安裝到本地倉庫
mvn clean install

# 僅編譯不執行測試
mvn clean install -DskipTests
```

### 生成扁平化 POM

```bash
# 生成扁平化 POM（解決 ${revision} 變數）
mvn flatten:flatten

# 清理扁平化 POM
mvn flatten:clean
```

## 📦 部署到 Maven 倉庫

### 1. 配置 Maven Settings

在 `~/.m2/settings.xml` 中配置：

```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>你的用戶名</username>
            <password>你的密碼</password>
        </server>
    </servers>
    
    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.passphrase>你的GPG密碼</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

### 2. 部署 SNAPSHOT 版本

```bash
# 部署 SNAPSHOT 版本到 Sonatype
mvn clean deploy
```

### 3. 發佈正式版本

```bash
# 使用 release profile 部署
mvn clean deploy -P release

# 或者分步驟執行
mvn clean compile
mvn source:jar-no-fork
mvn javadoc:jar
mvn gpg:sign-and-deploy-file
```

### 4. 本地私服部署

如果你有本地 Nexus 私服：

```xml
<!-- 在 pom.xml 中修改 distributionManagement -->
<distributionManagement>
    <repository>
        <id>releases</id>
        <url>http://your-nexus-server:8081/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>snapshots</id>
        <url>http://your-nexus-server:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

## 🔧 版本管理

### 修改版本號

```bash
# 修改版本號（會自動更新所有模組）
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT

# 提交版本號變更
mvn versions:commit

# 回滾版本號變更
mvn versions:revert
```

### 發佈版本流程

1. **準備發佈**
   ```bash
   mvn versions:set -DnewVersion=1.0.0
   mvn versions:commit
   ```

2. **提交代碼**
   ```bash
   git add .
   git commit -m "Release version 1.0.0"
   git tag v1.0.0
   ```

3. **部署到倉庫**
   ```bash
   mvn clean deploy -P release
   ```

4. **準備下一個開發版本**
   ```bash
   mvn versions:set -DnewVersion=1.1.0-SNAPSHOT
   mvn versions:commit
   git add .
   git commit -m "Prepare for next development iteration"
   git push origin main --tags
   ```

## 📋 組件說明

### common-component
核心通用組件，包含：
- 通用異常處理
- 日誌配置
- 工具類
- 基礎註解

### common-jwt
JWT 相關功能：
- JWT 生成和驗證
- JWT 工具類
- 基於 JJWT 和 Hutool

### common-oauth2
OAuth2 相關功能：
- OAuth2 客戶端配置
- 權限驗證
- 用戶信息獲取

### common-spring-boot-starter
Spring Boot 啟動器：
- 自動配置所有組件
- 零配置快速開始
- 條件化裝配

## 🔧 配置參數

### JWT 配置

```yaml
collaboportal:
  jwt:
    secret: your-secret-key
    expiration: 86400
    refresh-expiration: 604800
```

### OAuth2 配置

```yaml
collaboportal:
  oauth2:
    client-id: your-client-id
    client-secret: your-client-secret
    redirect-uri: http://localhost:8080/callback
```

## 🤝 貢獻指南

1. Fork 本倉庫
2. 創建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交變更 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 創建 Pull Request

## 📄 許可證

本專案採用 MIT 許可證 - 詳見 [LICENSE](LICENSE) 文件。

## 🆘 支援

如果你遇到問題或有建議：

1. 檢查 [Issues](https://github.com/collaboportal/common/issues) 中是否已有相關問題
2. 創建新的 Issue 詳細描述問題
3. 聯繫開發團隊：dev@collaboportal.com

## 🗺️ 路線圖

- [ ] 添加緩存組件
- [ ] 支援多數據源
- [ ] 添加監控指標
- [ ] 國際化支援
- [ ] 微服務組件

---

**Happy Coding! 🚀** 