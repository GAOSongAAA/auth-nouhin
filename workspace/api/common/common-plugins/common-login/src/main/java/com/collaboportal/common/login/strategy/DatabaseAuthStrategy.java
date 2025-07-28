package com.collaboportal.common.login.strategy;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.utils.Message;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 資料庫認證策略實作類
 * 
 * 此類實作了AuthenticationStrategy介面，負責處理基於資料庫的使用者認證流程。
 * 主要功能包括：
 * 1. 檢查和驗證現有的JWT認證令牌
 * 2. 刷新即將過期的有效令牌
 * 3. 處理過期或無效令牌的重定向到登入頁面
 * 4. 提供使用者資訊的存儲和管理
 * 
 * 認證流程：
 * - 首先檢查Cookie中是否存在認證令牌
 * - 如果令牌存在且有效，則刷新令牌並繼續
 * - 如果令牌不存在、過期或無效，則重定向到登入頁面
 */
@Component("databaseAuthStrategy")
public class DatabaseAuthStrategy implements AuthorizationStrategy {

    /**
     * 日誌記錄器，用於記錄認證過程中的各種狀態和錯誤資訊
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthStrategy.class);
    
    /**
     * JWT令牌工具類，用於令牌的生成、驗證和操作
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 構造函數
     * 通過依賴注入接收JWT令牌工具類實例
     * 
     * @param jwtTokenUtil JWT令牌工具類，用於處理令牌相關操作
     */
    public DatabaseAuthStrategy(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 執行資料庫認證邏輯
     * 
     * 此方法是認證策略的核心實作，按照以下步驟執行：
     * 1. 從請求的Cookie中提取認證令牌
     * 2. 檢查令牌是否存在
     * 3. 如果令牌存在，驗證其有效性和過期狀態
     * 4. 對於有效令牌，提取使用者資訊並刷新令牌
     * 5. 對於無效或不存在的令牌，重定向到登入頁面
     * 
     * @param request  基礎請求物件，包含HTTP請求的相關資訊
     * @param response 基礎回應物件，用於設定HTTP回應的相關資訊
     * @throws AuthenticationException 當認證過程中發生錯誤時拋出
     */
    @Override
    public void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException {
        logger.debug("開始執行資料庫認證策略...");

        // 從Cookie中獲取認證令牌
        // 使用預定義的Cookie名稱來查找認證令牌
        String token = request.getCookieValue(Message.Cookie.AUTH);

        // 檢查令牌是否存在
        // 如果令牌不存在或為空字串，表示使用者尚未登入或登入狀態已失效
        if (token == null || token.isEmpty()) {
            response.redirect("http://localhost:8080/login.html");
            return;
        }

        try {
            // 驗證令牌是否已過期
            // JWT令牌包含過期時間資訊，此處檢查當前時間是否超過令牌的有效期
            if (jwtTokenUtil.isTokenExpired(token)) {
                response.redirect("http://localhost:8080/login.html");
                return;
            }

            // 從JWT令牌中提取使用者資訊
            // JWT令牌的payload中包含使用者的相關資訊，如使用者ID、角色等
            Map<String, String> userInfo = JwtTokenUtil.getItemsJwtToken(token);
            
            // 檢查使用者資訊是否為空
            // 即使令牌有效，如果不包含使用者資訊也視為認證失敗
            if (userInfo.isEmpty()) {
                response.redirect("http://localhost:8080/login.html");
                return;
            }
            
            // 將使用者資訊存儲到請求上下文中
            // 這允許後續的處理邏輯訪問當前認證使用者的資訊
            CommonHolder.getStorage().set("USER_INFO", userInfo);
            logger.debug("使用者資訊已解析並存儲到請求上下文中：{}", userInfo.get("sub"));

            // 刷新令牌的過期時間
            // 這有助於保持使用者的登入狀態，避免頻繁重新登入
            String refreshedToken = jwtTokenUtil.updateExpiresAuthToken(token);
            
            // 將刷新後的令牌設定回Cookie
            // 參數說明：令牌值、路徑、域名、最大存活時間
            response.addCookie(Message.Cookie.AUTH, refreshedToken, "/", null, -1);
            logger.debug("認證令牌已成功刷新並設定到回應Cookie中。");

            // 認證成功，記錄成功訊息
            logger.info("使用者資料庫認證成功：{}。", userInfo.get("sub"));

        } catch (ExpiredJwtException e) {
            // 捕捉JWT過期異常
            // 這是正常的業務流程，當令牌過期時需要重新登入
            logger.info("認證令牌已過期（捕捉異常）。重定向到登入頁面。");
            response.redirect("http://localhost:8080/login.html");
            return;
        } catch (Exception e) {
            // 捕捉其他可能的異常（如令牌格式錯誤、簽名無效等）
            // 記錄錯誤並重定向到登入頁面
            logger.error("資料庫令牌驗證過程中發生錯誤。重定向到登入頁面。", e);
            response.redirect("http://localhost:8080/login.html");
            return;
        }
    }
}
