package com.collaboportal.common.advise;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.common.exception.ForbiddenException;

import com.collaboportal.common.model.ErrorResponseBody;
import com.collaboportal.common.utils.Message;

import java.nio.file.AccessDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ===== TODO:これでハンドリングできないエラーは追加するなりエラーメッセージの修正を行う =====
@RestControllerAdvice
public class RestExceptionHandler{

    Logger logger = LoggerFactory.getLogger(getClass());
    
    // カスタムエラーのハンドリング
    // レコード0件エラー
    @ExceptionHandler(CommonException.class)
    public ErrorResponseBody handleRecordNotFoundException(CommonException e) {
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(Integer.toString(HttpStatus.NOT_FOUND.value()), Message.W404, Message.ERROR_LEVEL_WARNING);
        return errorResponseBody;
    }
    
    // カスタムエラーのハンドリング
    // 認可エラー
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponseBody handleForbiddenException(ForbiddenException e) {
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(Integer.toString(HttpStatus.FORBIDDEN.value()), Message.W403, Message.ERROR_LEVEL_WARNING);
        return errorResponseBody;
    }
    // クエリパラメータのバリデーションエラーのハンドリング
    @ExceptionHandler(BindException.class)
    public ErrorResponseBody handleBindException(BindException ex) {
        logger.info("===== Bind Exception =====: {}: {}", ex.getClass().getName(), ex.getMessage(), ex);
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(Integer.toString(HttpStatus.BAD_REQUEST.value()), Message.W400, Message.ERROR_LEVEL_WARNING);
        return errorResponseBody;
    }
    // DB排他エラーのハンドリング
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ErrorResponseBody handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        logger.error("===== System Error =====: {}: {}", e.getClass().getName(), e.getMessage(), e);
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(Integer.toString(HttpStatus.CONFLICT.value()), Message.W409, Message.ERROR_LEVEL_ERROR);
        return errorResponseBody;
    }
    // 認証エラーのハンドリング
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponseBody handleAuthorizationErrorException(AuthenticationException e) {
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(Integer.toString(HttpStatus.UNAUTHORIZED.value()), Message.W401, Message.ERROR_LEVEL_WARNING);
        return errorResponseBody;
    }
    
    @ExceptionHandler(Exception.class)
    public ErrorResponseBody handleGenericException(Exception e) {
        logger.error("===== System Error (Generic) =====: {}", getStackTrace(e));

        if (e instanceof AuthenticationException) {
            return new ErrorResponseBody(
                Integer.toString(HttpStatus.UNAUTHORIZED.value()), 
                e.getMessage(),  
                Message.ERROR_LEVEL_ERROR
            );
        } else if (e instanceof DataAccessException) {
            logger.error("===== Database Error =====: {}", e.getMessage());
            return new ErrorResponseBody(
                Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), 
                "データベース処理中にエラーが発生しました",  
                Message.ERROR_LEVEL_ERROR
            );
        } else if (e instanceof AccessDeniedException) {
            logger.warn("===== Access Denied =====: {}", e.getMessage());
            return new ErrorResponseBody(
                Integer.toString(HttpStatus.FORBIDDEN.value()), 
                "アクセスが拒否されました",  
                Message.ERROR_LEVEL_WARNING
            );
        } else if (e.getCause() instanceof IllegalArgumentException) {
            logger.warn("===== Bad Request Error =====: {}", e.getMessage());
            return new ErrorResponseBody(
                Integer.toString(HttpStatus.BAD_REQUEST.value()), 
                "リクエストのパラメータが無効です",  
                Message.ERROR_LEVEL_WARNING
            );
        } 

        return new ErrorResponseBody(
            Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), 
            Message.W500, 
            Message.ERROR_LEVEL_ERROR
        );
    }


    private static String getStackTrace(Throwable ex){
        StackTraceElement[] list = ex.getStackTrace();
        StringBuilder b = new StringBuilder();
        b.append(ex.getClass()).append(":").append(ex.getMessage()).append("\n");
        for( StackTraceElement s : list ) {
            b.append(s.toString()).append("\n");
        }
        return b.toString();
    }

}
