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
 * カスタムエラーコントローラークラス
 * アプリケーション全体のエラーハンドリングを管理する
 */
@RestController
@Component
public class CustomErrorController implements ErrorController {

    // ロガー
    Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    /**
     * コンストラクタ
     * 初期化時にログを出力
     */
    public CustomErrorController(){
        logger.debug("=== CustomErrorController が正常に初期化されました ===");
        logger.debug("=== ErrorController Bean が登録されました ===");
    }

    /**
     * エラーハンドリングメソッド
     * @param request HTTPリクエストオブジェクト
     * @return ErrorResponseBody エラーレスポンスボディ
     */
    @RequestMapping("/error")
    public ErrorResponseBody handleError(HttpServletRequest request) {
        logger.info("=== CustomErrorController.handleError が呼び出されました ===");
        // リクエストからステータスコードと例外を取得
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    
        // ステータスコードが存在する場合
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
    
            // 404エラーの場合
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                logger.info("リソースが見つかりません。ステータスコード: {}, 例外: {}", statusCode, exception);
                ErrorResponseBody errorResponseBody = new ErrorResponseBody("404", Message.W404, Message.ERROR_LEVEL_WARNING);
                return errorResponseBody;
            } 
            // 403エラーの場合
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                logger.warn("アクセスが拒否されました。例外: {}", exception);
                ErrorResponseBody errorResponseBody = new ErrorResponseBody("403", Message.W403, Message.ERROR_LEVEL_WARNING);
                return errorResponseBody;
            } 
            // 500エラーの場合
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                logger.error("システムエラーが発生しました。例外: {}", exception);
                ErrorResponseBody errorResponseBody = new ErrorResponseBody("500", Message.W500, Message.ERROR_LEVEL_WARNING);
                return errorResponseBody;
            }
        }
    
        // その他のエラーの場合
        logger.error("不明なエラーが発生しました。例外: {}", exception);
        ErrorResponseBody errorResponseBody = new ErrorResponseBody("500", Message.W500, Message.ERROR_LEVEL_WARNING);
        return errorResponseBody;
    }
    
}
