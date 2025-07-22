package com.collaboportal.shoho.model;

import com.collaboportal.common.model.BaseResponseBody;
import com.collaboportal.shoho.entity.PrescriptionDispResult;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 処方元情報の詳細画面初期表示のレスポンスModelクラスです.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDispResponseBody extends BaseResponseBody {

    /** 企画名 */
    private String pre_kkk_nm;

    /** 納品先 */
    private String pre_nhn_nm;

    /** 得意先コード */
    private String pre_tok_cod;

    /** 伝票日付 */
    private String pre_dnp_ymd;

    /** 伝票No */
    private String pre_dnp_no;

    /** 商品名/商品/規格容量 */
    private String pre_syh_kkk_yry;

    /** 商品コード */
    private String pre_syh_cod;

    /** 数量 */
    private String pre_hnb_num;

    /** DCFコード */
    private String pre_dcf_cod;

    /** 処方元名 */
    private String pre_nm;

    /** 正式処方元名 */
    private String pre_ful_nm;

    /** 処方元不明コード */
    private int pre_cod;

    /** 不明理由 */
    private String pre_ryu;

    /** 処方医 */
    private String pre_dr;

    /** 処方医不明コード */
    private int pre_dr_cod;

    /** 処方医入力可否フラグ */
    private int pre_dr_input_flg;

    /** 診療科 */
    private String pre_dp;

    /** 診療科不明コード */
    private int pre_dp_cod;

    /** MR連絡事項 */
    private String pre_mr_ren;

    /** 状態 */
    private int pre_stat_flg;

    /**
     * エンティティからレスポンスモデルを生成します.
     * 
     * @param entity エンティティ
     * 
     * @return {@link PrescriptionDispResponseBody}
     */
    public static PrescriptionDispResponseBody of(PrescriptionDispResult entity) {

        return new PrescriptionDispResponseBody(
                entity.getPre_kkk_nm(), // 企画名
                entity.getPre_nhn_nm(), // 納品先
                entity.getPre_tok_cod(), // 得意先コード
                entity.getPre_dnp_ymd(), // 伝票日付
                entity.getPre_dnp_no(), // 伝票No
                entity.getPre_syh_kkk_yry(), // 商品名/商品/規格容量
                entity.getPre_syh_cod(), // 商品コード
                entity.getPre_hnb_num(), // 数量
                entity.getPre_dcf_cod(), // DCFコード
                entity.getPre_nm(), // 処方元名
                entity.getPre_ful_nm(), // 正式処方元名
                entity.getPre_cod(), // 処方元不明コード
                entity.getPre_ryu(), // 不明理由
                entity.getPre_dr(), // 処方医
                entity.getPre_dr_cod(), // 処方医不明コード
                entity.getPre_dr_input_flg(), // 処方医入力可否フラグ
                entity.getPre_dp(), // 診療科
                entity.getPre_dp_cod(), // 診療科不明コード
                entity.getPre_mr_ren(), // MR連絡事項
                entity.getPre_stat_flg() // 状態
        );
    }
}