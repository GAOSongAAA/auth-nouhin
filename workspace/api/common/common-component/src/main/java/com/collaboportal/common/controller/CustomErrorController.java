package com.collaboportal.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.model.ErrorResponseBody;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 自訂錯誤控制器類別
 * 管理整個應用程式的錯誤處理
 */
@RestController
@Component
public class CustomErrorController implements ErrorController {

    // 記錄器
    Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    /**
     * 建構函式
     * 初始化時輸出日誌
     */
    public CustomErrorController(){
        logger.debug("=== CustomErrorController 已正常初始化 ===");
        logger.debug("=== ErrorController Bean 已註冊 ===");
    }

    /**
     * 錯誤處理方法
     * @param request HTTP請求物件
     * @return ErrorResponseBody 錯誤回應主體
     */
    @RequestMapping("/error")
    public ErrorResponseBody handleError(HttpServletRequest request) {
        logger.info("=== CustomErrorController.handleError 已被呼叫 ===");
        // 從請求中取得狀態代碼和例外
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    
        // 若狀態代碼存在
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
    
            // 404錯誤的情況
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                String exceptionMessage = (exception != null) ? exception.getMessage() : "未知錯誤";
                logger.info("找不到資源。狀態代碼: {}, 例外: {}", statusCode, exceptionMessage);
                ErrorResponseBody errorResponseBody = new ErrorResponseBody("404", Message.W404, Message.ERROR_LEVEL_WARNING);
                return errorResponseBody;
            } 
            // 403錯誤的情況
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                String exceptionMessage = (exception != null) ? exception.getMessage() : "未知錯誤";
                logger.warn("存取被拒絕。例外: {}", exceptionMessage);
                ErrorResponseBody errorResponseBody = new ErrorResponseBody("403", Message.W403, Message.ERROR_LEVEL_WARNING);
                return errorResponseBody;
            } 
            // 500錯誤的情況
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                String exceptionMessage = (exception != null) ? exception.getMessage() : "未知錯誤";
                logger.error("發生系統錯誤。例外: {}", exceptionMessage);
                ErrorResponseBody errorResponseBody = new ErrorResponseBody("500", Message.W500, Message.ERROR_LEVEL_WARNING);
                return errorResponseBody;
            }
        }
    
        // 其他錯誤的情況
        String exceptionMessage = (exception != null) ? exception.getMessage() : "未知錯誤";
        logger.error("發生未知錯誤。例外: {}", exceptionMessage);
        ErrorResponseBody errorResponseBody = new ErrorResponseBody("500", Message.W500, Message.ERROR_LEVEL_WARNING);
        return errorResponseBody;
    }
    
}
