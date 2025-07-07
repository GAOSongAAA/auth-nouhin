package com.collaboportal.shoho.service;

import java.util.List;

import com.collaboportal.shoho.model.DcfCodeRequestParameter;

public interface DcfCodeService {
    /**
     * DCFコード一覧取得メソッド
     * 
     * @param param DCFコード一覧取得パラメータ
     * @return DCFコード一覧のリスト
     */
    List<String> getDcfCode(DcfCodeRequestParameter param);
}
