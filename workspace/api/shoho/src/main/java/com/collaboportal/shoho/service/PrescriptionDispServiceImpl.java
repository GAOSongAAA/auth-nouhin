package com.collaboportal.shoho.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.shoho.entity.PrescriptionDispResult;
import com.collaboportal.shoho.model.PrescriptionDispRequestParameter;
import com.collaboportal.shoho.repository.PrescriptionDispMapper;

import lombok.RequiredArgsConstructor;

/**
 * 処方元情報の詳細画面初期表示のService実装クラスです.
 */
@Service
@RequiredArgsConstructor
public class PrescriptionDispServiceImpl implements PrescriptionDispService {

    /** マッパー */
    private final PrescriptionDispMapper prescriptionDispMapper;

    /**
     * {@inheritdoc}
     */
    @Override
    public PrescriptionDispResult refer(PrescriptionDispRequestParameter param) {

        // 詳細情報取得
        // 取得が１件もないなら例外をスロー
        return Optional.ofNullable(prescriptionDispMapper.refer(param.getParam()))
                .orElseThrow(() -> new CommonException(InternalErrorCode.RECORD_NOT_FOUND_ERROR));
    }
}
