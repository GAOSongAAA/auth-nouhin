package com.collaboportal.shoho.model;

import java.util.List;

import com.collaboportal.common.model.BaseResponseBody;
import com.collaboportal.shoho.entity.PrescriptionListSearch;
import com.collaboportal.shoho.entity.PrescriptionListSearchResult;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 処方元情報一覧検索のレスポンスモデルクラスです.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionListSearchResponseBody extends BaseResponseBody {

	/** 総件数 */
	private Integer all_su;

	/** 開始行 */
	private Integer sta_row;

	/** 終了行 */
	private Integer end_row;

	/**
	 * 
	 * 処方医入力否数フラグ
	 * <ul>
	 * <li>{@code 0}:処方医が入力不可の企画が存在しない.（表示：処方医）</li>
	 * <li>{@code 1}:処方医が入力不可の企画が存在する.（表示：備考）</li>
	 * <li>{@code 2}:デフォルト.（表示：変更なし）</li>
	 * </ul>
	 */
	private int shm_dr_input_disabled_flg;

	/** 処方元情報 */
	private List<PrescriptionListSearch> shm_inf;

	/**
	 * エンティティからモデルを生成します.
	 * 
	 * @param param  パラメータ
	 * @param offset オフセット
	 * @param limit  リミット
	 * @return {@link PrescriptionListSearchResponseBody} モデル
	 */
	public static PrescriptionListSearchResponseBody of(
			PrescriptionListSearchResult param,
			Integer offset,
			Integer limit) {
		int allCount = param.getShm_inf().get(0).getAll_su();

		return new PrescriptionListSearchResponseBody(
				allCount,
				offset,
				offset + limit - 1 >= allCount
						? allCount
						: offset + limit - 1,
				param.getShm_dr_input_disabled_flg(),
				param.getShm_inf());
	}
}
