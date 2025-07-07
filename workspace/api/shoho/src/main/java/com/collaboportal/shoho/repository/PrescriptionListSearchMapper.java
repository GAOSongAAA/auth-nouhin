package com.collaboportal.shoho.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.collaboportal.shoho.entity.PrescriptionListSearch;
import com.collaboportal.shoho.entity.PrescriptionListSearchResult;
import com.collaboportal.shoho.model.PrescriptionListSearchRequestParameter;

/**
 * 処方元情報一覧検索のMapperクラスです.
 */
@Mapper
public interface PrescriptionListSearchMapper {

	/**
	 * 処方元情報を参照し、対象データを取得します.
	 *
	 * @param param パラメータ
	 *
	 * @return {@link PrescriptionListSearchResult} 結果
	 */
	List<PrescriptionListSearch> refer(PrescriptionListSearchRequestParameter param);

	/**
	 * 処方医入力否数フラグを取得します.
	 * 
	 * @param param パラメータ
	 * @return 結果
	 *         <ul>
	 *         <li>{@code 1}:処方医が入力不可の企画が存在する.</li>
	 *         <li>{@code 0}:処方医が入力不可の企画が存在しない.</li>
	 *         </ul>
	 */
	int fetchFlag(PrescriptionListSearchRequestParameter param);
}
