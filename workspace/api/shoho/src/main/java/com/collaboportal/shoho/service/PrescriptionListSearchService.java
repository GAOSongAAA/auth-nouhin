package com.collaboportal.shoho.service;

import com.collaboportal.shoho.entity.PrescriptionListSearchResult;
import com.collaboportal.shoho.model.PrescriptionListSearchRequestParameter;

/**
 * 一覧検索のServiceインターフェースです.
 */
public interface PrescriptionListSearchService {

    /**
     * 処方元情報を参照し、対象データを取得します.
     *
     * @param param パラメータ
     *
     * @return {@link PrescriptionListSearchResult} 結果
     */
    PrescriptionListSearchResult refer(PrescriptionListSearchRequestParameter param);
}
