package com.collaboportal.shoho.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.shoho.entity.PrescriptionListSearch;
import com.collaboportal.shoho.entity.PrescriptionListSearchResult;
import com.collaboportal.shoho.model.PrescriptionListSearchRequestParameter;
import com.collaboportal.shoho.repository.PrescriptionListSearchMapper;

import lombok.RequiredArgsConstructor;

/**
 * 処方元情報一覧検索のServiceクラスです.
 */
@Service
@RequiredArgsConstructor
public class PrescriptionListSearchServiceImpl implements PrescriptionListSearchService {

    /**
     * マッパー
     */
    private final PrescriptionListSearchMapper prescriptionListSearchMapper;

    /**
     * 処方元情報を参照し、対象データを取得します.
     *
     * @param param パラメータ
     *
     * @return {@link PrescriptionListSearchResult} 結果
     */
    @Override
    public PrescriptionListSearchResult refer(PrescriptionListSearchRequestParameter param) {

        // マッパー呼び出し
        List<PrescriptionListSearch> results = Optional.ofNullable(prescriptionListSearchMapper.refer(param))
                .orElse(new ArrayList<>());

        // 取得0件ならエラー
        if (results.isEmpty())
            throw new CommonException(InternalErrorCode.RECORD_NOT_FOUND_ERROR);

        // フラグ設定が必要ならフラグを取得
        int flag = param.shouldFetchFlag()
                ? prescriptionListSearchMapper.fetchFlag(param)
                : 2;

        return new PrescriptionListSearchResult(
                flag,
                results);
    }
}
