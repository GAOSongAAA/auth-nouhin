package com.collaboportal.shoho.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * 処方元情報一覧検索の結果を格納します.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionListSearchResult implements Serializable {

    /**
     * 処方医入力否数フラグ
     * <ul>
     * <li>{@code 0}:処方医が入力不可の企画が存在しない.（表示：処方医）</li>
     * <li>{@code 1}:処方医が入力不可の企画が存在する.（表示：備考）</li>
     * <li>{@code 2}:デフォルト.（表示：変更なし）</li>
     * </ul>
     */
    private int shm_dr_input_disabled_flg = 2;

    /** 処方元情報 */
    private List<PrescriptionListSearch> shm_inf;

}
