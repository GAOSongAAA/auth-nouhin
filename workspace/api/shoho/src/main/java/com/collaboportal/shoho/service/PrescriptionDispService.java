package com.collaboportal.shoho.service;

import com.collaboportal.shoho.entity.PrescriptionDispResult;
import com.collaboportal.shoho.model.PrescriptionDispRequestParameter;

/**
 * 処方元情報の詳細画面初期表示のServiceインターフェースです.
 */
public interface PrescriptionDispService {

	/**
	 * 処方元情報の詳細画面初期表示データを取得します.
	 * 
	 * @param param パラメータ
	 * 
	 * @return {@link PrescriptionDispResult} 取得結果
	 */
	PrescriptionDispResult refer(PrescriptionDispRequestParameter param);
}
