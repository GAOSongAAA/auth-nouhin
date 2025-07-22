package com.collaboportal.common.exception;

import com.collaboportal.common.error.InternalErrorCode;

/**
 * 共通例外クラス
 * アプリケーション全体で使用されるカスタム例外
 * 内部エラーコードとエラーメッセージを保持する
 */
public class CommonException extends RuntimeException{

    // 内部エラーコード
    private final InternalErrorCode internalErrorCode;

    /**
     * コンストラクタ
     * @param internalErrorCode 内部エラーコード
     */
    public CommonException(InternalErrorCode internalErrorCode){
        super(internalErrorCode.getErrorMessage());
        this.internalErrorCode = internalErrorCode;
    }

   
}
