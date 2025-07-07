package com.collaboportal.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内部エラーコードを定義する列挙型
 * 各エラーコードはエラーメッセージとエラーIDを持つ
 */
@AllArgsConstructor
public enum InternalErrorCode {
    // 未定義エラー
    UNDEINED_ERROR("Undefined", "ERROR_ID_000"),
    // 認証証明書エラー
    ATTESTATION_CERTIFICATE_ERROR("NotAuthenticated", "ERROR_ID_001"),
    // バリデーションエラー
    VALIDATION_ERROR("ValidationException", "ERROR_ID_002"),
    // レコード未検出エラー
    RECORD_NOT_FOUND_ERROR("RecordNotFoundException", "ERROR_ID_003"),
    // 楽観的ロック失敗エラー
    OPTIMISTIC_LOCKING_FAILURE_ERROR("OptimisticLockingFailureException", "ERROR_ID_004"),
    // システムエラー
    SYSTEM_ERROR("SystemException", "ERROR_ID_100"),
    // 認可エラー
    AUTHORIZATION_ERROR("AuthorizationException", "ERROR_ID_101");

    // エラーメッセージ
    @Getter
    private final String errorMessage;
    
    // エラーID
    @Getter
    private final String errorId;
}
